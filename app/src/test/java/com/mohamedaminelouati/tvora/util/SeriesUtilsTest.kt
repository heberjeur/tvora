package com.mohamedaminelouati.tvora.util

import com.mohamedaminelouati.tvora.domain.model.Program
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SeriesUtilsTest {

    @Test
    fun cleanSubtitle_stripsSeasonAndEpisodePrefixes() {
        val cleaned = SeriesUtils.cleanSubtitle("Saison 2 Épisode 4 : Le Retour", "Une Série")
        assertEquals("Le Retour", cleaned)
    }

    @Test
    fun cleanSubtitle_stripsCompactSeasonEpisodePrefixes() {
        val cleaned = SeriesUtils.cleanSubtitle("S02E04 - Le Jugement", "Une Série")
        assertEquals("Le Jugement", cleaned)
    }

    @Test
    fun cleanSubtitle_returnsNullWhenMatchingSeriesTitle() {
        val cleaned = SeriesUtils.cleanSubtitle("Capitaine Marleau", "Capitaine Marleau")
        assertNull(cleaned)
    }

    @Test
    fun cleanSubtitle_returnsNullWhenOnlySeasonOrEpisode() {
        val cleaned = SeriesUtils.cleanSubtitle("Saison 3 Épisode 12", "Une Série")
        assertNull(cleaned)
    }

    @Test
    fun cleanSubtitle_returnsNullForNullOrBlankInput() {
        assertNull(SeriesUtils.cleanSubtitle(null))
        assertNull(SeriesUtils.cleanSubtitle("   "))
    }

    @Test
    fun normalizeEpisodes_extractsEpisodeNumberFromSubtitle() {
        val program = Program(
            id = 1L,
            channelId = "tf1.fr",
            title = "HPI",
            subTitle = "Saison 3 Épisode 5 : Froid de canard",
            description = "",
            secondaryDescription = null,
            categories = listOf("Série"),
            contentRating = null,
            startTimestamp = 1700000000000L,
            stopTimestamp = 1700003600000L,
            seasonNumber = null,
            episodeNumber = null
        )

        val normalized = SeriesUtils.normalizeEpisodes(listOf(program))
        assertEquals(1, normalized.size)
        assertEquals(3, normalized[0].seasonNumber)
        assertEquals(5, normalized[0].episodeNumber)
    }
}
