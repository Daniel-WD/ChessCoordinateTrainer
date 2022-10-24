package com.titaniel.chesscoordinatetrainer.no_ads

import com.android.billingclient.api.*
import com.titaniel.chesscoordinatetrainer.billing.BillingProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoAdsInteractor @Inject constructor(private val billingInteractor: BillingProvider) {

    val noAdsProductDetails = channelFlow {
        billingInteractor.billingClient.collectLatest {
            it?.let { client ->
                val params = QueryProductDetailsParams.newBuilder()
                params.setProductList(
                    listOf(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId("no_ads")
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
                    )
                )

                val productDetailsResult = withContext(Dispatchers.IO) {
                    client.queryProductDetails(params.build())
                }

                send(productDetailsResult.productDetailsList?.get(0))
            } ?: send(null)
        }
    }//.stateIn(GlobalScope, SharingStarted.WhileSubscribed(), null)

}