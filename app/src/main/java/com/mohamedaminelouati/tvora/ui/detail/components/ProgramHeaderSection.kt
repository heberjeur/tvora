package com.mohamedaminelouati.tvora.ui.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mohamedaminelouati.tvora.R
import com.mohamedaminelouati.tvora.data.local.ChannelCatalog
import com.mohamedaminelouati.tvora.domain.model.Program
import com.mohamedaminelouati.tvora.ui.theme.AccentRed
import com.mohamedaminelouati.tvora.ui.theme.DarkBackground
import com.mohamedaminelouati.tvora.ui.theme.TextPrimary
import com.mohamedaminelouati.tvora.ui.theme.TextSecondary

@Composable
fun ProgramHeaderSection(
    program: Program,
    isLandscape: Boolean,
    broadcastDate: String,
    startTime: String,
    stopTime: String,
    effectiveSeasonNumber: Int?,
    effectiveEpisodeNumber: Int?,
    onChannelClick: (String) -> Unit,
    onSeasonClick: (Long, Int) -> Unit,
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (!program.iconUrl.isNullOrBlank() && !program.iconUrl.startsWith("data:")) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isLandscape) 160.dp else 240.dp)
                    .clickable(onClick = onImageClick)
            ) {
                AsyncImage(
                    model = program.iconUrl,
                    contentDescription = program.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, DarkBackground),
                                startY = 100f
                            )
                        )
                )
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clickable(onClick = onImageClick)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fullscreen,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onChannelClick(program.channelId) }
                    .padding(vertical = 4.dp)
            ) {
                val channelLogoUrl = ChannelCatalog.getLogoUrl(program.channelId)
                val channelDisplayName = ChannelCatalog.getDisplayName(program.channelId)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    modifier = Modifier.size(36.dp)
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

                Column {
                    Text(
                        text = channelDisplayName,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = AccentRed
                    )
                    Text(
                        text = "${broadcastDate.replaceFirstChar { it.uppercase() }}  •  $startTime - $stopTime  •  ${program.formattedDuration}",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val episodeTitle = remember(program.subTitle, program.title) {
                val clean = program.subTitle?.trim()
                if (clean.isNullOrEmpty()) {
                    null
                } else {
                    val stripped = clean
                        .replace(Regex("""^(?:(?:Saison|Season|S)\s*\d+\s*[-:•]?\s*|(?:[ÉéE]pisode|[ÉéE]p\.?|E)\s*\d+\s*[-:•]?\s*)+""", RegexOption.IGNORE_CASE), "")
                        .replace(Regex("""\s*[-:•(]?\s*(?:(?:Saison|Season|S)\s*\d+|(?:[ÉéE]pisode|[ÉéE]p\.?|E)\s*\d+)\s*\)?$""", RegexOption.IGNORE_CASE), "")
                        .trim()
                    val isRepeat = stripped.equals(program.title.trim(), ignoreCase = true)
                    if (stripped.isNotEmpty() && !isRepeat) stripped else null
                }
            }

            SelectionContainer {
                Column {
                    Text(
                        text = program.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    if (!episodeTitle.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = episodeTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary
                        )
                    }
                }
            }

            val hasEpisodeButton = effectiveEpisodeNumber != null
            val hasSeasonButton = effectiveSeasonNumber != null

            if (hasEpisodeButton || hasSeasonButton) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (hasEpisodeButton) {
                        DetailBadgeButton(
                            text = stringResource(R.string.label_episode_value, effectiveEpisodeNumber!!),
                            onClick = {
                                onSeasonClick(program.id, effectiveSeasonNumber ?: 1)
                            }
                        )
                    }
                    if (hasSeasonButton) {
                        DetailBadgeButton(
                            text = stringResource(R.string.label_season_value, effectiveSeasonNumber!!),
                            onClick = {
                                onSeasonClick(program.id, effectiveSeasonNumber)
                            }
                        )
                    }
                }
            }
        }
    }
}
