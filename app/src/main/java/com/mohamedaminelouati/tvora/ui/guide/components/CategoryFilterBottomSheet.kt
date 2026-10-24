package com.mohamedaminelouati.tvora.ui.guide.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mohamedaminelouati.tvora.R
import com.mohamedaminelouati.tvora.domain.model.TvCategory
import com.mohamedaminelouati.tvora.ui.theme.AccentRed
import com.mohamedaminelouati.tvora.util.CategoryUtils
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CategoryFilterBottomSheet(
    selectedCategory: TvCategory?,
    selectedSubCategory: String?,
    availableSubCategories: List<String>,
    currentLocale: Locale,
    onCategorySelect: (TvCategory?) -> Unit,
    onSubCategorySelect: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    var categoryQuery by remember { mutableStateOf("") }

    val filteredCategories = remember(categoryQuery) {
        if (categoryQuery.isBlank()) {
            TvCategory.entries
        } else {
            TvCategory.entries.filter { cat ->
                context.getString(cat.nameRes).contains(categoryQuery, ignoreCase = true)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.filter_categories_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (selectedCategory != null) {
                    TextButton(
                        onClick = {
                            onCategorySelect(null)
                            onSubCategorySelect(null)
                            scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.action_reset_filter),
                            color = AccentRed,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            OutlinedTextField(
                value = categoryQuery,
                onValueChange = { categoryQuery = it },
                placeholder = {
                    Text(
                        text = stringResource(R.string.categories_search_placeholder),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (categoryQuery.isNotEmpty()) {
                        IconButton(onClick = { categoryQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentRed,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (categoryQuery.isBlank()) {
                    item(span = { GridItemSpan(2) }) {
                        CategoryItemCard(
                            category = null,
                            isSelected = selectedCategory == null,
                            onClick = {
                                onCategorySelect(null)
                                onSubCategorySelect(null)
                                scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                            }
                        )
                    }
                }
                items(filteredCategories) { category ->
                    CategoryItemCard(
                        category = category,
                        isSelected = selectedCategory == category,
                        onClick = {
                            if (selectedCategory == category) {
                                scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                            } else {
                                onCategorySelect(category)
                                onSubCategorySelect(null)
                            }
                        }
                    )
                }
            }

            if (selectedCategory != null && availableSubCategories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.filter_subcategories_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AccentRed,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val isAllSubSelected = selectedSubCategory == null
                    Surface(
                        onClick = {
                            onSubCategorySelect(null)
                            scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isAllSubSelected) AccentRed else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(
                            1.dp,
                            if (isAllSubSelected) AccentRed else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.filter_all_channels_short),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isAllSubSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isAllSubSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }

                    availableSubCategories.forEach { subCat ->
                        val isSubSelected = selectedSubCategory == subCat
                        Surface(
                            onClick = {
                                onSubCategorySelect(subCat)
                                scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSubSelected) AccentRed else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (isSubSelected) AccentRed else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                        ) {
                            Text(
                                text = CategoryUtils.formatCategory(subCat, currentLocale),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSubSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSubSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItemCard(
    category: TvCategory?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) AccentRed else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    val containerColor = if (isSelected) AccentRed.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) AccentRed else MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(category),
                    contentDescription = null,
                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = if (category == null) {
                    stringResource(R.string.category_all)
                } else {
                    stringResource(category.nameRes)
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) AccentRed else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = AccentRed,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

fun getCategoryIcon(category: TvCategory?): ImageVector {
    return when (category) {
        null -> Icons.Default.GridView
        TvCategory.MOVIES -> Icons.Default.Movie
        TvCategory.SERIES -> Icons.Default.Tv
        TvCategory.ENTERTAINMENT -> Icons.Default.TheaterComedy
        TvCategory.SPORT -> Icons.Default.SportsSoccer
        TvCategory.NEWS -> Icons.Default.Newspaper
        TvCategory.DOCUMENTARY -> Icons.Default.Public
        TvCategory.KIDS -> Icons.Default.ChildCare
        TvCategory.MUSIC -> Icons.Default.MusicNote
        TvCategory.OTHER -> Icons.Default.Category
    }
}
