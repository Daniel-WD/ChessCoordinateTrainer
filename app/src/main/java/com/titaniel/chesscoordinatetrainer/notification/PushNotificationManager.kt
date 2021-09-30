package com.titaniel.chesscoordinatetrainer.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.titaniel.chesscoordinatetrainer.MainActivity
import com.titaniel.chesscoordinatetrainer.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PushNotificationManager @Inject constructor(@ApplicationContext val context: Context) {

    companion object {

        /**
         * Channel id for used notification channel
         */
        const val NOTIFICATION_CHANNEL_ID = "123"

    }

    init {

        // Create the NotificationChannel for API 26+ because of NotificationChannel class
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Create channel
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "channel name",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "channel description"
            }

            // Get NotificationManager system service
            val notificationManager: NotificationManager = getSystemService(context, NotificationManager::class.java) as NotificationManager

            // Create channel with notification manager
            notificationManager.createNotificationChannel(channel)
        }

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun sendNotification() {

        // Create intent for MainActivity
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Create pending intent
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        // Create notification
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("title")
            .setContentText("this is the content text")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Show notification
        NotificationManagerCompat.from(context).notify(0, notification)

    }

}