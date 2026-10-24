package com.mohamedaminelouati.tvora.data.mapper

import com.mohamedaminelouati.tvora.data.local.entity.ProgramEntity
import com.mohamedaminelouati.tvora.domain.model.Credit
import com.mohamedaminelouati.tvora.domain.model.Program

fun ProgramEntity.toDomain(): Program {
    return Program(
        id = id,
        channelId = channelId,
        startTimestamp = startTimestamp,
        stopTimestamp = stopTimestamp,
        title = title,
        originalTitle = title,
        subTitle = subTitle,
        description = description,
        secondaryDescription = secondaryDescription,
        categories = categories,
        iconUrl = iconUrl,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        episodeDisplay = episodeDisplay,
        credit = Credit(
            presenters = presenters,
            actors = actors,
            guests = guests,
            directors = directors,
            producers = producers,
            writers = writers
        ),
        contentRating = contentRating,
        starRating = starRating,
        isUnreleased = isUnreleased,
        hasSubtitles = hasSubtitles,
        isHd = isHd,
        durationMinutes = durationMinutes,
        productionYear = productionYear,
        seriesYear = seriesYear,
        country = country
    )
}

fun Program.toEntity(): ProgramEntity {
    return ProgramEntity(
        id = id,
        channelId = channelId,
        startTimestamp = startTimestamp,
        stopTimestamp = stopTimestamp,
        title = originalTitle,
        subTitle = subTitle,
        description = description,
        secondaryDescription = secondaryDescription,
        categories = categories,
        iconUrl = iconUrl,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        episodeDisplay = episodeDisplay,
        presenters = credit.presenters,
        actors = credit.actors,
        guests = credit.guests,
        directors = credit.directors,
        producers = credit.producers,
        writers = credit.writers,
        contentRating = contentRating,
        starRating = starRating,
        isUnreleased = isUnreleased,
        hasSubtitles = hasSubtitles,
        isHd = isHd,
        durationMinutes = durationMinutes,
        productionYear = productionYear,
        seriesYear = seriesYear,
        country = country
    )
}
