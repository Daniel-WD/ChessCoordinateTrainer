package com.titaniel.chesscoordinatetrainer.no_ads

import com.android.billingclient.api.*
import com.titaniel.chesscoordinatetrainer.billing.BillingClientProvider
import com.titaniel.chesscoordinatetrainer.billing.acknowledgeIfNotAlready
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoAdsInteractor @Inject constructor(private val billingProvider: BillingClientProvider) {

    companion object {
        const val NO_ADS_PRODUCT_ID = "no_ads" // todo in strings
    }

    val productDetails = channelFlow {
        billingProvider.billingClient.collectLatest {
            it?.let { client ->
                val params = QueryProductDetailsParams.newBuilder()
                params.setProductList(
                    listOf(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(NO_ADS_PRODUCT_ID)
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
                    )
                )
                val productDetailsResult = withContext(Dispatchers.IO) { client.queryProductDetails(params.build()) }
                send(productDetailsResult.productDetailsList?.getOrNull(0))
            } ?: send(null)
        }
    }.stateIn(billingProvider.billingScope, SharingStarted.Lazily, null)

    val isPurchased = channelFlow {
        suspend fun BillingClient.handlePurchasedItems(
            billingResult: BillingResult,
            purchases: List<Purchase>,
        ) {
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val noAdsPurchase =
                    purchases.find { purchase -> purchase.products.contains(NO_ADS_PRODUCT_ID) }
                val acknowledged = noAdsPurchase?.acknowledgeIfNotAlready(this)
                send(acknowledged ?: false)
            }
        }

        launch {
            billingProvider.purchasesUpdatedFlow.collectLatest { (billingResult, purchases) ->
                purchases?.let { billingProvider.billingClient.value?.handlePurchasedItems(billingResult, it) }
            }
        }
        billingProvider.billingClient.collectLatest {
            it?.let { billingClient ->
                val params = QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.INAPP)
                val (billingResult, purchases) = billingClient.queryPurchasesAsync(params.build())
                billingClient.handlePurchasedItems(billingResult, purchases)
            }
        }
    }.stateIn(billingProvider.billingScope, SharingStarted.Eagerly, false)

}