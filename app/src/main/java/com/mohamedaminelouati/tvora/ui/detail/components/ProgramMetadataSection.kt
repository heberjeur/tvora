package com.mohamedaminelouati.tvora.ui.detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mohamedaminelouati.tvora.R
import com.mohamedaminelouati.tvora.domain.model.Program
import com.mohamedaminelouati.tvora.ui.theme.DarkSurface
import com.mohamedaminelouati.tvora.ui.theme.TextPrimary
import com.mohamedaminelouati.tvora.util.CategoryUtils
import com.mohamedaminelouati.tvora.util.CountryUtils
import java.util.Locale

@Composable
fun ProgramMetadataSection(
    program: Program,
    effectiveSeasonNumber: Int?,
    effectiveEpisodeNumber: Int?,
    isSeriesProgram: Boolean,
    currentLocale: Locale,
    onPersonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.section_information),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (effectiveSeasonNumber != null) {
                val epText = if (effectiveEpisodeNumber != null) {
                    stringResource(R.string.label_season_episode_short, effectiveSeasonNumber, effectiveEpisodeNumber)
                } else {
                    stringResource(R.string.label_season_value, effectiveSeasonNumber)
                }
                InfoRow(
                    label = stringResource(R.string.label_season),
                    value = epText
                )
            } else if (effectiveEpisodeNumber != null) {
                InfoRow(
                    label = stringResource(R.string.label_episode),
                    value = stringResource(R.string.label_episode_value, effectiveEpisodeNumber)
                )
            }

            val formatKeywords = listOf(
                "Cinéma", "Cinema", "Film", "Téléfilm", "Telefilm", "Court-métrage", "Court-metrage",
                "Série", "Serie", "Série TV", "Mini-série", "Feuilleton",
                "Documentaire", "Magazine", "Journal", "Divertissement", "Jeu",
                "Sport", "Jeunesse", "Musique", "Culture Infos", "Info", "Information", "Autre",
                "Movie", "TV Movie", "Short Film", "Medium-length Film",
                "Series", "TV Series", "Mini-series", "Drama Series",
                "Documentary", "Docudrama", "Magazine", "News", "TV News", "News & Current Affairs",
                "Entertainment", "Game Show", "Game Shows", "Reality TV", "Show", "Theater",
                "Sports", "Kids", "Cartoon", "Animation", "Anime",
                "Music", "Musical", "Concert", "Opera", "Culture & News", "Culture", "Other"
            )
            val metaKeywords = listOf(
                "Rediffusion", "Direct", "En direct", "Inédit", "Inedite", "Inédite",
                "Audiodescription", "Sous-titres", "General",
                "Rerun", "Repeat", "Live", "Premiere", "Unreleased", "Audio Description", "Subtitles"
            )

            val formatValue = program.categories.firstOrNull { it.equals("Cinéma", ignoreCase = true) }
                ?: program.categories.firstOrNull { cat -> formatKeywords.any { it.equals(cat, ignoreCase = true) } }
                ?: program.categories.firstOrNull { cat -> !metaKeywords.any { it.equals(cat, ignoreCase = true) } }

            val specificGenres = program.categories.filter { cat ->
                !cat.equals(formatValue, ignoreCase = true) &&
                !formatKeywords.any { it.equals(cat, ignoreCase = true) } &&
                !metaKeywords.any { it.equals(cat, ignoreCase = true) }
            }

            if (!formatValue.isNullOrBlank()) {
                InfoRow(
                    label = stringResource(R.string.label_format),
                    value = CategoryUtils.formatCategory(formatValue, currentLocale)
                )
            }
            if (specificGenres.isNotEmpty()) {
                InfoRow(
                    label = stringResource(R.string.label_genre),
                    value = CategoryUtils.formatGenreList(specificGenres, currentLocale)
                )
            }

            if (isSeriesProgram) {
                if (program.productionYear != null) {
                    InfoRow(
                        label = stringResource(R.string.label_episode_release_year),
                        value = program.productionYear.toString()
                    )
                }
                if (program.seriesYear != null) {
                    InfoRow(
                        label = stringResource(R.string.label_series_release_year),
                        value = program.seriesYear.toString()
                    )
                }
            } else {
                val releaseYear = program.productionYear ?: program.seriesYear
                if (releaseYear != null) {
                    InfoRow(
                        label = stringResource(R.string.label_release_year),
                        value = releaseYear.toString()
                    )
                }
            }

            val broadcastType = when {
                program.isLiveBroadcast -> stringResource(R.string.status_live)
                program.isRerun -> stringResource(R.string.status_rerun)
                program.isUnreleasedResolved -> stringResource(R.string.status_unreleased)
                else -> null
            }
            if (broadcastType != null) {
                InfoRow(
                    label = stringResource(R.string.label_broadcast_type),
                    value = broadcastType
                )
            }

            val accessibilityText = when {
                program.hasSubtitlesResolved && program.hasAudioDescription -> {
                    stringResource(R.string.accessibility_subtitles_and_ad)
                }
                program.hasSubtitlesResolved -> {
                    stringResource(R.string.badge_subtitles_long)
                }
                program.hasAudioDescription -> {
                    stringResource(R.string.badge_ad_long)
                }
                else -> null
            }
            if (accessibilityText != null) {
                InfoRow(
                    label = stringResource(R.string.label_accessibility),
                    value = accessibilityText
                )
            }

            val ratingDisplay = when (program.contentRating?.trim()) {
                "-10", "10" -> stringResource(R.string.csa_under_10)
                "-12", "12", "13" -> stringResource(R.string.csa_under_12)
                "-16", "16" -> stringResource(R.string.csa_under_16)
                "-18", "18" -> stringResource(R.string.csa_under_18)
                "Tout public", "TP", "G", "Tous publics" -> stringResource(R.string.csa_all_audiences)
                null, "" -> null
                else -> program.contentRating.trim()
            }
            if (!ratingDisplay.isNullOrBlank()) {
                InfoRow(
                    label = stringResource(R.string.label_parental_rating),
                    value = ratingDisplay
                )
            }

            val rawCountry = program.country
                ?: if (program.channelId.endsWith(".fr")) {
                    stringResource(R.string.default_country)
                } else null
            val countryText = remember(rawCountry, currentLocale) {
                CountryUtils.formatCountry(rawCountry, currentLocale)
            }

            if (!countryText.isNullOrBlank()) {
                InfoRow(
                    label = stringResource(R.string.label_country),
                    value = countryText
                )
            }

            val validDirectors = program.credit.directors.map { it.trim() }.filter { it.isNotBlank() }.distinct()
            if (validDirectors.isNotEmpty()) {
                PeopleInfoRow(
                    label = if (validDirectors.size > 1) {
                        stringResource(R.string.label_directors)
                    } else {
                        stringResource(R.string.label_director)
                    },
                    people = validDirectors,
                    onPersonClick = onPersonClick
                )
            }

            val validPresenters = program.credit.presenters.map { it.trim() }.filter { it.isNotBlank() }.distinct()
            if (validPresenters.isNotEmpty()) {
                PeopleInfoRow(
                    label = stringResource(R.string.label_presented_by),
                    people = validPresenters,
                    onPersonClick = onPersonClick
                )
            }

            val validActors = program.credit.actors.map { it.trim() }.filter { it.isNotBlank() }.distinct()
            if (validActors.isNotEmpty()) {
                PeopleInfoRow(
                    label = if (validActors.size > 1) {
                        stringResource(R.string.label_actors)
                    } else {
                        stringResource(R.string.label_actor)
                    },
                    people = validActors,
                    onPersonClick = onPersonClick
                )
            }

            val validGuests = program.credit.guests.map { it.trim() }.filter { it.isNotBlank() }.distinct()
            if (validGuests.isNotEmpty()) {
                PeopleInfoRow(
                    label = stringResource(R.string.label_guests),
                    people = validGuests,
                    onPersonClick = onPersonClick
                )
            }

            if (program.isHd) {
                InfoRow(
                    label = stringResource(R.string.label_video_format),
                    value = stringResource(R.string.video_format_hd)
                )
            }

            if (program.starRating != null) {
                InfoRow(
                    label = stringResource(R.string.label_rating),
                    value = "★ ${"%.1f".format(Locale.US, program.starRating)}/5"
                )
            }
        }
    }
}
