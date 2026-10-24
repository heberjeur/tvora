package com.mohamedaminelouati.tvora.domain.repository

import com.mohamedaminelouati.tvora.domain.model.Program
import com.mohamedaminelouati.tvora.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getAllReminders(): Flow<List<Reminder>>
    fun getReminderForProgram(programId: Long): Flow<Reminder?>
    suspend fun getReminder(programId: Long): Reminder?
    suspend fun getUpcomingReminders(currentTimestamp: Long): List<Reminder>
    suspend fun saveReminder(reminder: Reminder): Long
    suspend fun createReminder(
        program: Program,
        leadTimeMinutes: Int = 10,
        syncToCalendar: Boolean = false,
        soundUri: String? = null,
        recurrence: String = "once"
    ): Long
    suspend fun deleteReminder(programId: Long)
    suspend fun deleteExpiredReminders(cutoffTimestamp: Long)
}
