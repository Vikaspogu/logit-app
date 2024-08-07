package com.vikaspogu.logit.worker

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vikaspogu.logit.R
import com.vikaspogu.logit.ui.util.Constants.CHANNEL_ID
import com.vikaspogu.logit.ui.util.Constants.NOTIFICATION_ID
import com.vikaspogu.logit.ui.util.Constants.NOTIFICATION_TITLE
import com.vikaspogu.logit.ui.util.Constants.VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
import com.vikaspogu.logit.ui.util.Constants.VERBOSE_NOTIFICATION_CHANNEL_NAME

class ReminderWorker(context: Context,workerParameters: WorkerParameters): CoroutineWorker(context,workerParameters) {
    override suspend fun doWork(): Result {
        makeReminderNotification(context = applicationContext)
        return Result.success()
    }

}

@SuppressLint("MissingPermission")
fun makeReminderNotification(context: Context){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(
            CHANNEL_ID,
            VERBOSE_NOTIFICATION_CHANNEL_NAME,
            importance
        )
        channel.description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(NOTIFICATION_TITLE)
        .setContentText(context.getString(R.string.reminder_message))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVibrate(LongArray(0))
        .setAutoCancel(true)

    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
}