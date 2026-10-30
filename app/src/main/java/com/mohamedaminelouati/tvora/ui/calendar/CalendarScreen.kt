package com.mohamedaminelouati.tvora.ui.calendar

import android.content.res.Configuration
import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.mohamedaminelouati.tvora.R
import com.mohamedaminelouati.tvora.data.local.ChannelCatalog
import com.mohamedaminelouati.tvora.domain.model.KeywordAlert
import com.mohamedaminelouati.tvora.domain.model.Reminder
import com.mohamedaminelouati.tvora.ui.theme.AccentRed
import com.mohamedaminelouati.tvora.ui.theme.DarkBackground
import com.mohamedaminelouati.tvora.ui.theme.DarkSurface
import com.mohamedaminelouati.tvora.ui.theme.DarkSurfaceVariant
import com.mohamedaminelouati.tvora.ui.theme.SurfaceBorder
import com.mohamedaminelouati.tvora.ui.theme.TextPrimary
import com.mohamedaminelouati.tvora.ui.theme.TextSecondary
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    initialKeyword: String? = null,
    onProgramClick: (Long) -> Unit,
    onSettingsClick: () -> Unit = {},
    onChannelClick: (String) -> Unit = {},
    viewModel: CalendarViewModel = hiltViewModel()
) {
    LaunchedEffect(initialKeyword) {
        if (!initialKeyword.isNullOrBlank()) {
            viewModel.setInitialKeyword(initialKeyword)
        }
    }
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val calendarScope by viewModel.calendarScope.collectAsStateWithLifecycle()
    val totalUpcomingCount by viewModel.totalUpcomingCount.collectAsStateWithLifecycle()
    val allReminders by viewModel.allReminders.collectAsStateWithLifecycle()
    val filteredReminders by viewModel.filteredReminders.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val activeKeywords by viewModel.activeKeywords.collectAsStateWithLifecycle()
    val selectedKeywordFilter by viewModel.selectedKeywordFilter.collectAsStateWithLifecycle()
    var isSearchActive by remember { mutableStateOf(false) }

    val zone = com.mohamedaminelouati.tvora.ui.theme.LocalDisplayZone.current
    val today = remember(zone) { LocalDate.now(zone) }
    var currentYearMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }

    LaunchedEffect(selectedDate) {
        val dateMonth = YearMonth.from(selectedDate)
        if (dateMonth != currentYearMonth) {
            currentYearMonth = dateMonth
        }
    }

    val configuration = LocalConfiguration.current
    val currentLocale = remember(configuration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !configuration.locales.isEmpty) {
            configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            configuration.locale ?: Locale.getDefault()
        }
    }
    val monthYearFormatter = remember(currentLocale) { DateTimeFormatter.ofPattern("MMMM yyyy", currentLocale) }
    val selectedDateHeaderFormatter = remember(currentLocale) { DateTimeFormatter.ofPattern("EEEE d MMMM", currentLocale) }

    val reminderDates = remember(allReminders, selectedKeywordFilter, zone) {
        allReminders
            .filter { rem ->
                when (selectedKeywordFilter) {
                    null -> true
                    "__manual__" -> rem.originKeyword == null
                    else -> rem.originKeyword.equals(selectedKeywordFilter, ignoreCase = true)
                }
            }
            .map { Instant.ofEpochMilli(it.startTimestamp).atZone(zone).toLocalDate() }
            .toSet()
    }

    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchActive) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = viewModel::setSearchQuery,
                            placeholder = { Text(stringResource(R.string.search_programs_placeholder)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = AccentRed,
                                unfocusedBorderColor = SurfaceBorder
                            ),
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.action_clear_search), tint = TextSecondary)
                                    }
                                }
                            }
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.calendar_title),
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    if (isSearchActive) {
                        IconButton(onClick = {
                            isSearchActive = false
                            viewModel.setSearchQuery("")
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = TextPrimary
                            )
                        }
                    }
                },
                actions = {
                    if (!isSearchActive) {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.action_search),
                                tint = TextPrimary
                            )
                        }
                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = stringResource(R.string.tab_settings),
                                tint = TextPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.48f)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.TopCenter
                ) {
                    CalendarMonthCard(
                        currentYearMonth = currentYearMonth,
                        onPrevMonth = { currentYearMonth = currentYearMonth.minusMonths(1) },
                        onNextMonth = { currentYearMonth = currentYearMonth.plusMonths(1) },
                        monthYearFormatter = monthYearFormatter,
                        currentLocale = currentLocale,
                        selectedDate = selectedDate,
                        today = today,
                        reminderDates = reminderDates,
                        onSelectDate = viewModel::selectDate
                    )
                }

                CalendarRemindersSection(
                    selectedDate = selectedDate,
                    selectedDateHeaderFormatter = selectedDateHeaderFormatter,
                    currentLocale = currentLocale,
                    calendarScope = calendarScope,
                    totalUpcomingCount = totalUpcomingCount,
                    filteredReminders = filteredReminders,
                    searchQuery = searchQuery,
                    activeKeywords = activeKeywords,
                    selectedKeywordFilter = selectedKeywordFilter,
                    onSelectScope = viewModel::setCalendarScope,
                    onSelectKeywordFilter = viewModel::selectKeywordFilter,
                    onSelectDate = viewModel::selectDate,
                    onProgramClick = onProgramClick,
                    onDeleteReminder = viewModel::removeReminder,
                    onChannelClick = onChannelClick,
                    modifier = Modifier
                        .weight(0.52f)
                        .fillMaxSize()
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                CalendarMonthCard(
                    currentYearMonth = currentYearMonth,
                    onPrevMonth = { currentYearMonth = currentYearMonth.minusMonths(1) },
                    onNextMonth = { currentYearMonth = currentYearMonth.plusMonths(1) },
                    monthYearFormatter = monthYearFormatter,
                    currentLocale = currentLocale,
                    selectedDate = selectedDate,
                    today = today,
                    reminderDates = reminderDates,
                    onSelectDate = viewModel::selectDate
                )

                CalendarRemindersSection(
                    selectedDate = selectedDate,
                    selectedDateHeaderFormatter = selectedDateHeaderFormatter,
                    currentLocale = currentLocale,
                    calendarScope = calendarScope,
                    totalUpcomingCount = totalUpcomingCount,
                    filteredReminders = filteredReminders,
                    searchQuery = searchQuery,
                    activeKeywords = activeKeywords,
                    selectedKeywordFilter = selectedKeywordFilter,
                    onSelectScope = viewModel::setCalendarScope,
                    onSelectKeywordFilter = viewModel::selectKeywordFilter,
                    onSelectDate = viewModel::selectDate,
                    onProgramClick = onProgramClick,
                    onDeleteReminder = viewModel::removeReminder,
                    onChannelClick = onChannelClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun CalendarMonthCard(
    currentYearMonth: YearMonth,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    monthYearFormatter: DateTimeFormatter,
    currentLocale: Locale,
    selectedDate: LocalDate,
    today: LocalDate,
    reminderDates: Set<LocalDate>,
    onSelectDate: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var totalDragOffset by remember { mutableFloatStateOf(0f) }
    val currentOnNextMonth by rememberUpdatedState(onNextMonth)
    val currentOnPrevMonth by rememberUpdatedState(onPrevMonth)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { totalDragOffset = 0f },
                    onDragEnd = {
                        if (totalDragOffset < -50f) {
                            currentOnNextMonth()
                        } else if (totalDragOffset > 50f) {
                            currentOnPrevMonth()
                        }
                        totalDragOffset = 0f
                    },
                    onDragCancel = { totalDragOffset = 0f },
                    onHorizontalDrag = { _, dragAmount ->
                        totalDragOffset += dragAmount
                    }
                )
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrevMonth) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = TextPrimary
                    )
                }

                Text(
                    text = currentYearMonth.format(monthYearFormatter)
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(currentLocale) else it.toString() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                IconButton(onClick = onNextMonth) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf(
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY,
                    DayOfWeek.SATURDAY,
                    DayOfWeek.SUNDAY
                ).forEach { dayOfWeek ->
                    val label = dayOfWeek.getDisplayName(TextStyle.SHORT, currentLocale).take(3)
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedContent(
                targetState = currentYearMonth,
                transitionSpec = {
                    if (targetState.isAfter(initialState)) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut()
                        )
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut()
                        )
                    }
                },
                label = "MonthDaysGrid"
            ) { displayedMonth ->
                Column {
                    val firstDay = displayedMonth.atDay(1)
                    val daysInMonth = displayedMonth.lengthOfMonth()
                    val leadingEmptySlots = (firstDay.dayOfWeek.value - 1)
                    val totalSlots = leadingEmptySlots + daysInMonth
                    val rowsCount = (totalSlots + 6) / 7

                    for (rowIndex in 0 until rowsCount) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            for (colIndex in 0 until 7) {
                                val slotIndex = rowIndex * 7 + colIndex
                                val dayNumber = slotIndex - leadingEmptySlots + 1

                                if (dayNumber in 1..daysInMonth) {
                                    val date = displayedMonth.atDay(dayNumber)
                                    val isSelected = date == selectedDate
                                    val isToday = date == today
                                    val hasReminders = reminderDates.contains(date)

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(38.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when {
                                                    isSelected -> AccentRed
                                                    else -> DarkSurfaceVariant.copy(alpha = 0.3f)
                                                }
                                            )
                                            .then(
                                                if (isToday && !isSelected) {
                                                    Modifier.border(1.dp, AccentRed.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                                                } else Modifier
                                            )
                                            .clickable { onSelectDate(date) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = dayNumber.toString(),
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) TextPrimary else if (isToday) AccentRed else TextPrimary
                                            )
                                            if (hasReminders) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(4.dp)
                                                        .clip(CircleShape)
                                                        .background(if (isSelected) TextPrimary else AccentRed)
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarRemindersSection(
    selectedDate: LocalDate,
    selectedDateHeaderFormatter: DateTimeFormatter,
    currentLocale: Locale,
    calendarScope: CalendarScope,
    totalUpcomingCount: Int,
    filteredReminders: List<Reminder>,
    searchQuery: String,
    activeKeywords: List<KeywordAlert>,
    selectedKeywordFilter: String?,
    onSelectScope: (CalendarScope) -> Unit,
    onSelectKeywordFilter: (String?) -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onProgramClick: (Long) -> Unit,
    onDeleteReminder: (Long) -> Unit,
    onChannelClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var dateDragOffset by remember { mutableFloatStateOf(0f) }
    val currentOnSelectDate by rememberUpdatedState(onSelectDate)
    val currentDate = selectedDate

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .then(
                    if (calendarScope == CalendarScope.SELECTED_DAY) {
                        Modifier.pointerInput(currentDate) {
                            detectHorizontalDragGestures(
                                onDragStart = { dateDragOffset = 0f },
                                onDragEnd = {
                                    if (dateDragOffset < -50f) {
                                        currentOnSelectDate(currentDate.plusDays(1))
                                    } else if (dateDragOffset > 50f) {
                                        currentOnSelectDate(currentDate.minusDays(1))
                                    }
                                    dateDragOffset = 0f
                                },
                                onDragCancel = { dateDragOffset = 0f },
                                onHorizontalDrag = { _, dragAmount ->
                                    dateDragOffset += dragAmount
                                }
                            )
                        }
                    } else Modifier
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (calendarScope == CalendarScope.SELECTED_DAY) {
                    selectedDate.format(selectedDateHeaderFormatter)
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(currentLocale) else it.toString() }
                } else {
                    if (selectedKeywordFilter != null) {
                        "#$selectedKeywordFilter"
                    } else {
                        stringResource(R.string.calendar_scope_upcoming, totalUpcomingCount)
                    }
                },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = stringResource(R.string.calendar_reminders_count, filteredReminders.size),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = calendarScope == CalendarScope.SELECTED_DAY,
                    onClick = { onSelectScope(CalendarScope.SELECTED_DAY) },
                    label = { Text(stringResource(R.string.calendar_scope_day)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentRed,
                        selectedLabelColor = Color.White,
                        containerColor = DarkSurfaceVariant,
                        labelColor = TextSecondary
                    ),
                    border = null
                )
            }
            item {
                FilterChip(
                    selected = calendarScope == CalendarScope.ALL_UPCOMING && selectedKeywordFilter == null,
                    onClick = {
                        onSelectScope(CalendarScope.ALL_UPCOMING)
                        onSelectKeywordFilter(null)
                    },
                    label = { Text(stringResource(R.string.calendar_scope_upcoming, totalUpcomingCount)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentRed,
                        selectedLabelColor = Color.White,
                        containerColor = DarkSurfaceVariant,
                        labelColor = TextSecondary
                    ),
                    border = null
                )
            }
            item {
                FilterChip(
                    selected = selectedKeywordFilter == "__manual__",
                    onClick = {
                        if (selectedKeywordFilter == "__manual__") {
                            onSelectKeywordFilter(null)
                        } else {
                            onSelectKeywordFilter("__manual__")
                        }
                    },
                    label = { Text(stringResource(R.string.filter_manual)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentRed,
                        selectedLabelColor = Color.White,
                        containerColor = DarkSurfaceVariant,
                        labelColor = TextSecondary
                    ),
                    border = null
                )
            }
            items(activeKeywords, key = { it.id }) { kw ->
                FilterChip(
                    selected = selectedKeywordFilter.equals(kw.keyword, ignoreCase = true),
                    onClick = {
                        if (selectedKeywordFilter.equals(kw.keyword, ignoreCase = true)) {
                            onSelectKeywordFilter(null)
                        } else {
                            onSelectKeywordFilter(kw.keyword)
                        }
                    },
                    label = { Text("#${kw.keyword}") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentRed,
                        selectedLabelColor = Color.White,
                        containerColor = DarkSurfaceVariant,
                        labelColor = TextSecondary
                    ),
                    border = null
                )
            }
        }

        if (filteredReminders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = TextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty()) {
                            stringResource(R.string.search_empty)
                        } else if (calendarScope == CalendarScope.SELECTED_DAY) {
                            stringResource(R.string.calendar_no_reminders_day)
                        } else {
                            stringResource(R.string.calendar_no_reminders_all)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            val groupedReminders = remember(filteredReminders) {
                filteredReminders.groupBy { it.originKeyword?.trim()?.lowercase() }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                groupedReminders.forEach { (keywordKey, remindersInGroup) ->
                    val groupDisplayName = remindersInGroup.firstOrNull()?.originKeyword

                    item(key = "header_${keywordKey ?: "__manual__"}") {
                        HashtagGroupHeader(
                            keyword = groupDisplayName,
                            count = remindersInGroup.size,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }

                    itemsIndexed(
                        items = remindersInGroup,
                        key = { index, reminder -> "${reminder.id}_${reminder.startTimestamp}_$index" }
                    ) { _, reminder ->
                        CalendarReminderItem(
                            reminder = reminder,
                            showDate = calendarScope == CalendarScope.ALL_UPCOMING,
                            onClick = { onProgramClick(reminder.programId) },
                            onDelete = { onDeleteReminder(reminder.programId) },
                            onChannelClick = onChannelClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HashtagGroupHeader(
    keyword: String?,
    count: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = DarkSurfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (keyword != null) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = AccentRed.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "#",
                            style = MaterialTheme.typography.titleSmall,
                            color = AccentRed,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = keyword,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = AccentRed
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.calendar_group_manual),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AccentRed.copy(alpha = 0.15f)
            ) {
                Text(
                    text = stringResource(R.string.calendar_reminders_count, count),
                    style = MaterialTheme.typography.labelSmall,
                    color = AccentRed,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun CalendarReminderItem(
    reminder: Reminder,
    showDate: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onChannelClick: (String) -> Unit = {}
) {
    val zone = com.mohamedaminelouati.tvora.ui.theme.LocalDisplayZone.current
    val zonedDateTime = Instant.ofEpochMilli(reminder.startTimestamp).atZone(zone)
    val timeStr = zonedDateTime.format(timeFormatter)
    val configuration = LocalConfiguration.current
    val currentLocale = remember(configuration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !configuration.locales.isEmpty) {
            configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            configuration.locale ?: Locale.getDefault()
        }
    }
    val dateStr = if (showDate) {
        val dayFormatter = remember(currentLocale) { DateTimeFormatter.ofPattern("EEE d MMM", currentLocale) }
        zonedDateTime.format(dayFormatter).replaceFirstChar { if (it.isLowerCase()) it.titlecase(currentLocale) else it.toString() }
    } else null

    val channelLogoUrl = ChannelCatalog.getLogoUrl(reminder.channelId)
    val channelDisplayName = ChannelCatalog.getDisplayName(reminder.channelId)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = { onChannelClick(reminder.channelId) },
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.08f),
                modifier = Modifier.size(44.dp)
            ) {
                if (!channelLogoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = channelLogoUrl,
                        contentDescription = channelDisplayName,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = channelDisplayName.take(3).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = channelDisplayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentRed,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onChannelClick(reminder.channelId) }
                    )
                    Text(
                        text = if (dateStr != null) "  •  $dateStr, $timeStr" else "  •  $timeStr",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (reminder.leadTimeMinutes == 0) {
                            stringResource(R.string.reminder_at_airtime)
                        } else {
                            stringResource(R.string.reminder_lead_time_format, reminder.leadTimeMinutes)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    reminder.originKeyword?.let { kw ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = AccentRed.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "#$kw",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentRed,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.action_remove_reminder),
                    tint = TextSecondary
                )
            }
        }
    }
}
