package com.mohamedaminelouati.tvora.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgramDescriptionUtilsTest {

    @Test
    fun clean_removesHtmlTagsAndEntities() {
        val raw = "<p>Un thriller captivant &amp; immersif.<br/>Avec des r&eacute;v&eacute;lations choc.</p>"
        val cleaned = ProgramDescriptionUtils.clean(raw)
        assertNotNull(cleaned)
        assertTrue(cleaned!!.contains("Un thriller captivant & immersif."))
        assertTrue(cleaned.contains("Avec des révélations choc."))
        assertFalse(cleaned.contains("<p>"))
        assertFalse(cleaned.contains("&amp;"))
    }

    @Test
    fun clean_stripsBoilerplatePhrases() {
        val raw = "Le bulletin météorologique international. Les prévisions météo pour la journée sont excellentes."
        val cleaned = ProgramDescriptionUtils.clean(raw)
        assertNull(cleaned)
    }

    @Test
    fun clean_stripsBrandMentions() {
        val raw = "Retrouvez la critique sur Télé-Loisirs. Un épisode magistral et bouleversant pour toute la famille."
        val cleaned = ProgramDescriptionUtils.clean(raw)
        assertNotNull(cleaned)
        assertFalse(cleaned!!.lowercase().contains("télé-loisirs"))
        assertTrue(cleaned.contains("Un épisode magistral et bouleversant pour toute la famille."))
    }

    @Test
    fun clean_rejectsTooShortOrNonAlphanumeric() {
        assertNull(ProgramDescriptionUtils.clean("Court"))
        assertNull(ProgramDescriptionUtils.clean("----....----"))
        assertNull(ProgramDescriptionUtils.clean(null))
    }

    @Test
    fun cleanWithTitlePrefix_removesTitlePrefixWithColonOrDash() {
        val title = "Des chiffres et des lettres"
        val descWithColon = "Des chiffres et des lettres : Deux candidats s'affrontent lors des épreuves de lettres et de chiffres."
        val cleanedColon = ProgramDescriptionUtils.cleanWithTitlePrefix(descWithColon, title)
        assertEquals("Deux candidats s'affrontent lors des épreuves de lettres et de chiffres.", cleanedColon)

        val descWithDash = "Des chiffres et des lettres - Deux candidats s'affrontent lors des épreuves de lettres et de chiffres."
        val cleanedDash = ProgramDescriptionUtils.cleanWithTitlePrefix(descWithDash, title)
        assertEquals("Deux candidats s'affrontent lors des épreuves de lettres et de chiffres.", cleanedDash)
    }

    @Test
    fun isRedundant_detectsDuplicateOrContainedSecondaryDescription() {
        val primary = "Un documentaire fascinant sur les fonds marins et la biodiversité océanique."
        val secondary = "Un documentaire fascinant sur les fonds marins"

        assertTrue(ProgramDescriptionUtils.isRedundant(primary, secondary))
        assertTrue(ProgramDescriptionUtils.isRedundant(primary, null))
        assertFalse(ProgramDescriptionUtils.isRedundant(null, secondary))
    }
}
