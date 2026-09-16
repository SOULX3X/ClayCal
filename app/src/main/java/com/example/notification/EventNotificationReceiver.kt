package com.example.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.CalendarDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventNotificationReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_TRIGGER_EVENT_NOTIFICATION = "com.claycal.ACTION_TRIGGER_EVENT_NOTIFICATION"
        const val ACTION_MARK_COMPLETE = "com.claycal.ACTION_MARK_COMPLETE"
        const val ACTION_SNOOZE_10_MIN = "com.claycal.ACTION_SNOOZE_10_MIN"

        const val EXTRA_EVENT_ID = "extra_event_id"
        const val EXTRA_EVENT_TITLE = "extra_event_title"
        const val EXTRA_EVENT_CATEGORY = "extra_event_category"
        const val EXTRA_EVENT_TIME = "extra_event_time"
        const val EXTRA_EVENT_LOCATION = "extra_event_location"
        const val EXTRA_EVENT_MINUTES_BEFORE = "extra_event_minutes_before"

        private const val TAG = "EventNotificationRec"

        fun postNotification(
            context: Context,
            eventId: Long,
            title: String,
            category: String,
            timeRange: String,
            location: String,
            minutesBefore: Int
        ) {
            // Check POST_NOTIFICATIONS permission on Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.w(TAG, "POST_NOTIFICATIONS permission not granted, skipping notification")
                    return
                }
            }

            EventNotificationScheduler.createNotificationChannel(context)

            // Intent to open MainActivity and show event detail dialog
            val contentIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(MainActivity.EXTRA_OPEN_EVENT_ID, eventId)
            }
            val contentPendingIntent = PendingIntent.getActivity(
                context,
                eventId.toInt(),
                contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Action: Mark Complete
            val completeIntent = Intent(context, EventNotificationReceiver::class.java).apply {
                action = ACTION_MARK_COMPLETE
                putExtra(EXTRA_EVENT_ID, eventId)
            }
            val completePendingIntent = PendingIntent.getBroadcast(
                context,
                (eventId + 100000).toInt(),
                completeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Action: Snooze 10m
            val snoozeIntent = Intent(context, EventNotificationReceiver::class.java).apply {
                action = ACTION_SNOOZE_10_MIN
                putExtra(EXTRA_EVENT_ID, eventId)
                putExtra(EXTRA_EVENT_TITLE, title)
                putExtra(EXTRA_EVENT_CATEGORY, category)
                putExtra(EXTRA_EVENT_TIME, timeRange)
                putExtra(EXTRA_EVENT_LOCATION, location)
                putExtra(EXTRA_EVENT_MINUTES_BEFORE, minutesBefore)
            }
            val snoozePendingIntent = PendingIntent.getBroadcast(
                context,
                (eventId + 200000).toInt(),
                snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val reminderSubtitle = when {
                minutesBefore == 0 -> "Happening now ($timeRange)"
                minutesBefore > 0 -> "In $minutesBefore minutes • $timeRange"
                else -> timeRange
            }

            val bodyText = if (location.isNotBlank()) {
                "$reminderSubtitle • 📍 $location"
            } else {
                reminderSubtitle
            }

            val builder = NotificationCompat.Builder(context, EventNotificationScheduler.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_event_notification)
                .setContentTitle(title)
                .setContentText(bodyText)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("$bodyText\nCategory: $category")
                        .setSummaryText(category)
                )
                .setColor(0xFFFF7A59.toInt()) // Warm clay accent
                .setContentIntent(contentPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .addAction(
                    android.R.drawable.checkbox_on_background,
                    "Mark Done",
                    completePendingIntent
                )
                .addAction(
                    android.R.drawable.ic_popup_reminder,
                    "Snooze 10m",
                    snoozePendingIntent
                )

            try {
                NotificationManagerCompat.from(context).notify(eventId.toInt(), builder.build())
                Log.d(TAG, "Notification posted for event id: $eventId")
            } catch (e: SecurityException) {
                Log.e(TAG, "SecurityException posting notification", e)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to post notification", e)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        Log.d(TAG, "onReceive action: $action")

        when (action) {
            ACTION_TRIGGER_EVENT_NOTIFICATION -> {
                val eventId = intent.getLongExtra(EXTRA_EVENT_ID, -1L)
                val title = intent.getStringExtra(EXTRA_EVENT_TITLE) ?: "Upcoming Event"
                val category = intent.getStringExtra(EXTRA_EVENT_CATEGORY) ?: "Personal"
                val timeRange = intent.getStringExtra(EXTRA_EVENT_TIME) ?: ""
                val location = intent.getStringExtra(EXTRA_EVENT_LOCATION) ?: ""
                val minutesBefore = intent.getIntExtra(EXTRA_EVENT_MINUTES_BEFORE, 15)

                if (eventId <= 0) return

                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val database = CalendarDatabase.getDatabase(context, this)
                        val event = database.calendarDao().getEventById(eventId)
                        // Only show if event still exists and is not already completed
                        if (event == null || !event.isCompleted) {
                            postNotification(
                                context = context,
                                eventId = eventId,
                                title = event?.title ?: title,
                                category = event?.category ?: category,
                                timeRange = event?.timeRangeFormatted ?: timeRange,
                                location = event?.location ?: location,
                                minutesBefore = event?.reminderMinutesBefore ?: minutesBefore
                            )
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error checking event before notification", e)
                        // Fallback: show with intent extras
                        postNotification(
                            context = context,
                            eventId = eventId,
                            title = title,
                            category = category,
                            timeRange = timeRange,
                            location = location,
                            minutesBefore = minutesBefore
                        )
                    } finally {
                        pendingResult.finish()
                    }
                }
            }

            ACTION_MARK_COMPLETE -> {
                val eventId = intent.getLongExtra(EXTRA_EVENT_ID, -1L)
                if (eventId > 0) {
                    NotificationManagerCompat.from(context).cancel(eventId.toInt())
                    val pendingResult = goAsync()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val database = CalendarDatabase.getDatabase(context, this)
                            database.calendarDao().updateCompletedStatus(eventId, true)
                            Log.d(TAG, "Marked event $eventId complete from notification action")
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to mark event complete", e)
                        } finally {
                            pendingResult.finish()
                        }
                    }
                }
            }

            ACTION_SNOOZE_10_MIN -> {
                val eventId = intent.getLongExtra(EXTRA_EVENT_ID, -1L)
                val title = intent.getStringExtra(EXTRA_EVENT_TITLE) ?: "Upcoming Event"
                val category = intent.getStringExtra(EXTRA_EVENT_CATEGORY) ?: "Personal"
                val timeRange = intent.getStringExtra(EXTRA_EVENT_TIME) ?: ""
                val location = intent.getStringExtra(EXTRA_EVENT_LOCATION) ?: ""

                if (eventId > 0) {
                    NotificationManagerCompat.from(context).cancel(eventId.toInt())
                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                    if (alarmManager != null) {
                        val snoozeTrigger = System.currentTimeMillis() + 10 * 60 * 1000L
                        val snoozeIntent = Intent(context, EventNotificationReceiver::class.java).apply {
                            setAction(ACTION_TRIGGER_EVENT_NOTIFICATION)
                            putExtra(EXTRA_EVENT_ID, eventId)
                            putExtra(EXTRA_EVENT_TITLE, title)
                            putExtra(EXTRA_EVENT_CATEGORY, category)
                            putExtra(EXTRA_EVENT_TIME, timeRange)
                            putExtra(EXTRA_EVENT_LOCATION, location)
                            putExtra(EXTRA_EVENT_MINUTES_BEFORE, 0)
                        }
                        val pendingIntent = PendingIntent.getBroadcast(
                            context,
                            eventId.toInt(),
                            snoozeIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        try {
                            alarmManager.setAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                snoozeTrigger,
                                pendingIntent
                            )
                            Log.d(TAG, "Event $eventId snoozed for 10 minutes")
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to schedule snooze", e)
                        }
                    }
                }
            }
        }
    }
}
