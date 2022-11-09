package com.titaniel.chesscoordinatetrainer.billing

import android.content.Context
import com.android.billingclient.api.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingClientProvider @Inject constructor(@ApplicationContext context: Context) {

    private val billingScope = CoroutineScope(Dispatchers.Default)

    data class PurchasesUpdatedEvent(
        val billingResult: BillingResult,
        val purchases: List<Purchase>?
    )

    private val _purchasesUpdatedFlow = MutableSharedFlow<PurchasesUpdatedEvent>()
    val purchasesUpdatedFlow = _purchasesUpdatedFlow.asSharedFlow()

    val billingClient = callbackFlow {

        val client = BillingClient.newBuilder(context)
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
                    trySend(client)
                }
            }

            override fun onBillingServiceDisconnected() {
                trySend(null)
            }
        }

        client.startConnection(billingClientStateListener)

        awaitClose { client.endConnection() }
    }.stateIn(billingScope, SharingStarted.Eagerly, null)

}