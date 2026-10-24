package com.mohamedaminelouati.tvora.data.repository

import com.mohamedaminelouati.tvora.data.local.db.ReminderDao
import com.mohamedaminelouati.tvora.data.mapper.toDomain
import com.mohamedaminelouati.tvora.data.mapper.toEntity
import com.mohamedaminelouati.tvora.domain.model.Program
import com.mohamedaminelouati.tvora.domain.model.Reminder
import com.mohamedaminelouati.tvora.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepositoryImpl @Inject constructor(
    private val reminderDao: ReminderDao
) : ReminderRepository {

    override fun getAllReminders(): Flow<List<Reminder>> {
        return reminderDao.getAllReminders().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getReminderForProgram(programId: Long): Flow<Reminder?> {
        return reminderDao.getReminderForProgram(programId).map { it?.toDomain() }
    }

    override suspend fun getReminder(programId: Long): Reminder? {
        return reminderDao.getReminderForProgramDirect(programId)?.toDomain()
    }

    override suspend fun getUpcomingReminders(currentTimestamp: Long): List<Reminder> {
        return reminderDao.getUpcomingReminders(currentTimestamp).map { it.toDomain() }
    }

    override suspend fun saveReminder(reminder: Reminder): Long {
        return reminderDao.insertReminder(reminder.toEntity())
    }

    override suspend fun createReminder(
        program: Program,
        leadTimeMinutes: Int,
        syncToCalendar: Boolean,
        soundUri: String?,
        recurrence: String
    ): Long {
        val triggerTimestamp = program.startTimestamp - (leadTimeMinutes * 60 * 1000L)
        val reminder = Reminder(
            programId = program.id,
            channelId = program.channelId,
            title = program.title,
            startTimestamp = program.startTimestamp,
            triggerTimestamp = triggerTimestamp,
            leadTimeMinutes = leadTimeMinutes,
            isCalendarSyncEnabled = syncToCalendar,
            soundUri = soundUri,
            recurrence = recurrence
        )
        return saveReminder(reminder)
    }

    override suspend fun deleteReminder(programId: Long) {
        reminderDao.deleteReminderByProgramId(programId)
    }

    override suspend fun deleteExpiredReminders(cutoffTimestamp: Long) {
        reminderDao.deleteExpiredReminders(cutoffTimestamp)
    }
}
