package com.vikaspogu.logit.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vikaspogu.logit.R
import com.vikaspogu.logit.ui.util.Constants.CHANNEL_ID
import com.vikaspogu.logit.ui.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.vikaspogu.logit.ui.util.Constants.NOTIFICATION_ID
import com.vikaspogu.logit.ui.util.Constants.NOTIFICATION_TITLE

class ReminderWorker(context: Context,workerParameters: WorkerParameters): CoroutineWorker(context,workerParameters) {
    override suspend fun doWork(): Result {
        sendNotification(context = applicationContext)
        return Result.success()
    }

}

private fun sendNotification(context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
    }

    val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle(NOTIFICATION_TITLE)
        .setContentText(context.getString(R.string.reminder_message))
        .setSmallIcon(R.drawable.ic_notification)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
}