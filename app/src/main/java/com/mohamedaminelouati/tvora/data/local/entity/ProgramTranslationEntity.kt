package com.mohamedaminelouati.tvora.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "program_translations",
    primaryKeys = ["programId", "targetLanguage"],
    indices = [
        Index(value = ["programId"]),
        Index(value = ["targetLanguage"])
    ]
)
data class ProgramTranslationEntity(
    val programId: Long,
    val targetLanguage: String,
    val translatedTitle: String,
    val translatedDescription: String
)
