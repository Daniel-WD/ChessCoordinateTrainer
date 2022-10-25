package com.titaniel.chesscoordinatetrainer.billing

import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.acknowledgePurchase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun Purchase.acknowledgeIfNotAlready(client: BillingClient) : Boolean {
    if (purchaseState == Purchase.PurchaseState.PURCHASED) {
        if (isAcknowledged) {
            return true
        } else {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchaseToken)
            val ackPurchaseResult = withContext(Dispatchers.IO) {
                client.acknowledgePurchase(
                    acknowledgePurchaseParams.build()
                )
            }
            if (ackPurchaseResult.responseCode == BillingClient.BillingResponseCode.OK) {
                return true
            }
        }
    }
    return false
}