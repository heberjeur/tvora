package com.mohamedaminelouati.tvora.domain.model

data class KeywordAlert(
    val id: Long = 0,
    val keyword: String,
    val isEnabled: Boolean = true,
    val leadTimeMinutes: Int = 10,
    val createdAt: Long = System.currentTimeMillis()
)
