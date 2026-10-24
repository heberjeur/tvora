package com.mohamedaminelouati.tvora.ui.categories.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import com.mohamedaminelouati.tvora.domain.model.TvCategory

fun getCategoriesCategoryIcon(category: TvCategory?): ImageVector {
    return when (category) {
        null -> Icons.Default.GridView
        TvCategory.MOVIES -> Icons.Default.Movie
        TvCategory.SERIES -> Icons.Default.Tv
        TvCategory.ENTERTAINMENT -> Icons.Default.Celebration
        TvCategory.SPORT -> Icons.Default.SportsSoccer
        TvCategory.NEWS -> Icons.Default.Newspaper
        TvCategory.DOCUMENTARY -> Icons.Default.Public
        TvCategory.KIDS -> Icons.Default.ChildCare
        TvCategory.MUSIC -> Icons.Default.MusicNote
        TvCategory.OTHER -> Icons.Default.MoreHoriz
    }
}
