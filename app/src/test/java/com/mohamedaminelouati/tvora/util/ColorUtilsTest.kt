package com.mohamedaminelouati.tvora.util

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class ColorUtilsTest {

    @Test
    fun parseHexColor_parsesSixCharacterHex() {
        val color = ColorUtils.parseHexColor("#1E88E5")
        assertEquals(Color(0xFF1E88E5), color)

        val colorWithoutHash = ColorUtils.parseHexColor("1E88E5")
        assertEquals(Color(0xFF1E88E5), colorWithoutHash)
    }

    @Test
    fun parseHexColor_parsesEightCharacterHex() {
        val color = ColorUtils.parseHexColor("#801E88E5")
        assertEquals(Color(0x801E88E5), color)
    }

    @Test
    fun parseHexColor_returnsFallbackOnInvalidOrBlank() {
        val fallback = Color(0xFF00FF00)
        assertEquals(fallback, ColorUtils.parseHexColor(null, fallback))
        assertEquals(fallback, ColorUtils.parseHexColor("", fallback))
        assertEquals(fallback, ColorUtils.parseHexColor("XYZ", fallback))
        assertEquals(fallback, ColorUtils.parseHexColor("#12", fallback))
    }

    @Test
    fun toHex_formatsColorToUppercaseHex() {
        val hex = ColorUtils.toHex(Color(0xFFE50914))
        assertEquals("#E50914", hex)
    }

    @Test
    fun computeVariant_darkensColorComponents() {
        val original = Color(1.0f, 1.0f, 1.0f, 1.0f)
        val variant = ColorUtils.computeVariant(original)
        assertEquals(0.78f, variant.red, 0.01f)
        assertEquals(0.78f, variant.green, 0.01f)
        assertEquals(0.78f, variant.blue, 0.01f)
    }
}
