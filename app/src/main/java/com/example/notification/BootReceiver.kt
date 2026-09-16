package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.CalendarDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d("BootReceiver", "BootReceiver triggered with action: $action")

        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == Intent.ACTION_TIME_CHANGED ||
            action == Intent.ACTION_TIMEZONE_CHANGED
        ) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val database = CalendarDatabase.getDatabase(context, this)
                    val allEvents = database.calendarDao().getAllEventsList()
                    val now = System.currentTimeMillis()

                    var scheduledCount = 0
                    for (event in allEvents) {
                        if (!event.isCompleted && event.reminderMinutesBefore >= 0) {
                            val trigger = EventNotificationScheduler.calculateTriggerMillis(event)
                            if (trigger > now) {
                                EventNotificationScheduler.scheduleNotification(context, event)
                                scheduledCount++
                            }
                        }
                    }
                    Log.d("BootReceiver", "Rescheduled $scheduledCount event alarms after boot/update")
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Failed to reschedule events after boot", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
