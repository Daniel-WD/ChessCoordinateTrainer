package com.titaniel.chesscoordinatetrainer.billing

import android.content.Context
import com.android.billingclient.api.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class BillingProvider @Inject constructor(@ApplicationContext context: Context) {

    data class PurchasesUpdatedEvent(
        val billingResult: BillingResult,
        val purchases: List<Purchase>?
    )

    private val _purchasesUpdatedFlow = MutableSharedFlow<PurchasesUpdatedEvent>()
    val purchasesUpdatedFlow = _purchasesUpdatedFlow.asSharedFlow()

    val billingClient = callbackFlow {

        val billingClient = BillingClient.newBuilder(context)
            .setListener { billingResult, purchases ->
                launch {
                    _purchasesUpdatedFlow.emit(
                        PurchasesUpdatedEvent(billingResult, purchases)
                    )
                }
            }
            .enablePendingPurchases()
            .build()

        val billingClientStateListener = object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    trySend(billingClient)
                }
            }

            override fun onBillingServiceDisconnected() {
                trySend(null)
            }
        }

        billingClient.startConnection(billingClientStateListener)

        awaitClose { billingClient.endConnection() }
    }.stateIn(GlobalScope, SharingStarted.Eagerly, null)

}