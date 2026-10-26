package com.mohamedaminelouati.tvora.data.mapper

import com.mohamedaminelouati.tvora.data.local.entity.KeywordAlertEntity
import com.mohamedaminelouati.tvora.domain.model.KeywordAlert

fun KeywordAlertEntity.toDomain(): KeywordAlert {
    return KeywordAlert(
        id = id,
        keyword = keyword,
        isEnabled = isEnabled,
        leadTimeMinutes = leadTimeMinutes,
        createdAt = createdAt
    )
}

fun KeywordAlert.toEntity(): KeywordAlertEntity {
    return KeywordAlertEntity(
        id = id,
        keyword = keyword,
        isEnabled = isEnabled,
        leadTimeMinutes = leadTimeMinutes,
        createdAt = createdAt
    )
}
