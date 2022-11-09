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
        private const val failureAdCountKey = "FailureAdCount"
    }

    private val remoteConfig = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings { minimumFetchIntervalInSeconds = 3600 }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }

    val failureAdCount by lazy { remoteConfig.getLong(failureAdCountKey) }

}