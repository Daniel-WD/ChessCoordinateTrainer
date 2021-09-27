package com.titaniel.chesscoordinatetrainer.feedback

import android.content.Context
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for anything related to feedback
 */
@Singleton
class FeedbackManager @Inject constructor(@ApplicationContext val context: Context) {

    /**
     * Start work request for sending [feedback] to firebase realtime db.
     */
    fun send(feedback: String) {

        // Return if feedback is blank
        if (feedback.isBlank()) {
            return
        }

        // Create work data
        val workData = workDataOf(FeedbackWorker.KEY_FEEDBACK_MSG to feedback)

        // Create work constraints
        val workConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Create work request
        val workRequest = OneTimeWorkRequestBuilder<FeedbackWorker>()
            .setConstraints(workConstraints)
            .setInputData(workData).build()

        // Enqueue work request
        WorkManager.getInstance(context).enqueue(workRequest)

    }

}