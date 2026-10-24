package com.mohamedaminelouati.tvora.ui.guide.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mohamedaminelouati.tvora.R
import com.mohamedaminelouati.tvora.domain.model.Program
import com.mohamedaminelouati.tvora.ui.components.ProgramCard
import com.mohamedaminelouati.tvora.ui.theme.AccentRed
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun GuideProgramList(
    programs: List<Program>,
    selectedDate: LocalDate?,
    searchQuery: String,
    today: LocalDate,
    tomorrow: LocalDate,
    zone: ZoneId,
    fullDayFormatter: DateTimeFormatter,
    channelNameMap: Map<String, String>,
    isLandscape: Boolean,
    onProgramClick: (Long) -> Unit,
    onCategoryClick: (String) -> Unit,
    onChannelClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (programs.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (searchQuery.isNotEmpty()) {
                        stringResource(R.string.search_empty)
                    } else {
                        stringResource(R.string.no_programs_for_date)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else if (isLandscape) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (selectedDate == null) {
                val grouped = programs.groupBy { prog ->
                    Instant.ofEpochMilli(prog.startTimestamp).atZone(zone).toLocalDate()
                }
                grouped.forEach { (date, datePrograms) ->
                    item(key = "header_$date", span = { GridItemSpan(maxLineSpan) }) {
                        val formattedDate = date.format(fullDayFormatter).replaceFirstChar { it.uppercase() }
                        val headerTitle = when (date) {
                            today -> stringResource(R.string.filter_today_prefix, formattedDate)
                            tomorrow -> stringResource(R.string.filter_tomorrow_prefix, formattedDate)
                            else -> formattedDate
                        }
                        Text(
                            text = headerTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AccentRed,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 4.dp)
                        )
                    }
                    items(
                        items = datePrograms,
                        key = { it.id }
                    ) { program ->
                        ProgramCard(
                            program = program,
                            onClick = { onProgramClick(program.id) },
                            channelName = channelNameMap[program.channelId],
                            onCategoryClick = onCategoryClick,
                            onChannelClick = onChannelClick
                        )
                    }
                }
            } else {
                items(
                    items = programs,
                    key = { it.id }
                ) { program ->
                    ProgramCard(
                        program = program,
                        onClick = { onProgramClick(program.id) },
                        channelName = channelNameMap[program.channelId],
                        onCategoryClick = onCategoryClick,
                        onChannelClick = onChannelClick
                    )
                }
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (selectedDate == null) {
                val grouped = programs.groupBy { prog ->
                    Instant.ofEpochMilli(prog.startTimestamp).atZone(zone).toLocalDate()
                }
                grouped.forEach { (date, datePrograms) ->
                    item(key = "header_$date") {
                        val formattedDate = date.format(fullDayFormatter).replaceFirstChar { it.uppercase() }
                        val headerTitle = when (date) {
                            today -> stringResource(R.string.filter_today_prefix, formattedDate)
                            tomorrow -> stringResource(R.string.filter_tomorrow_prefix, formattedDate)
                            else -> formattedDate
                        }
                        Text(
                            text = headerTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AccentRed,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 4.dp)
                        )
                    }
                    items(
                        items = datePrograms,
                        key = { it.id }
                    ) { program ->
                        ProgramCard(
                            program = program,
                            onClick = { onProgramClick(program.id) },
                            channelName = channelNameMap[program.channelId],
                            onCategoryClick = onCategoryClick,
                            onChannelClick = onChannelClick
                        )
                    }
                }
            } else {
                items(
                    items = programs,
                    key = { it.id }
                ) { program ->
                    ProgramCard(
                        program = program,
                        onClick = { onProgramClick(program.id) },
                        channelName = channelNameMap[program.channelId],
                        onCategoryClick = onCategoryClick,
                        onChannelClick = onChannelClick
                    )
                }
            }
        }
    }
}
