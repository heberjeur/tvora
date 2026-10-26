package com.mohamedaminelouati.tvora.ui.settings.keyword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamedaminelouati.tvora.data.local.prefs.UserPreferences
import com.mohamedaminelouati.tvora.domain.model.KeywordAlert
import com.mohamedaminelouati.tvora.domain.repository.KeywordAlertRepository
import com.mohamedaminelouati.tvora.domain.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class KeywordAlertItemUiState(
    val alert: KeywordAlert,
    val upcomingCount: Int = 0
)

@HiltViewModel
class KeywordAlertsViewModel @Inject constructor(
    private val keywordAlertRepository: KeywordAlertRepository,
    private val reminderRepository: ReminderRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val defaultNotificationSound: StateFlow<String?> = userPreferences.notificationSound
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _inputError = MutableStateFlow<String?>(null)
    val inputError: StateFlow<String?> = _inputError.asStateFlow()

    val keywordAlerts: StateFlow<List<KeywordAlertItemUiState>> = combine(
        keywordAlertRepository.getAllKeywordAlerts(),
        reminderRepository.getAllReminders()
    ) { alerts, reminders ->
        val now = System.currentTimeMillis()
        val upcomingCounts = reminders
            .filter { it.startTimestamp > now && it.originKeyword != null }
            .groupingBy { it.originKeyword!!.lowercase() }
            .eachCount()

        alerts.map { alert ->
            val count = upcomingCounts[alert.keyword.lowercase()] ?: 0
            KeywordAlertItemUiState(alert = alert, upcomingCount = count)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun clearError() {
        _inputError.value = null
    }

    fun addKeyword(rawKeyword: String, leadTimeMinutes: Int = 10, soundUri: String? = null) {
        val trimmed = rawKeyword.trim()
        if (trimmed.isBlank()) {
            _inputError.value = "empty"
            return
        }
        val exists = keywordAlerts.value.any { it.alert.keyword.equals(trimmed, ignoreCase = true) }
        if (exists) {
            _inputError.value = "duplicate"
            return
        }
        _inputError.value = null
        viewModelScope.launch {
            keywordAlertRepository.addKeywordAlert(trimmed, leadTimeMinutes, soundUri)
        }
    }

    fun setKeywordEnabled(id: Long, isEnabled: Boolean) {
        viewModelScope.launch {
            keywordAlertRepository.setKeywordEnabled(id, isEnabled)
        }
    }

    fun setKeywordSound(id: Long, soundUri: String?) {
        viewModelScope.launch {
            keywordAlertRepository.setKeywordSound(id, soundUri)
        }
    }

    fun deleteKeyword(id: Long, keyword: String, deleteReminders: Boolean) {
        viewModelScope.launch {
            keywordAlertRepository.deleteKeywordAlert(id, keyword, deleteReminders)
        }
    }
}
