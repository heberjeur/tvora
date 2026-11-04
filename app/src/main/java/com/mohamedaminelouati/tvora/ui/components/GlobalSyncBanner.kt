package com.mohamedaminelouati.tvora.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mohamedaminelouati.tvora.R
import com.mohamedaminelouati.tvora.domain.model.SyncProgress
import com.mohamedaminelouati.tvora.domain.model.SyncState
import com.mohamedaminelouati.tvora.ui.theme.AccentRed
import com.mohamedaminelouati.tvora.ui.theme.DarkSurfaceVariant
import com.mohamedaminelouati.tvora.ui.theme.IndicatorGreen
import com.mohamedaminelouati.tvora.ui.theme.TextPrimary
import com.mohamedaminelouati.tvora.ui.theme.TextSecondary

@Composable
fun GlobalSyncBanner(
    syncProgress: SyncProgress,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isDismissed by remember(syncProgress.state) { mutableStateOf(false) }

    val syncMessage = when (syncProgress.state) {
        SyncState.DOWNLOADING -> stringResource(R.string.sync_state_downloading)
        SyncState.PARSING -> stringResource(R.string.sync_state_parsing)
        SyncState.CACHING_IMAGES -> stringResource(R.string.sync_state_caching_images)
        SyncState.SUCCESS -> stringResource(R.string.sync_state_success, syncProgress.programCount)
        SyncState.ERROR -> stringResource(R.string.sync_state_error)
        SyncState.IDLE -> ""
    }

    val visible = !isDismissed && syncProgress.state != SyncState.IDLE && syncMessage.isNotBlank()

    AnimatedVisibility(
        visible = visible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(DarkSurfaceVariant)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (syncProgress.state == SyncState.SUCCESS) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = IndicatorGreen,
                        modifier = Modifier.size(18.dp)
                    )
                } else if (syncProgress.state == SyncState.ERROR) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = null,
                        tint = AccentRed,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = AccentRed,
                        strokeWidth = 2.dp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = syncMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        isDismissed = true
                        onDismiss()
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

