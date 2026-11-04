package com.mohamedaminelouati.tvora.domain.service

import com.mohamedaminelouati.tvora.data.local.db.KeywordAlertDao
import com.mohamedaminelouati.tvora.data.local.db.ProgramDao
import com.mohamedaminelouati.tvora.data.local.db.ReminderDao
import com.mohamedaminelouati.tvora.data.local.entity.ReminderEntity
import com.mohamedaminelouati.tvora.data.local.prefs.UserPreferences
import com.mohamedaminelouati.tvora.data.mapper.toDomain
import com.mohamedaminelouati.tvora.domain.model.KeywordAlert
import com.mohamedaminelouati.tvora.notification.AlarmScheduler
import com.mohamedaminelouati.tvora.notification.NotificationHelper
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeywordAlertManager @Inject constructor(
    private val keywordAlertDao: KeywordAlertDao,
    private val programDao: ProgramDao,
    private val reminderDao: ReminderDao,
    private val alarmScheduler: AlarmScheduler,
    private val notificationHelper: NotificationHelper,
    private val userPreferences: UserPreferences
) {

    suspend fun syncKeyword(keywordAlert: KeywordAlert, notifyOnDiscovery: Boolean = true): Int {
        if (!keywordAlert.isEnabled || keywordAlert.keyword.isBlank()) return 0
        cleanupDuplicateReminders()
        val now = System.currentTimeMillis()
        val cleanKeyword = keywordAlert.keyword.trim().lowercase()
        val matchingPrograms = programDao.searchUpcomingProgramsByText(cleanKeyword, now)

        val defaultSound = userPreferences.notificationSound.firstOrNull()
        val effectiveSoundUri = keywordAlert.soundUri ?: defaultSound

        var newRemindersCount = 0
        for (prog in matchingPrograms) {
            val aliases = com.mohamedaminelouati.tvora.data.local.ChannelCatalog.getChannelAliases(prog.channelId)
            val existing = reminderDao.getReminderForProgramDirect(prog.id)
                ?: reminderDao.getReminderForBroadcast(prog.channelId, aliases, prog.startTimestamp)
                ?: reminderDao.getReminderByTitleAndStart(prog.title, prog.startTimestamp)
            if (existing != null) {
                if (existing.programId != prog.id) {
                    reminderDao.updateReminder(existing.copy(programId = prog.id))
                }
                continue
            }

            val triggerTimestamp = prog.startTimestamp - (keywordAlert.leadTimeMinutes * 60 * 1000L)
            if (triggerTimestamp <= now) continue

            val reminderEntity = ReminderEntity(
                programId = prog.id,
                channelId = prog.channelId,
                title = prog.title,
                startTimestamp = prog.startTimestamp,
                triggerTimestamp = triggerTimestamp,
                leadTimeMinutes = keywordAlert.leadTimeMinutes,
                isCalendarSyncEnabled = false,
                calendarEventId = null,
                isNotificationEnabled = true,
                soundUri = effectiveSoundUri,
                recurrence = "once",
                originKeyword = keywordAlert.keyword
            )
            reminderDao.insertReminder(reminderEntity)
            alarmScheduler.schedule(reminderEntity.toDomain())
            newRemindersCount++
        }

        if (newRemindersCount > 0 && notifyOnDiscovery) {
            val firstMatch = matchingPrograms.first()
            notificationHelper.showKeywordDiscoveryNotification(
                keyword = keywordAlert.keyword,
                matchCount = newRemindersCount,
                firstProgramTitle = firstMatch.title,
                firstChannelId = firstMatch.channelId,
                customSoundUri = effectiveSoundUri
            )
        }

        return newRemindersCount
    }

    suspend fun syncAllActiveKeywords(): Int {
        cleanupDuplicateReminders()
        val activeKeywords = keywordAlertDao.getActiveKeywordAlertsDirect()
        var totalScheduled = 0
        for (entity in activeKeywords) {
            totalScheduled += syncKeyword(entity.toDomain(), notifyOnDiscovery = true)
        }
        return totalScheduled
    }

    private suspend fun cleanupDuplicateReminders() {
        try {
            reminderDao.purgeDuplicateReminders()
        } catch (_: Exception) {
        }
        val all = reminderDao.getAllRemindersDirect()
        val seen = mutableSetOf<String>()
        for (rem in all) {
            val key = "${com.mohamedaminelouati.tvora.data.local.ChannelCatalog.normalizeId(rem.channelId)}_${rem.startTimestamp}_${rem.title.trim().lowercase()}"
            if (!seen.add(key)) {
                reminderDao.deleteReminder(rem)
            }
        }
    }

    suspend fun updateSoundForKeyword(keyword: String, soundUri: String?) {
        val reminders = reminderDao.getRemindersByOriginKeyword(keyword)
        val now = System.currentTimeMillis()
        for (r in reminders) {
            if (r.startTimestamp > now) {
                val updated = r.copy(soundUri = soundUri)
                reminderDao.updateReminder(updated)
                alarmScheduler.schedule(updated.toDomain())
            }
        }
    }

    suspend fun cancelRemindersForKeyword(keyword: String) {
        val reminders = reminderDao.getRemindersByOriginKeyword(keyword)
        for (r in reminders) {
            alarmScheduler.cancel(r.programId)
        }
        reminderDao.deleteRemindersByOriginKeyword(keyword)
    }
}
