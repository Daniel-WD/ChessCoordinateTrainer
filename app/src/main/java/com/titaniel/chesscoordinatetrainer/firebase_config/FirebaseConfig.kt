package com.titaniel.chesscoordinatetrainer.firebase_config

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.titaniel.chesscoordinatetrainer.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseConfig @Inject constructor() {

    companion object {
        private const val CORRECT_AD_THRESHOLD = "correct_ad_threshold"
        private const val INCORRECT_AD_THRESHOLD = "incorrect_ad_threshold"
        private const val SHOW_CORRECT_ADS = "show_correct_ads"
        private const val SHOW_INCORRECT_ADS = "show_incorrect_ads"
        private const val SHOW_NO_ADS_BUTTON = "show_no_ads_button"
    }

    private val remoteConfig = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings { minimumFetchIntervalInSeconds = 3600 }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }

    val correctAdThreshold by lazy { remoteConfig.getLong(CORRECT_AD_THRESHOLD) }
    val incorrectAddThreshold by lazy { remoteConfig.getLong(INCORRECT_AD_THRESHOLD) }
    val showCorrectAds by lazy { remoteConfig.getBoolean(SHOW_CORRECT_ADS) }
    val showIncorrectAds by lazy { remoteConfig.getBoolean(SHOW_INCORRECT_ADS) }
    val showNoAdsButton by lazy { remoteConfig.getBoolean(SHOW_NO_ADS_BUTTON) }

}