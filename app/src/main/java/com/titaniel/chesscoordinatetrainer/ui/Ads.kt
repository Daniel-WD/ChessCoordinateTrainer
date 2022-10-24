package com.titaniel.chesscoordinatetrainer.ui

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first

@Composable
fun BannerAd(modifier: Modifier, id: String, adSize: AdSize = AdSize.FULL_BANNER) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(adSize)
                adUnitId = id
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

@Composable
fun InterstitialAd(
    interstitialAd: InterstitialAd,
    presentInterstitial: (InterstitialAd) -> Unit,
    onShow: () -> Unit
) {
    LaunchedEffect(key1 = true) {
        interstitialAd.let(presentInterstitial)
        onShow()
    }
}

@Composable
fun InterstitialAd(id: String, show: Boolean, present: (InterstitialAd) -> Unit, onShow: () -> Unit) {
    val context = LocalContext.current
    var ad by remember { mutableStateOf<InterstitialAd?>(null) }
    val adsFlow = remember { interstitialFlow(context, id) }
    if (show) {
        LaunchedEffect(key1 = true) {
            ad?.let(present)
            ad = null
            onShow()
        }
    } else {
        LaunchedEffect(key1 = true) {
            ad ?: run { ad = adsFlow.first() }
        }
    }
}

fun interstitialFlow(context: Context, adId: String) = callbackFlow {
    val adRequest = AdRequest.Builder().build()
    InterstitialAd.load(
        context,
        adId,
        adRequest,
        object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                trySend(null)
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                trySend(interstitialAd)
            }
        })
    awaitClose {  }
}