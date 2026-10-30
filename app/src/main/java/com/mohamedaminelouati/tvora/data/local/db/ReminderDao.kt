package com.mohamedaminelouati.tvora.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mohamedaminelouati.tvora.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY triggerTimestamp ASC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE triggerTimestamp > :currentTimestamp ORDER BY triggerTimestamp ASC")
    suspend fun getUpcomingReminders(currentTimestamp: Long): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE programId = :programId LIMIT 1")
    fun getReminderForProgram(programId: Long): Flow<ReminderEntity?>

    @Query("SELECT * FROM reminders WHERE programId = :programId LIMIT 1")
    suspend fun getReminderForProgramDirect(programId: Long): ReminderEntity?

    @Query("SELECT * FROM reminders WHERE id = :id LIMIT 1")
    suspend fun getReminderById(id: Long): ReminderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE programId = :programId")
    suspend fun deleteReminderByProgramId(programId: Long)

    @Query("DELETE FROM reminders WHERE triggerTimestamp < :cutoffTimestamp")
    suspend fun deleteExpiredReminders(cutoffTimestamp: Long)

    @Query("SELECT * FROM reminders WHERE originKeyword = :keyword")
    suspend fun getRemindersByOriginKeyword(keyword: String): List<ReminderEntity>

    @Query("DELETE FROM reminders WHERE originKeyword = :keyword")
    suspend fun deleteRemindersByOriginKeyword(keyword: String)

    @Query("SELECT COUNT(*) FROM reminders WHERE originKeyword = :keyword AND startTimestamp > :now")
    suspend fun countUpcomingRemindersByKeyword(keyword: String, now: Long): Int
}
