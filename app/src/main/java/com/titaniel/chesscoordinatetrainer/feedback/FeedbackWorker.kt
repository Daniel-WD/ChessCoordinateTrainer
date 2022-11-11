package com.titaniel.chesscoordinatetrainer.feedback

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.titaniel.chesscoordinatetrainer.feedback.FeedbackWorker.Companion.KEY_FEEDBACK_MSG
import java.util.*

class FeedbackWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    companion object {

        const val KEY_FEEDBACK_MSG = "KEY_FEEDBACK_MSG"

        private const val FEEDBACK_PATH = "feedback"

    }

    override fun doWork(): Result {

        val feedback = inputData.getString(KEY_FEEDBACK_MSG)
        if (feedback.isNullOrBlank()) {
            return Result.failure()
        }

        val feedbackRef = Firebase.database.getReference("${FEEDBACK_PATH}/${UUID.randomUUID()}")

        feedbackRef.setValue(feedback)

        return Result.success()
    }

}