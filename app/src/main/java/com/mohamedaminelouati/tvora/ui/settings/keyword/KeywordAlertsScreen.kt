package com.mohamedaminelouati.tvora.ui.settings.keyword

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedaminelouati.tvora.R
import com.mohamedaminelouati.tvora.ui.theme.AccentRed
import com.mohamedaminelouati.tvora.ui.theme.DarkBackground
import com.mohamedaminelouati.tvora.ui.theme.DarkSurface
import com.mohamedaminelouati.tvora.ui.theme.DarkSurfaceVariant
import com.mohamedaminelouati.tvora.ui.theme.SurfaceBorder
import com.mohamedaminelouati.tvora.ui.theme.TextPrimary
import com.mohamedaminelouati.tvora.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeywordAlertsScreen(
    onBackClick: () -> Unit,
    viewModel: KeywordAlertsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val keywordAlerts by viewModel.keywordAlerts.collectAsStateWithLifecycle()
    val defaultSound by viewModel.defaultNotificationSound.collectAsStateWithLifecycle()
    val inputError by viewModel.inputError.collectAsStateWithLifecycle()
    var inputText by remember { mutableStateOf("") }
    var itemToDelete by remember { mutableStateOf<KeywordAlertItemUiState?>(null) }
    var selectedKeywordIdForSound by remember { mutableStateOf<Long?>(null) }

    val ringtonePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            }
            selectedKeywordIdForSound?.let { id ->
                viewModel.setKeywordSound(id, uri?.toString())
            }
        }
        selectedKeywordIdForSound = null
    }

    val handleAdd = {
        if (inputText.isNotBlank()) {
            viewModel.addKeyword(inputText, soundUri = defaultSound)
            if (inputError == null) {
                inputText = ""
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.keyword_alerts_title),
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = {
                                inputText = it
                                if (inputError != null) viewModel.clearError()
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.keyword_input_placeholder),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            singleLine = true,
                            isError = inputError != null,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { handleAdd() }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = AccentRed,
                                unfocusedBorderColor = SurfaceBorder,
                                errorBorderColor = MaterialTheme.colorScheme.error,
                                errorTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = handleAdd,
                            colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.action_add),
                                tint = Color.White
                            )
                        }
                    }

                    if (inputError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (inputError) {
                                "duplicate" -> stringResource(R.string.keyword_error_duplicate)
                                else -> stringResource(R.string.keyword_error_empty)
                            },
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            if (keywordAlerts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = TextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = stringResource(R.string.empty_keywords_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.empty_keywords_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(keywordAlerts, key = { it.alert.id }) { item ->
                        val soundDisplayName = remember(item.alert.soundUri, defaultSound) {
                            val uriStringToResolve = item.alert.soundUri ?: defaultSound
                            if (uriStringToResolve != null) {
                                try {
                                    val ringtone = RingtoneManager.getRingtone(context, Uri.parse(uriStringToResolve))
                                    ringtone?.getTitle(context) ?: context.getString(R.string.keyword_sound_default)
                                } catch (_: SecurityException) {
                                    context.getString(R.string.keyword_sound_default)
                                } catch (_: IllegalArgumentException) {
                                    context.getString(R.string.keyword_sound_default)
                                }
                            } else {
                                context.getString(R.string.keyword_sound_default)
                            }
                        }

                        KeywordAlertCard(
                            item = item,
                            soundTitle = soundDisplayName,
                            onToggleEnabled = { enabled ->
                                viewModel.setKeywordEnabled(item.alert.id, enabled)
                            },
                            onPickSoundClick = {
                                selectedKeywordIdForSound = item.alert.id
                                val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                                    putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
                                    putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                                    putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
                                    val existing = item.alert.soundUri ?: defaultSound
                                    existing?.let {
                                        putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(it))
                                    }
                                }
                                ringtonePickerLauncher.launch(intent)
                            },
                            onResetSoundClick = if (item.alert.soundUri != null) {
                                { viewModel.setKeywordSound(item.alert.id, null) }
                            } else null,
                            onDeleteClick = {
                                itemToDelete = item
                            }
                        )
                    }
                }
            }
        }
    }

    itemToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = {
                Text(
                    text = stringResource(R.string.delete_keyword_dialog_title),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.delete_keyword_dialog_desc),
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteKeyword(target.alert.id, target.alert.keyword, deleteReminders = true)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text(stringResource(R.string.action_delete_keyword_with_reminders))
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = { itemToDelete = null }) {
                        Text(stringResource(R.string.action_cancel), color = TextSecondary)
                    }
                    TextButton(
                        onClick = {
                            viewModel.deleteKeyword(target.alert.id, target.alert.keyword, deleteReminders = false)
                            itemToDelete = null
                        }
                    ) {
                        Text(stringResource(R.string.action_delete_keyword_only), color = TextPrimary)
                    }
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
private fun KeywordAlertCard(
    item: KeywordAlertItemUiState,
    soundTitle: String,
    onToggleEnabled: (Boolean) -> Unit,
    onPickSoundClick: () -> Unit,
    onResetSoundClick: (() -> Unit)? = null,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = AccentRed.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "#",
                            style = MaterialTheme.typography.titleMedium,
                            color = AccentRed,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.alert.keyword,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = TextSecondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.keyword_upcoming_count, item.upcomingCount),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (item.upcomingCount > 0) AccentRed else TextSecondary,
                            fontWeight = if (item.upcomingCount > 0) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (item.alert.soundUri != null) AccentRed.copy(alpha = 0.15f) else DarkSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.clickable(onClick = onPickSoundClick)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = if (item.alert.soundUri != null) Icons.Default.MusicNote else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = if (item.alert.soundUri != null) AccentRed else TextSecondary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = soundTitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (item.alert.soundUri != null) AccentRed else TextSecondary,
                                fontWeight = if (item.alert.soundUri != null) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Switch(
                checked = item.alert.isEnabled,
                onCheckedChange = onToggleEnabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AccentRed,
                    uncheckedThumbColor = Color(0xFFD6D6D6),
                    uncheckedTrackColor = DarkSurfaceVariant,
                    uncheckedBorderColor = SurfaceBorder
                )
            )

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.action_delete_keyword_only),
                    tint = TextSecondary
                )
            }
        }
    }
}
