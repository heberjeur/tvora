package com.mohamedaminelouati.tvora.ui.detail

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedaminelouati.tvora.ui.detail.components.ProgramHeaderSection
import com.mohamedaminelouati.tvora.ui.detail.components.ProgramImageDialog
import com.mohamedaminelouati.tvora.ui.detail.components.ProgramMetadataSection
import com.mohamedaminelouati.tvora.ui.detail.components.ProgramSynopsisSection
import com.mohamedaminelouati.tvora.ui.detail.components.ProgramUpcomingBroadcastsSection
import com.mohamedaminelouati.tvora.ui.detail.components.ReminderScheduleSection
import com.mohamedaminelouati.tvora.ui.theme.AccentRed
import com.mohamedaminelouati.tvora.ui.theme.DarkBackground
import com.mohamedaminelouati.tvora.ui.theme.LocalDisplayZone
import com.mohamedaminelouati.tvora.ui.theme.TextPrimary
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Locale

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramDetailScreen(
    onBackClick: () -> Unit,
    onChannelClick: (String) -> Unit = {},
    onProgramClick: (Long) -> Unit = {},
    onSeasonClick: (Long, Int) -> Unit = { _, _ -> },
    onPersonClick: (String) -> Unit = {},
    viewModel: ProgramDetailViewModel = hiltViewModel()
) {
    val program by viewModel.program.collectAsStateWithLifecycle()
    val reminder by viewModel.reminder.collectAsStateWithLifecycle()
    val upcomingBroadcasts by viewModel.upcomingBroadcasts.collectAsStateWithLifecycle()
    val defaultLeadTime by viewModel.defaultLeadTimeMinutes.collectAsStateWithLifecycle()
    val defaultRecurrence by viewModel.defaultReminderRecurrence.collectAsStateWithLifecycle()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val currentLocale = remember(configuration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !configuration.locales.isEmpty) {
            configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            configuration.locale ?: Locale.getDefault()
        }
    }
    val dateFormatter = remember(currentLocale) { DateTimeFormatter.ofPattern("EEEE d MMMM", currentLocale) }

    var isImageEnlarged by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = program?.title ?: "",
                        color = TextPrimary,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        val currentProgram = program
        if (currentProgram == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentRed)
            }
        } else {
            val zone = LocalDisplayZone.current
            val startTime = Instant.ofEpochMilli(currentProgram.startTimestamp).atZone(zone).format(timeFormatter)
            val stopTime = Instant.ofEpochMilli(currentProgram.stopTimestamp).atZone(zone).format(timeFormatter)
            val broadcastDate = Instant.ofEpochMilli(currentProgram.startTimestamp).atZone(zone).format(dateFormatter)

            val parsedEpFromSub = remember(currentProgram.subTitle) {
                Regex("""(?:[ÉéE]pisode|[ÉéE]p\.?)\s*(\d+)""", RegexOption.IGNORE_CASE)
                    .find(currentProgram.subTitle ?: "")?.groupValues?.get(1)?.toIntOrNull()
            }
            val effectiveEpisodeNumber = parsedEpFromSub ?: currentProgram.episodeNumber

            val parsedSeasonFromSub = remember(currentProgram.subTitle) {
                Regex("""Saisons?\s*(\d+)""", RegexOption.IGNORE_CASE)
                    .find(currentProgram.subTitle ?: "")?.groupValues?.get(1)?.toIntOrNull()
            }
            val effectiveSeasonNumber = parsedSeasonFromSub ?: currentProgram.seasonNumber

            val isSeriesProgram = currentProgram.seasonNumber != null ||
                currentProgram.seriesYear != null ||
                currentProgram.categories.any { it.equals("Série", ignoreCase = true) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                ProgramHeaderSection(
                    program = currentProgram,
                    isLandscape = isLandscape,
                    broadcastDate = broadcastDate,
                    startTime = startTime,
                    stopTime = stopTime,
                    effectiveSeasonNumber = effectiveSeasonNumber,
                    effectiveEpisodeNumber = effectiveEpisodeNumber,
                    onChannelClick = onChannelClick,
                    onSeasonClick = onSeasonClick,
                    onImageClick = { isImageEnlarged = true }
                )

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ProgramMetadataSection(
                        program = currentProgram,
                        effectiveSeasonNumber = effectiveSeasonNumber,
                        effectiveEpisodeNumber = effectiveEpisodeNumber,
                        isSeriesProgram = isSeriesProgram,
                        currentLocale = currentLocale,
                        onPersonClick = onPersonClick
                    )

                    ReminderScheduleSection(
                        program = currentProgram,
                        reminder = reminder,
                        defaultLeadTime = defaultLeadTime,
                        defaultRecurrence = defaultRecurrence,
                        onToggleReminder = { lead, recurrence ->
                            viewModel.toggleReminder(lead, recurrence)
                        },
                        onCustomDateTimeReminder = { time, recurrence ->
                            viewModel.setCustomDateTimeReminder(time, recurrence)
                        },
                        createCalendarIntent = { prog ->
                            viewModel.createExternalCalendarIntent(prog)
                        }
                    )

                    ProgramSynopsisSection(
                        program = currentProgram,
                        isSeriesProgram = isSeriesProgram
                    )

                    ProgramUpcomingBroadcastsSection(
                        upcomingBroadcasts = upcomingBroadcasts,
                        zone = zone,
                        dateFormatter = dateFormatter,
                        timeFormatter = timeFormatter,
                        onProgramClick = onProgramClick
                    )
                }
            }
        }
    }

    val currentImageUrl = program?.iconUrl
    if (isImageEnlarged && !currentImageUrl.isNullOrBlank()) {
        ProgramImageDialog(
            imageUrl = currentImageUrl,
            title = program?.title ?: "",
            onDismiss = { isImageEnlarged = false }
        )
    }
}
