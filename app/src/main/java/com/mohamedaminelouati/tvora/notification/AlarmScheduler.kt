package com.mohamedaminelouati.tvora.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.mohamedaminelouati.tvora.domain.model.Reminder
import com.mohamedaminelouati.tvora.receiver.ReminderReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(reminder: Reminder, vibrationEnabled: Boolean = true) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("extra_program_id", reminder.programId)
            putExtra("extra_title", reminder.title)
            putExtra("extra_channel_id", reminder.channelId)
            putExtra("extra_lead_time", reminder.leadTimeMinutes)
            putExtra("extra_sound_uri", reminder.soundUri)
            putExtra("extra_vibration_enabled", vibrationEnabled)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.programId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminder.triggerTimestamp,
                pendingIntent
            )
        } catch (_: SecurityException) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminder.triggerTimestamp,
                pendingIntent
            )
        }
    }

    fun cancel(programId: Long) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            programId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
