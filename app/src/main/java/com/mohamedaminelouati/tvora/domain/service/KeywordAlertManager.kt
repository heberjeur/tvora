package com.mohamedaminelouati.tvora.domain.service

import com.mohamedaminelouati.tvora.data.local.db.KeywordAlertDao
import com.mohamedaminelouati.tvora.data.local.db.ProgramDao
import com.mohamedaminelouati.tvora.data.local.db.ReminderDao
import com.mohamedaminelouati.tvora.data.local.entity.ReminderEntity
import com.mohamedaminelouati.tvora.data.mapper.toDomain
import com.mohamedaminelouati.tvora.domain.model.KeywordAlert
import com.mohamedaminelouati.tvora.notification.AlarmScheduler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeywordAlertManager @Inject constructor(
    private val keywordAlertDao: KeywordAlertDao,
    private val programDao: ProgramDao,
    private val reminderDao: ReminderDao,
    private val alarmScheduler: AlarmScheduler
) {

    suspend fun syncKeyword(keywordAlert: KeywordAlert): Int {
        if (!keywordAlert.isEnabled || keywordAlert.keyword.isBlank()) return 0
        val now = System.currentTimeMillis()
        val cleanKeyword = keywordAlert.keyword.trim().lowercase()
        val matchingPrograms = programDao.searchUpcomingProgramsByText(cleanKeyword, now)

        var newRemindersCount = 0
        for (prog in matchingPrograms) {
            val existing = reminderDao.getReminderForProgramDirect(prog.id)
            if (existing != null) continue

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
                soundUri = null,
                recurrence = "once",
                originKeyword = keywordAlert.keyword
            )
            reminderDao.insertReminder(reminderEntity)
            alarmScheduler.schedule(reminderEntity.toDomain())
            newRemindersCount++
        }
        return newRemindersCount
    }

    suspend fun syncAllActiveKeywords(): Int {
        val activeKeywords = keywordAlertDao.getActiveKeywordAlertsDirect()
        var totalScheduled = 0
        for (entity in activeKeywords) {
            totalScheduled += syncKeyword(entity.toDomain())
        }
        return totalScheduled
    }

    suspend fun cancelRemindersForKeyword(keyword: String) {
        val reminders = reminderDao.getRemindersByOriginKeyword(keyword)
        for (r in reminders) {
            alarmScheduler.cancel(r.programId)
        }
        reminderDao.deleteRemindersByOriginKeyword(keyword)
    }
}
