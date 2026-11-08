package com.mohamedaminelouati.tvora.ui.settings.sections

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mohamedaminelouati.tvora.R
import com.mohamedaminelouati.tvora.data.local.PresetChannelsData
import com.mohamedaminelouati.tvora.domain.model.Channel
import com.mohamedaminelouati.tvora.ui.settings.components.SettingsCardHeader
import com.mohamedaminelouati.tvora.ui.theme.AccentRed

@Composable
fun AdditionalChannelsSettingsSection(
    customChannels: List<Channel>,
    onNavigateToChannelManagement: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCatalogCount = PresetChannelsData.all.size

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SettingsCardHeader(
                icon = Icons.Default.AddCircleOutline,
                title = stringResource(R.string.settings_custom_channels_title)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.settings_custom_channels_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onNavigateToChannelManagement)
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(
                            R.string.settings_custom_channels_summary,
                            customChannels.size,
                            totalCatalogCount
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (customChannels.isNotEmpty()) AccentRed else MaterialTheme.colorScheme.onSurface
                    )
                }

                OutlinedButton(
                    onClick = onNavigateToChannelManagement,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.settings_custom_channels_manage_btn),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
