package com.mohamedaminelouati.tvora.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mohamedaminelouati.tvora.data.local.entity.KeywordAlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KeywordAlertDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeyword(entity: KeywordAlertEntity): Long

    @Update
    suspend fun updateKeyword(entity: KeywordAlertEntity)

    @Query("DELETE FROM keyword_alerts WHERE id = :id")
    suspend fun deleteKeyword(id: Long)

    @Query("DELETE FROM keyword_alerts WHERE keyword = :keyword")
    suspend fun deleteKeywordByText(keyword: String)

    @Query("SELECT * FROM keyword_alerts WHERE id = :id LIMIT 1")
    suspend fun getKeywordAlertById(id: Long): KeywordAlertEntity?

    @Query("SELECT * FROM keyword_alerts ORDER BY createdAt DESC")
    fun getAllKeywordAlerts(): Flow<List<KeywordAlertEntity>>

    @Query("SELECT * FROM keyword_alerts WHERE isEnabled = 1 ORDER BY createdAt DESC")
    fun getActiveKeywordAlerts(): Flow<List<KeywordAlertEntity>>

    @Query("SELECT * FROM keyword_alerts WHERE isEnabled = 1")
    suspend fun getActiveKeywordAlertsDirect(): List<KeywordAlertEntity>

    @Query("UPDATE keyword_alerts SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun setKeywordEnabled(id: Long, isEnabled: Boolean)

    @Query("UPDATE keyword_alerts SET soundUri = :soundUri WHERE id = :id")
    suspend fun setKeywordSound(id: Long, soundUri: String?)
}
