package com.mohamedaminelouati.tvora.ui.settings.sections

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mohamedaminelouati.tvora.R
import com.mohamedaminelouati.tvora.ui.settings.components.SettingsCardHeader
import com.mohamedaminelouati.tvora.ui.theme.AccentRed

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LanguageSettingsSection(
    appLanguage: String,
    activity: Activity?,
    onSetLanguage: (String, Activity?) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SettingsCardHeader(
                icon = Icons.Default.Language,
                title = stringResource(R.string.settings_language_title)
            )
            Spacer(modifier = Modifier.height(14.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "fr" to stringResource(R.string.language_french),
                    "en" to stringResource(R.string.language_english)
                ).forEach { (lang, label) ->
                    FilterChip(
                        selected = appLanguage == lang,
                        onClick = { onSetLanguage(lang, activity) },
                        label = { Text(text = label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentRed,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = null,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }
    }
}
