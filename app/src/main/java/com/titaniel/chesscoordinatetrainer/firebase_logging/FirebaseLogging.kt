package com.titaniel.chesscoordinatetrainer.firebase_logging

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.titaniel.chesscoordinatetrainer.BuildConfig
import com.titaniel.chesscoordinatetrainer.ui.board.ChessColor
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseLogging @Inject constructor(@ApplicationContext context: Context) {

    companion object {

        private const val EVENT_TILE_CLICKED_CORRECTLY = "tile_clicked_correctly"
        private const val EVENT_TILE_CLICKED_INCORRECTLY = "tile_clicked_incorrectly"
        private const val EVENT_ROTATION_CHANGE_TO_BLACK = "rotation_change_to_black"
        private const val EVENT_ROTATION_CHANGE_TO_WHITE = "rotation_change_to_white"
        private const val EVENT_COORDINATE_RULERS_VISIBLE = "coordinate_rulers_visible"
        private const val EVENT_COORDINATE_RULERS_INVISIBLE = "coordinate_rulers_invisible"
        private const val EVENT_PIECES_VISIBLE = "pieces_visible"
        private const val EVENT_PIECES_INVISIBLE = "pieces_invisible"
        private const val EVENT_FEEDBACK_DIALOG_OPEN = "feedback_dialog_open"
        private const val EVENT_FEEDBACK_DIALOG_SEND = "feedback_dialog_send"
        private const val EVENT_FEEDBACK_DIALOG_DISMISS = "feedback_dialog_dismiss"
        private const val PARAM_NOTATION = "notation"
        private const val PARAM_MESSAGE = "message"
        private const val EVENT_TRIGGER_INTERSTITIAL = "trigger_interstitial"
        private const val EVENT_PURCHASE_NO_ADS_CLICK = "purchase_no_ads_click"

    }

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    init {
        firebaseAnalytics.setAnalyticsCollectionEnabled(BuildConfig.DEBUG.not())
    }

    fun logTriggerInterstitial() {
        firebaseAnalytics.logEvent(EVENT_TRIGGER_INTERSTITIAL, null)
    }

    fun logPurchaseNoAdsClick() {
        firebaseAnalytics.logEvent(EVENT_PURCHASE_NO_ADS_CLICK, null)
    }

    fun logTileClicked(notation: String, correct: Boolean) {
        firebaseAnalytics.logEvent(if (correct) EVENT_TILE_CLICKED_CORRECTLY else EVENT_TILE_CLICKED_INCORRECTLY) {
            param(PARAM_NOTATION, notation)
        }
    }

    fun logRotationChange(newFront: ChessColor) {
        firebaseAnalytics.logEvent(
            when (newFront) {
                ChessColor.BLACK -> EVENT_ROTATION_CHANGE_TO_BLACK
                ChessColor.WHITE -> EVENT_ROTATION_CHANGE_TO_WHITE
            }, null
        )
    }

    fun logCoordinateRulersVisibilityChange(visible: Boolean) {
        firebaseAnalytics.logEvent(
            if (visible) EVENT_COORDINATE_RULERS_VISIBLE else EVENT_COORDINATE_RULERS_INVISIBLE,
            null
        )
    }

    fun logPiecesVisibilityChange(visible: Boolean) {
        firebaseAnalytics.logEvent(
            if (visible) EVENT_PIECES_VISIBLE else EVENT_PIECES_INVISIBLE, null
        )
    }

    fun logFeedbackDialogOpen() {
        firebaseAnalytics.logEvent(EVENT_FEEDBACK_DIALOG_OPEN, null)
    }

    fun logFeedbackDialogSend(message: String) {
        firebaseAnalytics.logEvent(EVENT_FEEDBACK_DIALOG_SEND) {
            param(PARAM_MESSAGE, message)
        }
    }

    fun logFeedbackDialogDismiss() {
        firebaseAnalytics.logEvent(EVENT_FEEDBACK_DIALOG_DISMISS, null)
    }

}