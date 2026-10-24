package com.mohamedaminelouati.tvora.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class CategoryUtilsTest {

    @Test
    fun formatCategory_preservesFrenchWhenLocaleIsFrench() {
        val result = CategoryUtils.formatCategory("Cinéma", Locale.FRENCH)
        assertEquals("Cinéma", result)

        val seriesResult = CategoryUtils.formatCategory("Série TV", Locale.FRANCE)
        assertEquals("Série TV", seriesResult)
    }

    @Test
    fun formatCategory_translatesToEnglishWhenLocaleIsEnglish() {
        assertEquals("Cinema", CategoryUtils.formatCategory("Cinéma", Locale.ENGLISH))
        assertEquals("TV Movie", CategoryUtils.formatCategory("Téléfilm", Locale.US))
        assertEquals("Series", CategoryUtils.formatCategory("Série", Locale.ENGLISH))
        assertEquals("Documentary", CategoryUtils.formatCategory("Documentaire", Locale.ENGLISH))
        assertEquals("Entertainment", CategoryUtils.formatCategory("Divertissement", Locale.ENGLISH))
    }

    @Test
    fun formatCategory_translatesCompoundCategories() {
        val result = CategoryUtils.formatCategory("Film : Comédie", Locale.ENGLISH)
        assertEquals("Movie: Comedy", result)

        val seriesResult = CategoryUtils.formatCategory("Série : Policier", Locale.ENGLISH)
        assertEquals("Series: Crime / Detective", seriesResult)
    }

    @Test
    fun formatCategory_handlesBlankOrUnknownCategories() {
        assertEquals("", CategoryUtils.formatCategory(null, Locale.ENGLISH))
        assertEquals("", CategoryUtils.formatCategory("   ", Locale.ENGLISH))
        assertEquals("GenreInconnu", CategoryUtils.formatCategory("GenreInconnu", Locale.ENGLISH))
    }

    @Test
    fun formatGenreList_formatsAndDeduplicatesGenres() {
        val genres = listOf("Film", "Comédie", "Drame")
        val englishGenres = CategoryUtils.formatGenreList(genres, Locale.ENGLISH)
        assertEquals("Movie, Comedy, Drama", englishGenres)

        val frenchGenres = CategoryUtils.formatGenreList(genres, Locale.FRENCH)
        assertEquals("Film, Comédie, Drame", frenchGenres)
    }
}
