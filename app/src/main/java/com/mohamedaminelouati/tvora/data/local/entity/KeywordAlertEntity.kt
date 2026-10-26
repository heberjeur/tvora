package com.mohamedaminelouati.tvora.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "keyword_alerts",
    indices = [
        Index(value = ["keyword"], unique = true)
    ]
)
data class KeywordAlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val keyword: String,
    val isEnabled: Boolean = true,
    val leadTimeMinutes: Int = 10,
    @ColumnInfo(defaultValue = "NULL")
    val soundUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
