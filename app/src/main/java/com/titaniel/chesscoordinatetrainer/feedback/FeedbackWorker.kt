package com.titaniel.chesscoordinatetrainer.feedback

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.titaniel.chesscoordinatetrainer.feedback.FeedbackWorker.Companion.KEY_FEEDBACK_MSG
import java.util.*

/**
 * Worker for sending feedback, provided by [KEY_FEEDBACK_MSG], to firebase realtime db.
 */
class FeedbackWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    companion object {

        /**
         * Key for feedback message
         */
        const val KEY_FEEDBACK_MSG = "KEY_FEEDBACK_MSG"

        /**
         * Path to feedback data in firebase realtime database
         */
        private const val FEEDBACK_PATH = "feedback"

    }

    override fun doWork(): Result {

        // Get feedback
        val feedback = inputData.getString(KEY_FEEDBACK_MSG)

        // If feedback msg invalid...
        if (feedback.isNullOrBlank()) {

            // Return failure
            return Result.failure()
        }

        // Create reference to save feedback in
        val feedbackRef = Firebase.database.getReference("${FEEDBACK_PATH}/${UUID.randomUUID()}")

        // Set feedback
        feedbackRef.setValue(feedback)

        // Return success result
        return Result.success()
    }

}