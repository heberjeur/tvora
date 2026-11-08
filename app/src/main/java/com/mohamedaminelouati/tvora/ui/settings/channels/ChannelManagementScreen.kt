package com.mohamedaminelouati.tvora.ui.settings.channels

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.mohamedaminelouati.tvora.R
import com.mohamedaminelouati.tvora.data.local.ChannelCatalog
import com.mohamedaminelouati.tvora.data.local.ChannelCatalog.PresetChannel
import com.mohamedaminelouati.tvora.data.local.ChannelClassification
import com.mohamedaminelouati.tvora.data.local.ChannelRegion
import com.mohamedaminelouati.tvora.data.local.ChannelTheme
import com.mohamedaminelouati.tvora.ui.theme.AccentRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelManagementScreen(
    onBackClick: () -> Unit,
    viewModel: ChannelManagementViewModel = hiltViewModel()
) {
    val customChannels by viewModel.customChannels.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedRegion by viewModel.selectedRegion.collectAsStateWithLifecycle()
    val selectedTheme by viewModel.selectedTheme.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val filteredCatalog by viewModel.filteredCatalogChannels.collectAsStateWithLifecycle()
    val filteredCustoms by viewModel.filteredCustomChannels.collectAsStateWithLifecycle()

    var showClearAllDialog by remember { mutableStateOf(false) }

    val customNormalized = remember(customChannels) {
        customChannels.map { ChannelCatalog.normalizeId(it.id) }.toSet()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.channel_management_title),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    if (selectedTab == 1 && customChannels.isNotEmpty()) {
                        IconButton(onClick = { showClearAllDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = stringResource(R.string.action_clear_all),
                                tint = AccentRed
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = AccentRed,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = AccentRed
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { viewModel.setSelectedTab(0) },
                    text = {
                        Text(
                            text = stringResource(R.string.tab_catalog, viewModel.allPresets.size),
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { viewModel.setSelectedTab(1) },
                    text = {
                        Text(
                            text = stringResource(R.string.tab_my_channels, customChannels.size),
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                )
            }

            SearchAndFilterHeader(
                searchQuery = searchQuery,
                onSearchChange = viewModel::setSearchQuery,
                selectedRegion = selectedRegion,
                onRegionChange = viewModel::setSelectedRegion,
                selectedTheme = selectedTheme,
                onThemeChange = viewModel::setSelectedTheme,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (selectedTab == 0) {
                if (filteredCatalog.isEmpty()) {
                    EmptyStateView(
                        message = stringResource(R.string.empty_search_channels),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredCatalog, key = { it.id }) { preset ->
                            val isAdded = customNormalized.contains(ChannelCatalog.normalizeId(preset.id))
                            ChannelItemCard(
                                preset = preset,
                                isAdded = isAdded,
                                onToggle = { viewModel.toggleChannel(preset) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            } else {
                if (filteredCustoms.isEmpty()) {
                    EmptyStateView(
                        message = stringResource(
                            if (searchQuery.isBlank() && selectedRegion == ChannelRegion.ALL && selectedTheme == ChannelTheme.ALL) {
                                R.string.empty_my_channels_title
                            } else {
                                R.string.empty_search_channels
                            }
                        ),
                        subMessage = if (searchQuery.isBlank() && selectedRegion == ChannelRegion.ALL && selectedTheme == ChannelTheme.ALL) {
                            stringResource(R.string.empty_my_channels_desc)
                        } else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredCustoms, key = { it.id }) { preset ->
                            ChannelItemCard(
                                preset = preset,
                                isAdded = true,
                                onToggle = { viewModel.toggleChannel(preset) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }

    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.confirm_clear_all_title),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(text = stringResource(R.string.confirm_clear_all_desc))
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllCustomChannels()
                        showClearAllDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text(text = stringResource(R.string.action_clear_all))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllDialog = false }) {
                    Text(text = stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun SearchAndFilterHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedRegion: ChannelRegion,
    onRegionChange: (ChannelRegion) -> Unit,
    selectedTheme: ChannelTheme,
    onThemeChange: (ChannelTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(R.string.search_catalog_channels_hint),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentRed,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ChannelRegion.values().forEach { region ->
                val isSelected = selectedRegion == region
                FilterChip(
                    selected = isSelected,
                    onClick = { onRegionChange(region) },
                    label = {
                        Text(
                            text = "${region.flagEmoji} ${stringResource(region.labelRes)}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentRed.copy(alpha = 0.15f),
                        selectedLabelColor = AccentRed
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ChannelTheme.values().forEach { theme ->
                val isSelected = selectedTheme == theme
                FilterChip(
                    selected = isSelected,
                    onClick = { onThemeChange(theme) },
                    label = {
                        Text(
                            text = "${theme.iconEmoji} ${stringResource(theme.labelRes)}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentRed.copy(alpha = 0.15f),
                        selectedLabelColor = AccentRed
                    )
                )
            }
        }
    }
}

@Composable
private fun ChannelItemCard(
    preset: PresetChannel,
    isAdded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val region = remember(preset.id) { ChannelClassification.getRegion(preset.id) }
    val theme = remember(preset.id, preset.displayName) {
        ChannelClassification.getTheme(preset.id, preset.displayName)
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (preset.iconUrl.isNotBlank()) {
                AsyncImage(
                    model = preset.iconUrl,
                    contentDescription = preset.displayName,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White)
                        .padding(2.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = preset.displayName.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = preset.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.channel_number_format, preset.channelNumber),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(text = "•", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    Text(text = region.flagEmoji, style = MaterialTheme.typography.bodySmall)
                    Text(text = theme.iconEmoji, style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isAdded) {
                OutlinedButton(
                    onClick = onToggle,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2E7D32)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.action_added),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Button(
                    onClick = onToggle,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.action_add_channel),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateView(
    message: String,
    subMessage: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Tv,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subMessage != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
