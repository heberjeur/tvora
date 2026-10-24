package com.mohamedaminelouati.tvora.ui.detail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mohamedaminelouati.tvora.R
import com.mohamedaminelouati.tvora.data.local.ChannelCatalog
import com.mohamedaminelouati.tvora.domain.model.Program
import com.mohamedaminelouati.tvora.ui.theme.AccentRed
import com.mohamedaminelouati.tvora.ui.theme.DarkSurface
import com.mohamedaminelouati.tvora.ui.theme.TextPrimary
import com.mohamedaminelouati.tvora.ui.theme.TextSecondary
import com.mohamedaminelouati.tvora.util.SeriesUtils
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ProgramUpcomingBroadcastsSection(
    upcomingBroadcasts: List<Program>,
    zone: ZoneId,
    dateFormatter: DateTimeFormatter,
    timeFormatter: DateTimeFormatter,
    onProgramClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (upcomingBroadcasts.isEmpty()) return

    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Tv,
                contentDescription = null,
                tint = AccentRed,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = stringResource(R.string.section_upcoming_broadcasts),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        upcomingBroadcasts.take(5).forEach { upcoming ->
            val upStart = Instant.ofEpochMilli(upcoming.startTimestamp).atZone(zone).format(timeFormatter)
            val upDate = Instant.ofEpochMilli(upcoming.startTimestamp).atZone(zone).format(dateFormatter)

            val parsedEp = remember(upcoming.subTitle) {
                Regex("""(?:[ÉéE]pisode|[ÉéE]p\.?)\s*(\d+)""", RegexOption.IGNORE_CASE)
                    .find(upcoming.subTitle ?: "")?.groupValues?.get(1)?.toIntOrNull()
            }
            val epNum = upcoming.episodeNumber ?: parsedEp
            val parsedSeason = remember(upcoming.subTitle) {
                Regex("""Saisons?\s*(\d+)""", RegexOption.IGNORE_CASE)
                    .find(upcoming.subTitle ?: "")?.groupValues?.get(1)?.toIntOrNull()
            }
            val seasonNum = upcoming.seasonNumber ?: parsedSeason

            val cleanSub = remember(upcoming.subTitle, upcoming.title) {
                SeriesUtils.cleanSubtitle(upcoming.subTitle, upcoming.title)
            }
            val displayTitle = cleanSub ?: upcoming.title

            val fullTitle = when {
                seasonNum != null && epNum != null -> {
                    stringResource(R.string.label_season_episode_broadcast_title, seasonNum, epNum, displayTitle)
                }
                seasonNum != null -> {
                    stringResource(R.string.label_season_with_title, seasonNum, displayTitle)
                }
                epNum != null -> {
                    stringResource(R.string.label_episode_with_title, epNum, displayTitle)
                }
                else -> displayTitle
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onProgramClick(upcoming.id) },
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(8.dp)
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
                        Text(
                            text = ChannelCatalog.getDisplayName(upcoming.channelId),
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentRed,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.broadcast_at_time, upDate, upStart),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = fullTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        maxLines = 3
                    )
                }
            }
        }
    }
}
