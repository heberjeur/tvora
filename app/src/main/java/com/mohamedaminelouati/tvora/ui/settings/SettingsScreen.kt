package com.mohamedaminelouati.tvora.ui.settings

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedaminelouati.tvora.R
import com.mohamedaminelouati.tvora.ui.settings.dialogs.CustomColorDialog
import com.mohamedaminelouati.tvora.ui.settings.dialogs.FavoritesOrderDialog
import com.mohamedaminelouati.tvora.ui.settings.dialogs.HiddenChannelsDialog
import com.mohamedaminelouati.tvora.ui.settings.dialogs.TimezoneDialog
import com.mohamedaminelouati.tvora.ui.settings.sections.AppearanceSettingsSection
import com.mohamedaminelouati.tvora.ui.settings.sections.ChannelSettingsSection
import com.mohamedaminelouati.tvora.ui.settings.sections.DisplayBadgesSettingsSection
import com.mohamedaminelouati.tvora.ui.settings.sections.LanguageSettingsSection
import com.mohamedaminelouati.tvora.ui.settings.sections.NotificationSettingsSection
import com.mohamedaminelouati.tvora.ui.settings.sections.StorageSettingsSection
import com.mohamedaminelouati.tvora.ui.settings.sections.SyncSettingsSection
import com.mohamedaminelouati.tvora.ui.settings.sections.TimezoneSettingsSection
import com.mohamedaminelouati.tvora.ui.settings.sections.WidgetSettingsSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: (() -> Unit)? = null,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = remember(context) {
        var ctx = context
        while (ctx is ContextWrapper) {
            if (ctx is Activity) return@remember ctx
            ctx = ctx.baseContext
        }
        null
    }

    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val accentColor by viewModel.accentColor.collectAsStateWithLifecycle()
    val isNotificationsEnabled by viewModel.isNotificationsEnabled.collectAsStateWithLifecycle()
    val reminderLeadTime by viewModel.reminderLeadTime.collectAsStateWithLifecycle()
    val reminderRecurrence by viewModel.reminderRecurrence.collectAsStateWithLifecycle()
    val syncFrequencyHours by viewModel.syncFrequencyHours.collectAsStateWithLifecycle()
    val autoSyncEnabled by viewModel.autoSyncEnabled.collectAsStateWithLifecycle()
    val syncWifiOnly by viewModel.syncWifiOnly.collectAsStateWithLifecycle()
    val syncChargingOnly by viewModel.syncChargingOnly.collectAsStateWithLifecycle()
    val retentionDays by viewModel.retentionDays.collectAsStateWithLifecycle()
    val hideReruns by viewModel.hideReruns.collectAsStateWithLifecycle()
    val highlightAccessibility by viewModel.highlightAccessibility.collectAsStateWithLifecycle()
    val showCsaRatings by viewModel.showCsaRatings.collectAsStateWithLifecycle()
    val showChannelNumbers by viewModel.showChannelNumbers.collectAsStateWithLifecycle()
    val channelSortOrder by viewModel.channelSortOrder.collectAsStateWithLifecycle()
    val hiddenChannels by viewModel.hiddenChannels.collectAsStateWithLifecycle()
    val favoriteChannels by viewModel.favoriteChannels.collectAsStateWithLifecycle()
    val favoriteChannelsOrder by viewModel.favoriteChannelsOrder.collectAsStateWithLifecycle()
    val allChannels by viewModel.allChannels.collectAsStateWithLifecycle()
    val cacheSizeText by viewModel.cacheSizeText.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val autoTranslateEnabled by viewModel.autoTranslateEnabled.collectAsStateWithLifecycle()
    val translateWifiOnly by viewModel.translateWifiOnly.collectAsStateWithLifecycle()
    val isEnglishModelDownloaded by viewModel.isEnglishModelDownloaded.collectAsStateWithLifecycle()
    val isDownloadingEnglishModel by viewModel.isDownloadingEnglishModel.collectAsStateWithLifecycle()
    val displayTimezone by viewModel.displayTimezone.collectAsStateWithLifecycle()
    val notificationSound by viewModel.notificationSound.collectAsStateWithLifecycle()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsStateWithLifecycle()
    val widgetTheme by viewModel.widgetTheme.collectAsStateWithLifecycle()
    val widgetShowProgress by viewModel.widgetShowProgress.collectAsStateWithLifecycle()

    var showFavoritesDialog by remember { mutableStateOf(false) }
    var showHiddenDialog by remember { mutableStateOf(false) }
    var showTimezoneDialog by remember { mutableStateOf(false) }
    var showCustomColorDialog by remember { mutableStateOf(false) }

    val soundDisplayName = remember(notificationSound) {
        if (notificationSound != null) {
            try {
                val ringtone = RingtoneManager.getRingtone(context, Uri.parse(notificationSound))
                ringtone?.getTitle(context) ?: context.getString(R.string.settings_sound_default)
            } catch (_: SecurityException) {
                context.getString(R.string.settings_sound_default)
            } catch (_: IllegalArgumentException) {
                context.getString(R.string.settings_sound_default)
            }
        } else {
            context.getString(R.string.settings_sound_default)
        }
    }

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
            viewModel.setNotificationSound(uri?.toString())
        }
    }

    if (showFavoritesDialog) {
        FavoritesOrderDialog(
            allChannels = allChannels,
            favoriteChannels = favoriteChannels,
            favoriteChannelsOrder = favoriteChannelsOrder,
            onMoveFavorite = viewModel::moveFavoriteChannel,
            onToggleFavorite = viewModel::toggleFavoriteChannel,
            onDismiss = { showFavoritesDialog = false }
        )
    }

    if (showHiddenDialog) {
        HiddenChannelsDialog(
            allChannels = allChannels,
            hiddenChannels = hiddenChannels,
            onToggleHidden = viewModel::toggleHiddenChannel,
            onDismiss = { showHiddenDialog = false }
        )
    }

    if (showTimezoneDialog) {
        TimezoneDialog(
            displayTimezone = displayTimezone,
            onSelectTimezone = {
                viewModel.setDisplayTimezone(it)
                showTimezoneDialog = false
            },
            onDismiss = { showTimezoneDialog = false }
        )
    }

    if (showCustomColorDialog) {
        CustomColorDialog(
            initialColorHex = accentColor,
            onDismiss = { showCustomColorDialog = false },
            onColorSelected = { selectedHex ->
                viewModel.setAccentColor(selectedHex)
                showCustomColorDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.tab_settings),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppearanceSettingsSection(
                themeMode = themeMode,
                accentColor = accentColor,
                onSetThemeMode = viewModel::setThemeMode,
                onSetAccentColor = viewModel::setAccentColor,
                onShowCustomColorDialog = { showCustomColorDialog = true }
            )

            DisplayBadgesSettingsSection(
                hideReruns = hideReruns,
                highlightAccessibility = highlightAccessibility,
                showCsaRatings = showCsaRatings,
                showChannelNumbers = showChannelNumbers,
                onHideRerunsChange = viewModel::setHideReruns,
                onHighlightAccessibilityChange = viewModel::setHighlightAccessibility,
                onShowCsaRatingsChange = viewModel::setShowCsaRatings,
                onShowChannelNumbersChange = viewModel::setShowChannelNumbers
            )

            ChannelSettingsSection(
                channelSortOrder = channelSortOrder,
                favoriteChannelsCount = favoriteChannels.size,
                hiddenChannelsCount = hiddenChannels.size,
                onSetChannelSortOrder = viewModel::setChannelSortOrder,
                onShowFavoritesDialog = { showFavoritesDialog = true },
                onShowHiddenDialog = { showHiddenDialog = true }
            )

            WidgetSettingsSection(
                widgetTheme = widgetTheme,
                widgetShowProgress = widgetShowProgress,
                onSetWidgetTheme = viewModel::setWidgetTheme,
                onSetWidgetShowProgress = viewModel::setWidgetShowProgress,
                onUpdateWidgets = {
                    viewModel.updateWidgets()
                    Toast.makeText(
                        context,
                        context.getString(R.string.widget_updated_toast),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            LanguageSettingsSection(
                appLanguage = appLanguage,
                autoTranslateEnabled = autoTranslateEnabled,
                translateWifiOnly = translateWifiOnly,
                isEnglishModelDownloaded = isEnglishModelDownloaded,
                isDownloadingEnglishModel = isDownloadingEnglishModel,
                activity = activity,
                onSetLanguage = viewModel::setLanguage,
                onSetAutoTranslate = viewModel::setAutoTranslateEnabled,
                onSetTranslateWifiOnly = viewModel::setTranslateWifiOnly,
                onDownloadEnglishModel = viewModel::downloadEnglishModel,
                onDeleteEnglishModel = viewModel::deleteEnglishModel
            )

            TimezoneSettingsSection(
                displayTimezone = displayTimezone,
                onShowTimezoneDialog = { showTimezoneDialog = true }
            )

            NotificationSettingsSection(
                isNotificationsEnabled = isNotificationsEnabled,
                soundDisplayName = soundDisplayName,
                vibrationEnabled = vibrationEnabled,
                reminderLeadTime = reminderLeadTime,
                reminderRecurrence = reminderRecurrence,
                onToggleNotifications = viewModel::toggleNotifications,
                onPickSound = {
                    val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                        putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
                        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
                        notificationSound?.let {
                            putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(it))
                        }
                    }
                    ringtonePickerLauncher.launch(intent)
                },
                onSetVibrationEnabled = viewModel::setVibrationEnabled,
                onSetLeadTime = viewModel::setLeadTime,
                onSetReminderRecurrence = viewModel::setReminderRecurrence
            )

            SyncSettingsSection(
                syncFrequencyHours = syncFrequencyHours,
                autoSyncEnabled = autoSyncEnabled,
                syncWifiOnly = syncWifiOnly,
                syncChargingOnly = syncChargingOnly,
                retentionDays = retentionDays,
                isSyncing = isSyncing,
                onSetSyncFrequency = viewModel::setSyncFrequency,
                onSetAutoSync = viewModel::setAutoSync,
                onSetSyncWifiOnly = viewModel::setSyncWifiOnly,
                onSetSyncChargingOnly = viewModel::setSyncChargingOnly,
                onSetRetentionDays = viewModel::setRetentionDays,
                onTriggerSync = viewModel::triggerSyncNow
            )

            StorageSettingsSection(
                cacheSizeText = cacheSizeText,
                onClearCache = viewModel::clearCache
            )
        }
    }
}
