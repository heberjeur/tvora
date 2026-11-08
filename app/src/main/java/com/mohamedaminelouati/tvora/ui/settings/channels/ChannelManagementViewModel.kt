package com.mohamedaminelouati.tvora.ui.settings.channels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamedaminelouati.tvora.data.local.ChannelCatalog
import com.mohamedaminelouati.tvora.data.local.ChannelCatalog.PresetChannel
import com.mohamedaminelouati.tvora.data.local.ChannelClassification
import com.mohamedaminelouati.tvora.data.local.ChannelRegion
import com.mohamedaminelouati.tvora.data.local.ChannelTheme
import com.mohamedaminelouati.tvora.data.local.PresetChannelsData
import com.mohamedaminelouati.tvora.domain.model.Channel
import com.mohamedaminelouati.tvora.domain.model.ChannelPackage
import com.mohamedaminelouati.tvora.domain.repository.EpgRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelManagementViewModel @Inject constructor(
    private val epgRepository: EpgRepository
) : ViewModel() {

    val allPresets: List<PresetChannel> = PresetChannelsData.all

    val defaultTntChannels: List<PresetChannel> = ChannelCatalog.tntChannelIds.take(28).map { id ->
        PresetChannel(
            id = ChannelCatalog.canonicalChannelId(id),
            displayName = ChannelCatalog.getDisplayName(id),
            channelNumber = ChannelCatalog.getChannelNumber(id),
            iconUrl = ChannelCatalog.getLogoUrl(id) ?: ""
        )
    }

    val customChannels: StateFlow<List<Channel>> = epgRepository.getCustomChannels()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedRegion = MutableStateFlow(ChannelRegion.ALL)
    val selectedRegion: StateFlow<ChannelRegion> = _selectedRegion.asStateFlow()

    private val _selectedTheme = MutableStateFlow(ChannelTheme.ALL)
    val selectedTheme: StateFlow<ChannelTheme> = _selectedTheme.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    val filteredCatalogChannels: StateFlow<List<PresetChannel>> = combine(
        searchQuery,
        selectedRegion,
        selectedTheme
    ) { query, region, theme ->
        val trimmed = query.trim()
        allPresets.filter { preset ->
            val matchesRegion = region == ChannelRegion.ALL || ChannelClassification.getRegion(preset.id) == region
            val matchesTheme = theme == ChannelTheme.ALL || ChannelClassification.getTheme(preset.id, preset.displayName) == theme
            val matchesQuery = trimmed.isEmpty() ||
                preset.displayName.contains(trimmed, ignoreCase = true) ||
                preset.id.contains(trimmed, ignoreCase = true) ||
                preset.channelNumber.toString() == trimmed

            matchesRegion && matchesTheme && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), allPresets)

    val filteredCustomChannels: StateFlow<List<PresetChannel>> = combine(
        customChannels,
        searchQuery,
        selectedRegion,
        selectedTheme
    ) { customs, query, region, theme ->
        val trimmed = query.trim()
        val customNormalized = customs.map { ChannelCatalog.normalizeId(it.id) }.toSet()
        allPresets.filter { preset ->
            if (!customNormalized.contains(ChannelCatalog.normalizeId(preset.id))) return@filter false
            val matchesRegion = region == ChannelRegion.ALL || ChannelClassification.getRegion(preset.id) == region
            val matchesTheme = theme == ChannelTheme.ALL || ChannelClassification.getTheme(preset.id, preset.displayName) == theme
            val matchesQuery = trimmed.isEmpty() ||
                preset.displayName.contains(trimmed, ignoreCase = true) ||
                preset.id.contains(trimmed, ignoreCase = true) ||
                preset.channelNumber.toString() == trimmed

            matchesRegion && matchesTheme && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredTntChannels: StateFlow<List<PresetChannel>> = searchQuery.map { query ->
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            defaultTntChannels
        } else {
            defaultTntChannels.filter { channel ->
                channel.displayName.contains(trimmed, ignoreCase = true) ||
                    channel.id.contains(trimmed, ignoreCase = true) ||
                    channel.channelNumber.toString() == trimmed
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), defaultTntChannels)

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedRegion(region: ChannelRegion) {
        _selectedRegion.value = region
    }

    fun setSelectedTheme(theme: ChannelTheme) {
        _selectedTheme.value = theme
    }

    fun setSelectedTab(index: Int) {
        _selectedTab.value = index
    }

    fun toggleChannel(preset: PresetChannel) {
        viewModelScope.launch {
            val currentCustoms = customChannels.value
            val isAlreadyAdded = currentCustoms.any {
                ChannelCatalog.normalizeId(it.id) == ChannelCatalog.normalizeId(preset.id)
            }
            if (isAlreadyAdded) {
                epgRepository.removeCustomChannel(preset.id)
            } else {
                val channel = Channel(
                    id = preset.id,
                    displayName = preset.displayName,
                    iconUrl = preset.iconUrl,
                    packageId = ChannelPackage.COMMUNITY_ENRICHED.id,
                    channelNumber = preset.channelNumber,
                    isFavorite = false
                )
                epgRepository.addCustomChannel(channel)
            }
        }
    }

    fun clearAllCustomChannels() {
        viewModelScope.launch {
            epgRepository.clearCustomChannels()
        }
    }
}
