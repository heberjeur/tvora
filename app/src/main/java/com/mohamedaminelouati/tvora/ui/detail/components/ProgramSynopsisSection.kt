package com.mohamedaminelouati.tvora.ui.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Description
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
import com.mohamedaminelouati.tvora.domain.model.Program
import com.mohamedaminelouati.tvora.ui.theme.AccentRed
import com.mohamedaminelouati.tvora.ui.theme.TextPrimary
import com.mohamedaminelouati.tvora.util.ProgramDescriptionUtils

@Composable
fun ProgramSynopsisSection(
    program: Program,
    isSeriesProgram: Boolean,
    modifier: Modifier = Modifier
) {
    val cleanedDescription = remember(program.description, program.title) {
        ProgramDescriptionUtils.cleanWithTitlePrefix(program.description, program.title)
    }

    val cleanedSecondaryDescription = remember(program.secondaryDescription, program.title, cleanedDescription) {
        val secondary = ProgramDescriptionUtils.cleanWithTitlePrefix(program.secondaryDescription, program.title)
        if (secondary != null && !ProgramDescriptionUtils.isRedundant(cleanedDescription, secondary)) {
            secondary
        } else {
            null
        }
    }

    val primaryDescriptionHeader = when {
        isSeriesProgram && !cleanedSecondaryDescription.isNullOrBlank() -> stringResource(R.string.section_episode_description)
        isSeriesProgram && program.seasonNumber == null && program.episodeNumber == null -> stringResource(R.string.section_series_description)
        else -> stringResource(R.string.section_description)
    }

    val secondaryDescriptionHeader = if (isSeriesProgram) {
        stringResource(R.string.section_series_description)
    } else {
        stringResource(R.string.section_more_details)
    }

    Column(modifier = modifier) {
        if (!cleanedDescription.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = AccentRed,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = primaryDescriptionHeader,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            SelectionContainer {
                Text(
                    text = cleanedDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.3
                )
            }
        }

        if (!cleanedSecondaryDescription.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = null,
                    tint = AccentRed,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = secondaryDescriptionHeader,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            SelectionContainer {
                Text(
                    text = cleanedSecondaryDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.3
                )
            }
        }
    }
}
