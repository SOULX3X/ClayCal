package com.example.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.example.model.CalendarEvent
import java.util.Calendar

object EventNotificationScheduler {

    const val CHANNEL_ID = "claycal_event_reminders"
    const val CHANNEL_NAME = "Event Reminders"
    const val CHANNEL_DESCRIPTION = "Alerts and reminders for your scheduled calendar events"

    private const val TAG = "EventNotificationScheduler"

    /**
     * Ensures the notification channel exists on API 26+.
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .build()

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                lightColor = 0xFFFF7A59.toInt()
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
                setSound(soundUri, audioAttributes)
                setShowBadge(true)
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    /**
     * Calculates the millisecond epoch timestamp at which the reminder alarm should fire.
     */
    fun calculateTriggerMillis(event: CalendarEvent): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, event.year)
            set(Calendar.MONTH, event.month - 1) // Calendar months are 0-based
            set(Calendar.DAY_OF_MONTH, event.day)
            set(Calendar.HOUR_OF_DAY, event.startHour)
            set(Calendar.MINUTE, event.startMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val eventStartMillis = calendar.timeInMillis
        val offsetMillis = event.reminderMinutesBefore * 60 * 1000L
        return eventStartMillis - offsetMillis
    }

    /**
     * Schedules a notification alarm for the given event.
     */
    fun scheduleNotification(context: Context, event: CalendarEvent) {
        createNotificationChannel(context)

        // If reminder is disabled (-1) or event is already completed, cancel existing alarm
        if (event.reminderMinutesBefore < 0 || event.isCompleted) {
            cancelNotification(context, event.id)
            return
        }

        val triggerMillis = calculateTriggerMillis(event)
        val now = System.currentTimeMillis()

        if (triggerMillis <= now) {
            Log.d(TAG, "Event trigger time already passed for event id: ${event.id}")
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            ?: return

        val intent = Intent(context, EventNotificationReceiver::class.java).apply {
            action = EventNotificationReceiver.ACTION_TRIGGER_EVENT_NOTIFICATION
            putExtra(EventNotificationReceiver.EXTRA_EVENT_ID, event.id)
            putExtra(EventNotificationReceiver.EXTRA_EVENT_TITLE, event.title)
            putExtra(EventNotificationReceiver.EXTRA_EVENT_CATEGORY, event.category)
            putExtra(EventNotificationReceiver.EXTRA_EVENT_TIME, event.timeRangeFormatted)
            putExtra(EventNotificationReceiver.EXTRA_EVENT_LOCATION, event.location)
            putExtra(EventNotificationReceiver.EXTRA_EVENT_MINUTES_BEFORE, event.reminderMinutesBefore)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            event.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            }
            Log.d(TAG, "Successfully scheduled alarm for event '${event.title}' at $triggerMillis (in ${(triggerMillis - now) / 1000}s)")
        } catch (e: SecurityException) {
            Log.w(TAG, "SecurityException scheduling exact alarm, falling back to setAndAllowWhileIdle", e)
            try {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            } catch (fallbackError: Exception) {
                Log.e(TAG, "Failed to schedule reminder alarm", fallbackError)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error scheduling alarm", e)
        }
    }

    /**
     * Cancels any scheduled alarm and dismisses any active notification for this event id.
     */
    fun cancelNotification(context: Context, eventId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val intent = Intent(context, EventNotificationReceiver::class.java).apply {
            action = EventNotificationReceiver.ACTION_TRIGGER_EVENT_NOTIFICATION
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }

        // Also cancel visible notification if currently posted
        try {
            NotificationManagerCompat.from(context).cancel(eventId.toInt())
        } catch (e: Exception) {
            Log.w(TAG, "Failed to dismiss notification", e)
        }
    }

    /**
     * Triggers an immediate test notification to verify sounds, styling, and vibration.
     */
    fun sendTestNotification(context: Context) {
        createNotificationChannel(context)
        EventNotificationReceiver.postNotification(
            context = context,
            eventId = 999999L,
            title = "Claycal Reminder Test ✨",
            category = "Personal",
            timeRange = "Starts soon",
            location = "Your Calendar",
            minutesBefore = 15
        )
    }
}
