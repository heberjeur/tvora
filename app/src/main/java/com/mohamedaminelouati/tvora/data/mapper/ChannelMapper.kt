package com.mohamedaminelouati.tvora.data.mapper

import com.mohamedaminelouati.tvora.data.local.ChannelCatalog
import com.mohamedaminelouati.tvora.data.local.entity.ChannelEntity
import com.mohamedaminelouati.tvora.domain.model.Channel

fun ChannelEntity.toDomain(): Channel {
    val catalogNumber = ChannelCatalog.getChannelNumber(id)
    return Channel(
        id = id,
        displayName = if (ChannelCatalog.isCatalogChannel(id)) ChannelCatalog.getDisplayName(id) else displayName,
        iconUrl = ChannelCatalog.getLogoUrl(id) ?: iconUrl,
        packageId = packageId,
        channelNumber = if (catalogNumber > 0) catalogNumber else channelNumber,
        isFavorite = isFavorite
    )
}

fun Channel.toEntity(): ChannelEntity {
    val catalogNumber = ChannelCatalog.getChannelNumber(id)
    return ChannelEntity(
        id = id,
        displayName = displayName,
        iconUrl = iconUrl,
        packageId = packageId,
        channelNumber = if (catalogNumber > 0) catalogNumber else channelNumber,
        isFavorite = isFavorite
    )
}
