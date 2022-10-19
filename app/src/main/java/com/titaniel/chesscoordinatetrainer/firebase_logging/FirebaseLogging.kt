package com.titaniel.chesscoordinatetrainer.firebase_logging

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.titaniel.chesscoordinatetrainer.ui.board.ChessColor
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseLogging @Inject constructor(@ApplicationContext context: Context) {

    companion object {

        /**
         * Event to track correct tile clicked events
         */
        private const val EVENT_TILE_CLICKED_CORRECTLY = "tile_clicked_correctly"

        /**
         * Event to track incorrect tile clicked events
         */
        private const val EVENT_TILE_CLICKED_INCORRECTLY = "tile_clicked_incorrectly"

        /**
         * Event to track rotation change to black events
         */
        private const val EVENT_ROTATION_CHANGE_TO_BLACK = "rotation_change_to_black"

        /**
         * Event to track rotation change to white events
         */
        private const val EVENT_ROTATION_CHANGE_TO_WHITE = "rotation_change_to_white"

        /**
         * Event to track change of coordinate rulers to visible
         */
        private const val EVENT_COORDINATE_RULERS_VISIBLE = "coordinate_rulers_visible"

        /**
         * Event to track change of coordinate rulers to invisible
         */
        private const val EVENT_COORDINATE_RULERS_INVISIBLE = "coordinate_rulers_invisible"

        private const val EVENT_PIECES_VISIBLE = "pieces_visible"
        private const val EVENT_PIECES_INVISIBLE = "pieces_invisible"

        /**
         * Event to track feedback dialog open events
         */
        private const val EVENT_FEEDBACK_DIALOG_OPEN = "feedback_dialog_open"

        /**
         * Event to track feedback dialog send events
         */
        private const val EVENT_FEEDBACK_DIALOG_SEND = "feedback_dialog_send"

        /**
         * Event to track feedback dialog dismiss events
         */
        private const val EVENT_FEEDBACK_DIALOG_DISMISS = "feedback_dialog_dismiss"

        /**
         * Notation parameter for [EVENT_TILE_CLICKED_CORRECTLY]
         */
        private const val PARAM_NOTATION = "notation"

        /**
         * Parameter for [EVENT_FEEDBACK_DIALOG_SEND], containing the message to send
         */
        private const val PARAM_MESSAGE = "message"

    }

    /**
     * Instance of firebase analytics
     */
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    /**
     * Logs that tile was clicked at [notation] and if it was the [correct] one.
     */
    fun logTileClicked(notation: String, correct: Boolean) {

        // Log tile clicked event
        firebaseAnalytics.logEvent(if (correct) EVENT_TILE_CLICKED_CORRECTLY else EVENT_TILE_CLICKED_INCORRECTLY) {

            // Add parameters
            param(PARAM_NOTATION, notation)

        }

    }

    /**
     * Logs rotation change to [newFront].
     */
    fun logRotationChange(newFront: ChessColor) {

        // Log rotation event
        firebaseAnalytics.logEvent(
            when (newFront) {
                ChessColor.BLACK -> EVENT_ROTATION_CHANGE_TO_BLACK
                ChessColor.WHITE -> EVENT_ROTATION_CHANGE_TO_WHITE
            }, null
        )

    }

    /**
     * Logs if coordinate rulers are [visible].
     */
    fun logCoordinateRulersVisibilityChange(visible: Boolean) {

        // Log coords rulers visibility event
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

    /**
     * Logs open action of feedback dialog.
     */
    fun logFeedbackDialogOpen() {

        // Log feedback dialog open event
        firebaseAnalytics.logEvent(EVENT_FEEDBACK_DIALOG_OPEN, null)

    }

    /**
     * Logs send [message] action of feedback dialog.
     */
    fun logFeedbackDialogSend(message: String) {

        // Log feedback dialog send event
        firebaseAnalytics.logEvent(EVENT_FEEDBACK_DIALOG_SEND) {

            // Add parameters
            param(PARAM_MESSAGE, message)

        }
    }

    /**
     * Logs when feedback dialog gets hidden
     */
    fun logFeedbackDialogDismiss() {

        // Log feedback dialog cancel event
        firebaseAnalytics.logEvent(EVENT_FEEDBACK_DIALOG_DISMISS, null)

    }

}