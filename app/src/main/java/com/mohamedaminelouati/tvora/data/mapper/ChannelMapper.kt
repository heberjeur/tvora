package com.mohamedaminelouati.tvora.data.mapper

import com.mohamedaminelouati.tvora.data.local.ChannelCatalog
import com.mohamedaminelouati.tvora.data.local.entity.ChannelEntity
import com.mohamedaminelouati.tvora.domain.model.Channel

fun ChannelEntity.toDomain(): Channel {
    return Channel(
        id = id,
        displayName = displayName,
        iconUrl = ChannelCatalog.getLogoUrl(id) ?: iconUrl,
        packageId = packageId,
        channelNumber = if (channelNumber > 0) channelNumber else ChannelCatalog.getChannelNumber(id),
        isFavorite = isFavorite
    )
}

fun Channel.toEntity(): ChannelEntity {
    return ChannelEntity(
        id = id,
        displayName = displayName,
        iconUrl = iconUrl,
        packageId = packageId,
        channelNumber = channelNumber,
        isFavorite = isFavorite
    )
}
