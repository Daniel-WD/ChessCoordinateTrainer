package com.titaniel.chesscoordinatetrainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.titaniel.chesscoordinatetrainer.ui.screens.TrainerWrapper
import com.titaniel.chesscoordinatetrainer.ui.theme.ChessCoordinateTrainerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    var interAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
        setContent {
            ChessCoordinateTrainerTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                    SetSystemUi()

                    TrainerWrapper(showInterstitialAd = ::showInterstitial)
//                    ThankYou()

                }
            }
        }
    }

    fun showInterstitial(ad: InterstitialAd) = ad.show(this)

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