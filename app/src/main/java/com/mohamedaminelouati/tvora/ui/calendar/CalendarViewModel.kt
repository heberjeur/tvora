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

enum class CalendarScope {
    SELECTED_DAY,
    ALL_UPCOMING
}

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

    private val _calendarScope = MutableStateFlow(CalendarScope.SELECTED_DAY)
    val calendarScope: StateFlow<CalendarScope> = _calendarScope.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedKeywordFilter = MutableStateFlow<String?>(null)
    val selectedKeywordFilter: StateFlow<String?> = _selectedKeywordFilter.asStateFlow()

    val activeKeywords: StateFlow<List<KeywordAlert>> = keywordAlertRepository.getActiveKeywordAlerts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReminders: StateFlow<List<Reminder>> = reminderRepository.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalUpcomingCount: StateFlow<Int> = allReminders.map { reminders ->
        val now = System.currentTimeMillis()
        reminders.count { it.startTimestamp >= now - 60_000L }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private data class FilterParams(
        val date: LocalDate,
        val query: String,
        val keywordFilter: String?,
        val scope: CalendarScope
    )

    private val filterParams = combine(
        _selectedDate,
        _searchQuery,
        _selectedKeywordFilter,
        _calendarScope
    ) { date, query, filter, scope ->
        FilterParams(date, query, filter, scope)
    }

    val filteredReminders: StateFlow<List<Reminder>> = combine(
        allReminders,
        filterParams,
        displayZoneFlow
    ) { reminders, params, zone ->
        val now = System.currentTimeMillis()
        reminders.filter { reminder ->
            val reminderDate = Instant.ofEpochMilli(reminder.startTimestamp).atZone(zone).toLocalDate()
            val matchesScope = when (params.scope) {
                CalendarScope.SELECTED_DAY -> {
                    when (reminder.recurrence) {
                        "daily" -> params.date >= reminderDate
                        "weekly" -> params.date >= reminderDate && java.time.temporal.ChronoUnit.DAYS.between(reminderDate, params.date) % 7 == 0L
                        "monthly" -> params.date >= reminderDate && params.date.dayOfMonth == reminderDate.dayOfMonth
                        else -> reminderDate == params.date
                    }
                }
                CalendarScope.ALL_UPCOMING -> {
                    reminder.startTimestamp >= now - 60_000L
                }
            }
            val matchesQuery = params.query.isBlank() ||
                reminder.title.contains(params.query, ignoreCase = true) ||
                reminder.channelId.contains(params.query, ignoreCase = true)
            val matchesKeyword = when (val filter = params.keywordFilter) {
                null -> true
                "__manual__" -> reminder.originKeyword == null
                else -> reminder.originKeyword.equals(filter, ignoreCase = true)
            }
            matchesScope && matchesQuery && matchesKeyword
        }.sortedBy { it.startTimestamp }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setInitialKeyword(keyword: String?) {
        if (!keyword.isNullOrBlank()) {
            _selectedKeywordFilter.value = keyword
            _calendarScope.value = CalendarScope.ALL_UPCOMING
        }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        _calendarScope.value = CalendarScope.SELECTED_DAY
    }

    fun setCalendarScope(scope: CalendarScope) {
        _calendarScope.value = scope
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectKeywordFilter(filter: String?) {
        _selectedKeywordFilter.value = filter
        if (filter != null) {
            _calendarScope.value = CalendarScope.ALL_UPCOMING
        }
    }

    fun removeReminder(programId: Long) {
        viewModelScope.launch {
            alarmScheduler.cancel(programId)
            reminderRepository.deleteReminder(programId)
        }
    }
}
