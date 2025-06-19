package com.st.demo.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.st.demo.R

object NotificationUtils {

    const val CHANNEL_ID = "activity_channel"
    const val NOTIFICATION_ID = 1001

    private var isChannelCreated = false

    fun createNotification(context: Context, activity: String): Notification {
        createChannel(context)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("GymTrackerAI")
            .setContentText("Activity: $activity")
            .setSmallIcon(R.drawable.ic_gym_notification)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    fun updateNotification(context: Context, activity: String) {
        val notification = createNotification(context, activity)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun createChannel(context: Context) {
        if (isChannelCreated) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Activity Recognition",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Activity recognition notifications"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        isChannelCreated = true
    }
}