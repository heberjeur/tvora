package com.mohamedaminelouati.tvora.domain.repository

import com.mohamedaminelouati.tvora.domain.model.KeywordAlert
import kotlinx.coroutines.flow.Flow

interface KeywordAlertRepository {
    fun getAllKeywordAlerts(): Flow<List<KeywordAlert>>
    fun getActiveKeywordAlerts(): Flow<List<KeywordAlert>>
    suspend fun getActiveKeywordAlertsDirect(): List<KeywordAlert>
    suspend fun addKeywordAlert(keyword: String, leadTimeMinutes: Int = 10): Long
    suspend fun setKeywordEnabled(id: Long, isEnabled: Boolean)
    suspend fun deleteKeywordAlert(id: Long, keyword: String, deleteReminders: Boolean = true)
    suspend fun countUpcomingReminders(keyword: String): Int
}
