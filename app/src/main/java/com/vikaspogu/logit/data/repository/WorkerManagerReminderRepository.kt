package com.vikaspogu.logit.data.repository

import android.content.Context
import android.icu.util.Calendar
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.vikaspogu.logit.worker.ReminderWorker
import java.util.Locale
import java.util.concurrent.TimeUnit

class WorkerManagerReminderRepository(context: Context) {
    private val workManager = WorkManager.getInstance(context)

    fun scheduleReminder(reminderDays: Set<String>, reminderTime: Long) {
        val calendar = Calendar.getInstance()
        val (hour, min) = getHourAndMinuteFromMillis(reminderTime)
        reminderDays.forEach { day ->
            calendar.set(Calendar.DAY_OF_WEEK, getDayIndex(day)!!)
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, min)
            calendar.set(Calendar.SECOND, 0)

            val initialDelay = calendar.timeInMillis - System.currentTimeMillis()
            val periodicWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(7, TimeUnit.DAYS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build()

            workManager.enqueue(periodicWorkRequest)
        }
    }

    private fun getDayIndex(dayName: String): Int? {
        return when (dayName.lowercase(Locale.ROOT)) {
            "sunday" -> Calendar.SUNDAY
            "monday" -> Calendar.MONDAY
            "tuesday" -> Calendar.TUESDAY
            "wednesday" -> Calendar.WEDNESDAY
            "thursday" -> Calendar.THURSDAY
            "friday" -> Calendar.FRIDAY
            "saturday" -> Calendar.SATURDAY
            else -> null
        }
    }

    private fun getHourAndMinuteFromMillis(millis: Long): Pair<Int, Int> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = millis
        }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return Pair(hour, minute)
    }
}