package com.mohamedaminelouati.tvora.ui.guide

import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedaminelouati.tvora.R
import com.mohamedaminelouati.tvora.ui.guide.components.CategoryFilterBottomSheet
import com.mohamedaminelouati.tvora.ui.guide.components.ChannelFilterBottomSheet
import com.mohamedaminelouati.tvora.ui.guide.components.DateFilterBottomSheet
import com.mohamedaminelouati.tvora.ui.guide.components.FilterPill
import com.mohamedaminelouati.tvora.ui.guide.components.GuideProgramList
import com.mohamedaminelouati.tvora.ui.guide.components.getCategoryIcon
import com.mohamedaminelouati.tvora.ui.theme.AccentRed
import com.mohamedaminelouati.tvora.ui.theme.LocalDisplayZone
import com.mohamedaminelouati.tvora.util.CategoryUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideScreen(
    onProgramClick: (Long) -> Unit,
    onSettingsClick: () -> Unit = {},
    initialChannelId: String? = null,
    viewModel: GuideViewModel = hiltViewModel()
) {
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedSubCategory by viewModel.selectedSubCategory.collectAsStateWithLifecycle()
    val availableSubCategories by viewModel.availableSubCategories.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val selectedChannelId by viewModel.selectedChannelId.collectAsStateWithLifecycle()
    val channels by viewModel.channels.collectAsStateWithLifecycle()
    val programs by viewModel.programs.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val currentLocale = remember(configuration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !configuration.locales.isEmpty) {
            configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            configuration.locale ?: Locale.getDefault()
        }
    }
    val dayChipFormatter = remember(currentLocale) { DateTimeFormatter.ofPattern("EEE d MMM", currentLocale) }
    val fullDayFormatter = remember(currentLocale) { DateTimeFormatter.ofPattern("EEEE d MMMM", currentLocale) }

    var isSearchActive by remember { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }
    var showChannelSheet by remember { mutableStateOf(false) }
    var showDateSheet by remember { mutableStateOf(false) }

    LaunchedEffect(initialChannelId) {
        if (!initialChannelId.isNullOrBlank()) {
            viewModel.selectChannel(initialChannelId)
        }
    }

    val zone = LocalDisplayZone.current
    val today = remember(zone) { LocalDate.now(zone) }
    val tomorrow = remember(today) { today.plusDays(1) }
    val availableDates = remember(today) {
        (0..10).map { today.plusDays(it.toLong()) }
    }

    val channelNameMap = remember(channels) {
        channels.associate { it.id to it.displayName }
    }

    val activeChannel = remember(selectedChannelId, channels) {
        channels.find { it.id == selectedChannelId }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchActive) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = viewModel::setSearchQuery,
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.search_programs_placeholder),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentRed,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            ),
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = stringResource(R.string.action_clear_search),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 4.dp)
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.tab_guide),
                            color = MaterialTheme.colorScheme.onSurface,
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
                                tint = MaterialTheme.colorScheme.onSurface
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
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            onClick = { viewModel.refresh(force = true) },
                            enabled = !isRefreshing
                        ) {
                            if (isRefreshing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = AccentRed,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = stringResource(R.string.action_refresh),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = stringResource(R.string.tab_settings),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val categoryLabel = when {
                    selectedCategory != null && selectedSubCategory != null -> "${stringResource(selectedCategory!!.nameRes)}: ${CategoryUtils.formatCategory(selectedSubCategory, currentLocale)}"
                    selectedSubCategory != null -> CategoryUtils.formatCategory(selectedSubCategory, currentLocale)
                    selectedCategory != null -> stringResource(selectedCategory!!.nameRes)
                    else -> stringResource(R.string.filter_categories_title)
                }
                FilterPill(
                    isSelected = selectedCategory != null || selectedSubCategory != null,
                    icon = getCategoryIcon(selectedCategory),
                    label = categoryLabel,
                    onClick = { showCategorySheet = true },
                    onClear = { viewModel.clearCategoryFilter() }
                )

                FilterPill(
                    isSelected = activeChannel != null,
                    icon = Icons.Default.LiveTv,
                    channelIconUrl = activeChannel?.iconUrl,
                    label = activeChannel?.displayName ?: stringResource(R.string.filter_channels_title),
                    onClick = { showChannelSheet = true },
                    onClear = { viewModel.selectChannel(null) }
                )

                val dateLabel = when (selectedDate) {
                    null -> stringResource(R.string.filter_dates_title)
                    today -> stringResource(R.string.filter_today)
                    tomorrow -> stringResource(R.string.filter_tomorrow)
                    else -> selectedDate!!.format(dayChipFormatter)
                }
                FilterPill(
                    isSelected = selectedDate != null,
                    icon = if (selectedDate != null) Icons.Default.Today else Icons.Default.CalendarMonth,
                    label = dateLabel,
                    onClick = { showDateSheet = true },
                    onClear = { viewModel.selectDate(null) }
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            GuideProgramList(
                programs = programs,
                selectedDate = selectedDate,
                searchQuery = searchQuery,
                today = today,
                tomorrow = tomorrow,
                zone = zone,
                fullDayFormatter = fullDayFormatter,
                channelNameMap = channelNameMap,
                isLandscape = isLandscape,
                onProgramClick = onProgramClick,
                onCategoryClick = { tag -> viewModel.filterByTag(tag) },
                onChannelClick = { chId -> viewModel.selectChannel(chId) }
            )
        }
    }

    if (showCategorySheet) {
        CategoryFilterBottomSheet(
            selectedCategory = selectedCategory,
            selectedSubCategory = selectedSubCategory,
            availableSubCategories = availableSubCategories,
            currentLocale = currentLocale,
            onCategorySelect = viewModel::selectCategory,
            onSubCategorySelect = viewModel::selectSubCategory,
            onDismiss = { showCategorySheet = false }
        )
    }

    if (showChannelSheet) {
        ChannelFilterBottomSheet(
            channels = channels,
            selectedChannelId = selectedChannelId,
            onChannelSelect = viewModel::selectChannel,
            onDismiss = { showChannelSheet = false }
        )
    }

    if (showDateSheet) {
        DateFilterBottomSheet(
            availableDates = availableDates,
            selectedDate = selectedDate,
            today = today,
            tomorrow = tomorrow,
            fullDayFormatter = fullDayFormatter,
            onDateSelect = viewModel::selectDate,
            onDismiss = { showDateSheet = false }
        )
    }
}
