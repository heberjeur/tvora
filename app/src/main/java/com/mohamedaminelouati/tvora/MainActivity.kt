package com.mohamedaminelouati.tvora

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.mohamedaminelouati.tvora.data.local.prefs.UserPreferences
import com.mohamedaminelouati.tvora.ui.navigation.TvoraAppNavigation
import com.mohamedaminelouati.tvora.ui.theme.TvoraTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale
import javax.inject.Inject

import android.content.Intent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.compose.runtime.CompositionLocalProvider
import com.mohamedaminelouati.tvora.ui.theme.LocalDisplayDensity
import com.mohamedaminelouati.tvora.ui.theme.LocalHighlightAccessibility
import com.mohamedaminelouati.tvora.ui.theme.LocalFontSizeScale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    private var pendingProgramId by mutableStateOf<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        applySavedLocale()
        enableEdgeToEdge()
        setContent {
            val appLanguage by userPreferences.appLanguage.collectAsStateWithLifecycle(initialValue = "fr")
            val currentLocale = androidx.compose.runtime.remember(appLanguage) {
                Locale.forLanguageTag(appLanguage)
            }
            val localizedConfiguration = androidx.compose.runtime.remember(appLanguage) {
                Locale.setDefault(currentLocale)
                val config = android.content.res.Configuration(resources.configuration)
                config.setLocale(currentLocale)
                @Suppress("DEPRECATION")
                resources.updateConfiguration(config, resources.displayMetrics)
                config
            }

            val themeMode by userPreferences.themeMode.collectAsStateWithLifecycle(initialValue = "dark")
            val fontSizeScale by userPreferences.fontSizeScale.collectAsStateWithLifecycle(initialValue = "normal")
            val displayDensity by userPreferences.displayDensity.collectAsStateWithLifecycle(initialValue = "comfort")
            val highlightAccessibility by userPreferences.highlightAccessibility.collectAsStateWithLifecycle(initialValue = true)
            val showCsaRatings by userPreferences.showCsaRatings.collectAsStateWithLifecycle(initialValue = true)
            val showChannelNumbers by userPreferences.showChannelNumbers.collectAsStateWithLifecycle(initialValue = true)
            val displayTimezone by userPreferences.displayTimezone.collectAsStateWithLifecycle(initialValue = "origin")
            val accentColor by userPreferences.accentColor.collectAsStateWithLifecycle(initialValue = "#E50914")
            val currentDisplayZone = androidx.compose.runtime.remember(displayTimezone) {
                com.mohamedaminelouati.tvora.util.TimezoneUtils.resolveZoneId(displayTimezone)
            }

            TvoraTheme(themeMode = themeMode, fontSizeScale = fontSizeScale, accentColorHex = accentColor) {
                val currentDensity = androidx.compose.ui.platform.LocalDensity.current
                val effectiveDensity = if (displayDensity == "compact") {
                    androidx.compose.ui.unit.Density(
                        density = currentDensity.density * 0.88f,
                        fontScale = currentDensity.fontScale * 0.92f
                    )
                } else {
                    currentDensity
                }

                CompositionLocalProvider(
                    androidx.compose.ui.platform.LocalConfiguration provides localizedConfiguration,
                    androidx.compose.ui.platform.LocalDensity provides effectiveDensity,
                    LocalDisplayDensity provides displayDensity,
                    LocalHighlightAccessibility provides highlightAccessibility,
                    com.mohamedaminelouati.tvora.ui.theme.LocalShowCsaRatings provides showCsaRatings,
                    com.mohamedaminelouati.tvora.ui.theme.LocalShowChannelNumbers provides showChannelNumbers,
                    LocalFontSizeScale provides fontSizeScale,
                    com.mohamedaminelouati.tvora.ui.theme.LocalDisplayZone provides currentDisplayZone
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        TvoraAppNavigation(
                            deepLinkProgramId = pendingProgramId,
                            onDeepLinkConsumed = { pendingProgramId = null }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val id = intent?.getLongExtra("program_id", -1L) ?: -1L
        if (id > 0) {
            pendingProgramId = id
        }
    }

    private fun applySavedLocale() {
        try {
            val lang = runBlocking { userPreferences.appLanguage.first() }
            val locale = Locale.forLanguageTag(lang)
            Locale.setDefault(locale)
            val config = resources.configuration
            config.setLocale(locale)
            @Suppress("DEPRECATION")
            resources.updateConfiguration(config, resources.displayMetrics)
        } catch (e: Exception) {
            Log.w("MainActivity", "Failed to apply saved locale", e)
        }
    }
}
