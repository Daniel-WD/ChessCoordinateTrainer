package com.titaniel.chesscoordinatetrainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.titaniel.chesscoordinatetrainer.billing.BillingProvider
import com.titaniel.chesscoordinatetrainer.ui.screens.TrainerWrapper
import com.titaniel.chesscoordinatetrainer.ui.theme.ChessCoordinateTrainerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var billingProvider: BillingProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
        setContent {
            ChessCoordinateTrainerTheme {
                Surface(color = MaterialTheme.colors.background) {
                    SetSystemUi()
                    TrainerWrapper(
                        presentInterstitial = ::presentInterstitial,
                        startPurchaseFlow = ::purchaseProduct
                    )
                }
            }
        }
    }

    private fun presentInterstitial(ad: InterstitialAd) = ad.show(this)

    private fun purchaseProduct(productDetails: ProductDetails): BillingResult? {
        return billingProvider.billingClient.value?.let {
            val productDetailsParamsList = listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails).build()
            )

            val billingFlowParams =
                BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParamsList)
                    .build()

            it.launchBillingFlow(this, billingFlowParams)
        }
    }

}

@Composable
fun SetSystemUi() {

    // Get system ui controller
    val systemUiController = rememberSystemUiController()

    systemUiController.isSystemBarsVisible = false

//    val statusBarColor = if(MaterialTheme.colors.isLight) Color(0xFFECECEC) else Color(0xFF1A1A1A)
//
//    val backgroundColor = MaterialTheme.colors.background
//
//    SideEffect {
//
//        // Set system bar color
//        systemUiController.setSystemBarsColor(color = statusBarColor)
//
//        // Set navigation bar color
//        systemUiController.setNavigationBarColor(color = backgroundColor)
//    }

}