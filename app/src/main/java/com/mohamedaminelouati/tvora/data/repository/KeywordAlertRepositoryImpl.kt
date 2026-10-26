package com.mohamedaminelouati.tvora.data.repository

import com.mohamedaminelouati.tvora.data.local.db.KeywordAlertDao
import com.mohamedaminelouati.tvora.data.local.db.ReminderDao
import com.mohamedaminelouati.tvora.data.local.entity.KeywordAlertEntity
import com.mohamedaminelouati.tvora.data.mapper.toDomain
import com.mohamedaminelouati.tvora.domain.model.KeywordAlert
import com.mohamedaminelouati.tvora.domain.repository.KeywordAlertRepository
import com.mohamedaminelouati.tvora.domain.service.KeywordAlertManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeywordAlertRepositoryImpl @Inject constructor(
    private val keywordAlertDao: KeywordAlertDao,
    private val reminderDao: ReminderDao,
    private val keywordAlertManager: KeywordAlertManager
) : KeywordAlertRepository {

    override fun getAllKeywordAlerts(): Flow<List<KeywordAlert>> {
        return keywordAlertDao.getAllKeywordAlerts().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getActiveKeywordAlerts(): Flow<List<KeywordAlert>> {
        return keywordAlertDao.getActiveKeywordAlerts().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getActiveKeywordAlertsDirect(): List<KeywordAlert> {
        return keywordAlertDao.getActiveKeywordAlertsDirect().map { it.toDomain() }
    }

    override suspend fun addKeywordAlert(keyword: String, leadTimeMinutes: Int): Long {
        val trimmed = keyword.trim()
        val entity = KeywordAlertEntity(
            keyword = trimmed,
            leadTimeMinutes = leadTimeMinutes
        )
        val id = keywordAlertDao.insertKeyword(entity)
        val saved = keywordAlertDao.getKeywordAlertById(id)
        if (saved != null) {
            keywordAlertManager.syncKeyword(saved.toDomain())
        }
        return id
    }

    override suspend fun setKeywordEnabled(id: Long, isEnabled: Boolean) {
        val current = keywordAlertDao.getKeywordAlertById(id) ?: return
        keywordAlertDao.setKeywordEnabled(id, isEnabled)
        if (isEnabled) {
            keywordAlertManager.syncKeyword(current.toDomain().copy(isEnabled = true))
        } else {
            keywordAlertManager.cancelRemindersForKeyword(current.keyword)
        }
    }

    override suspend fun deleteKeywordAlert(id: Long, keyword: String, deleteReminders: Boolean) {
        keywordAlertDao.deleteKeyword(id)
        if (deleteReminders) {
            keywordAlertManager.cancelRemindersForKeyword(keyword)
        }
    }

    override suspend fun countUpcomingReminders(keyword: String): Int {
        return reminderDao.countUpcomingRemindersByKeyword(keyword, System.currentTimeMillis())
    }
}
