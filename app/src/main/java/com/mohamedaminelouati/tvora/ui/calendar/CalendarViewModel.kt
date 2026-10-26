package com.mohamedaminelouati.tvora.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamedaminelouati.tvora.domain.model.KeywordAlert
import com.mohamedaminelouati.tvora.domain.model.Reminder
import com.mohamedaminelouati.tvora.domain.repository.KeywordAlertRepository
import com.mohamedaminelouati.tvora.domain.repository.ReminderRepository
import com.mohamedaminelouati.tvora.notification.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val keywordAlertRepository: KeywordAlertRepository,
    private val alarmScheduler: AlarmScheduler,
    private val userPreferences: com.mohamedaminelouati.tvora.data.local.prefs.UserPreferences
) : ViewModel() {

    private val displayZoneFlow = userPreferences.displayTimezone.map {
        com.mohamedaminelouati.tvora.util.TimezoneUtils.resolveZoneId(it)
    }

    private val _selectedDate = MutableStateFlow(LocalDate.now(com.mohamedaminelouati.tvora.util.TimezoneUtils.ORIGIN_ZONE))
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedKeywordFilter = MutableStateFlow<String?>(null)
    val selectedKeywordFilter: StateFlow<String?> = _selectedKeywordFilter.asStateFlow()

    val activeKeywords: StateFlow<List<KeywordAlert>> = keywordAlertRepository.getActiveKeywordAlerts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReminders: StateFlow<List<Reminder>> = reminderRepository.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredReminders: StateFlow<List<Reminder>> = combine(
        allReminders,
        _selectedDate,
        _searchQuery,
        _selectedKeywordFilter,
        displayZoneFlow
    ) { reminders, date, query, filter, zone ->
        reminders.filter { reminder ->
            val reminderDate = Instant.ofEpochMilli(reminder.startTimestamp).atZone(zone).toLocalDate()
            val matchesDate = when (reminder.recurrence) {
                "daily" -> date >= reminderDate
                "weekly" -> date >= reminderDate && java.time.temporal.ChronoUnit.DAYS.between(reminderDate, date) % 7 == 0L
                "monthly" -> date >= reminderDate && date.dayOfMonth == reminderDate.dayOfMonth
                else -> reminderDate == date
            }
            val matchesQuery = query.isBlank() ||
                reminder.title.contains(query, ignoreCase = true) ||
                reminder.channelId.contains(query, ignoreCase = true)
            val matchesKeyword = when (filter) {
                null -> true
                "__manual__" -> reminder.originKeyword == null
                else -> reminder.originKeyword.equals(filter, ignoreCase = true)
            }
            matchesDate && matchesQuery && matchesKeyword
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectKeywordFilter(filter: String?) {
        _selectedKeywordFilter.value = filter
    }

    fun removeReminder(programId: Long) {
        viewModelScope.launch {
            alarmScheduler.cancel(programId)
            reminderRepository.deleteReminder(programId)
        }
    }
}
