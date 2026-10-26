package com.mohamedaminelouati.tvora.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mohamedaminelouati.tvora.MainActivity
import com.mohamedaminelouati.tvora.R
import com.mohamedaminelouati.tvora.data.local.ChannelCatalog
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID_DEFAULT = "tvora_reminders"
        private const val CHANNEL_PREFIX_SOUND = "tvora_sound_"
    }

    init {
        createDefaultChannel()
    }

    fun showReminderNotification(
        programId: Long,
        title: String,
        channelId: String,
        leadTimeMinutes: Int,
        customSoundUri: String? = null,
        vibrationEnabled: Boolean = true
    ) {
        val channelIdToUse = if (customSoundUri != null) {
            getOrCreateSoundChannel(customSoundUri)
        } else {
            CHANNEL_ID_DEFAULT
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("extra_program_id", programId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            programId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelDisplayName = ChannelCatalog.getDisplayName(channelId)
        val contentText = context.getString(
            R.string.reminder_starting_soon,
            title,
            leadTimeMinutes,
            channelDisplayName
        )

        val soundUri = customSoundUri?.let { Uri.parse(it) }
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(context, channelIdToUse)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(context.getString(R.string.reminder_title))
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (vibrationEnabled) {
            builder.setVibrate(longArrayOf(0, 300, 200, 300))
        } else {
            builder.setVibrate(longArrayOf(0))
        }

        notificationManager.notify(programId.toInt(), builder.build())
    }

    fun showKeywordDiscoveryNotification(
        keyword: String,
        matchCount: Int,
        firstProgramTitle: String,
        firstChannelId: String,
        customSoundUri: String? = null
    ) {
        val channelIdToUse = if (customSoundUri != null) {
            getOrCreateSoundChannel(customSoundUri)
        } else {
            CHANNEL_ID_DEFAULT
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            keyword.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelName = ChannelCatalog.getDisplayName(firstChannelId)
        val contentText = if (matchCount == 1) {
            context.getString(R.string.keyword_notification_single_match, firstProgramTitle, channelName)
        } else {
            context.getString(R.string.keyword_notification_multiple_matches, matchCount, keyword)
        }

        val soundUri = customSoundUri?.let { Uri.parse(it) }
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(context, channelIdToUse)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(context.getString(R.string.keyword_notification_title, keyword))
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 300, 200, 300))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(keyword.hashCode(), builder.build())
    }

    private fun getOrCreateSoundChannel(soundUriString: String): String {
        val safeHash = Math.abs(soundUriString.hashCode())
        val channelId = "${CHANNEL_PREFIX_SOUND}$safeHash"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val existing = notificationManager.getNotificationChannel(channelId)
            if (existing == null) {
                val soundUri = Uri.parse(soundUriString)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                    .build()

                val channel = NotificationChannel(
                    channelId,
                    context.getString(R.string.settings_notification_sound),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    setSound(soundUri, audioAttributes)
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
        return channelId
    }

    private fun createDefaultChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .build()

            val channel = NotificationChannel(
                CHANNEL_ID_DEFAULT,
                context.getString(R.string.reminder_title),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.settings_notifications_title)
                setSound(defaultSoundUri, audioAttributes)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}
