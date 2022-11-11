package com.titaniel.chesscoordinatetrainer.feedback

import android.content.Context
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedbackManager @Inject constructor(@ApplicationContext val context: Context) {

    fun send(feedback: String) {

        if (feedback.isBlank()) {
            return
        }

        val workData = workDataOf(FeedbackWorker.KEY_FEEDBACK_MSG to feedback)

        val workConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<FeedbackWorker>()
            .setConstraints(workConstraints)
            .setInputData(workData).build()

        WorkManager.getInstance(context).enqueue(workRequest)

    }

}