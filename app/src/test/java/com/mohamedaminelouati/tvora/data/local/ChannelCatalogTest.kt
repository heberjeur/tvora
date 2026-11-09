package com.mohamedaminelouati.tvora.data.local

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.Collator
import java.util.Locale

class ChannelCatalogTest {

    @Test
    fun testTntChannelsCountAndOrder() {
        assertEquals(28, ChannelCatalog.tntChannelIds.size)
        assertEquals(28, ChannelCatalog.tntChannelIds.distinct().size)

        for (i in 1..28) {
            val channelId = ChannelCatalog.tntChannelIds[i - 1]
            assertEquals(i, ChannelCatalog.getChannelNumber(channelId))
        }

        assertEquals("TF1.fr", ChannelCatalog.tntChannelIds[0])
        assertEquals("France2.fr", ChannelCatalog.tntChannelIds[1])
        assertEquals("Arte.fr", ChannelCatalog.tntChannelIds[6])
        assertEquals("LCP.fr", ChannelCatalog.tntChannelIds[7])
        assertEquals("TFX.fr", ChannelCatalog.tntChannelIds[10])
        assertEquals("CStar.fr", ChannelCatalog.tntChannelIds[16])
        assertEquals("RMCStory.fr", ChannelCatalog.tntChannelIds[22])
        assertEquals("RMCDecouverte.fr", ChannelCatalog.tntChannelIds[23])
        assertEquals("Cherie25.fr", ChannelCatalog.tntChannelIds[24])
        assertEquals("CanalPlus.fr", ChannelCatalog.tntChannelIds[25])
        assertEquals("TV5Monde.fr", ChannelCatalog.tntChannelIds[26])
        assertEquals("France24.fr", ChannelCatalog.tntChannelIds[27])
    }

    @Test
    fun testNormalizeId() {
        assertEquals("cstar", ChannelCatalog.normalizeId("CStar.fr"))
        assertEquals("cstar", ChannelCatalog.normalizeId("CStar.ch"))
        assertEquals("cstar", ChannelCatalog.normalizeId("cstar"))
        assertEquals("cstar", ChannelCatalog.normalizeId("D17.fr"))

        assertEquals("lcp", ChannelCatalog.normalizeId("LCP.fr"))
        assertEquals("lcp", ChannelCatalog.normalizeId("LaChaineParlementaire.fr"))
        assertEquals("lcp", ChannelCatalog.normalizeId("lcp"))

        assertEquals("cherie25", ChannelCatalog.normalizeId("Cherie25.fr"))
        assertEquals("cherie25", ChannelCatalog.normalizeId("RMCLife.fr"))

        assertEquals("tfx", ChannelCatalog.normalizeId("TFX.fr"))
        assertEquals("tfx", ChannelCatalog.normalizeId("NT1.fr"))

        assertEquals("rmcstory", ChannelCatalog.normalizeId("RMCStory.fr"))
        assertEquals("rmcstory", ChannelCatalog.normalizeId("Numero23.fr"))

        assertEquals("lequipe", ChannelCatalog.normalizeId("LEquipe.fr"))
        assertEquals("lequipe", ChannelCatalog.normalizeId("LEquipe21.fr"))

        assertEquals("canalplus", ChannelCatalog.normalizeId("CanalPlus.fr"))
        assertEquals("canalplus", ChannelCatalog.normalizeId("Canal+"))
        assertEquals("canalplus", ChannelCatalog.normalizeId("canalplus"))

        assertEquals("01tv", ChannelCatalog.normalizeId("01TV.fr"))
        assertEquals("13erue", ChannelCatalog.normalizeId("13eRue.fr"))
        assertEquals("13erue", ChannelCatalog.normalizeId("13emeRue.fr"))
    }

    @Test
    fun testCanonicalChannelId() {
        assertEquals("CStar.fr", ChannelCatalog.canonicalChannelId("CStar.ch"))
        assertEquals("CStar.fr", ChannelCatalog.canonicalChannelId("cstar"))
        assertEquals("CStar.fr", ChannelCatalog.canonicalChannelId("CStar.fr"))

        assertEquals("LCP.fr", ChannelCatalog.canonicalChannelId("LaChaineParlementaire.fr"))
        assertEquals("LCP.fr", ChannelCatalog.canonicalChannelId("LCP.fr"))

        assertEquals("Cherie25.fr", ChannelCatalog.canonicalChannelId("RMCLife.fr"))
        assertEquals("Cherie25.fr", ChannelCatalog.canonicalChannelId("Cherie25.fr"))

        assertEquals("TFX.fr", ChannelCatalog.canonicalChannelId("NT1.fr"))
        assertEquals("RMCStory.fr", ChannelCatalog.canonicalChannelId("Numero23.fr"))
        assertEquals("LEquipe.fr", ChannelCatalog.canonicalChannelId("LEquipe21.fr"))
    }

    @Test
    fun testIsDeadChannel() {
        assertTrue(ChannelCatalog.isDeadChannel("NT1.fr"))
        assertTrue(ChannelCatalog.isDeadChannel("Numero23.fr"))
        assertTrue(ChannelCatalog.isDeadChannel("LEquipe21.fr"))
        assertTrue(ChannelCatalog.isDeadChannel("D17.fr"))
        assertTrue(ChannelCatalog.isDeadChannel("CStar.ch"))
        assertTrue(ChannelCatalog.isDeadChannel("LaChaineParlementaire.fr"))

        assertFalse(ChannelCatalog.isDeadChannel("TF1.fr"))
        assertFalse(ChannelCatalog.isDeadChannel("LCP.fr"))
        assertFalse(ChannelCatalog.isDeadChannel("CStar.fr"))
        assertFalse(ChannelCatalog.isDeadChannel("Cherie25.fr"))
        assertFalse(ChannelCatalog.isDeadChannel("ParisPremiere.fr"))
    }

    @Test
    fun testIsTntChannel() {
        assertTrue(ChannelCatalog.isTntChannel("TF1.fr"))
        assertTrue(ChannelCatalog.isTntChannel("LCP.fr"))
        assertTrue(ChannelCatalog.isTntChannel("LaChaineParlementaire.fr"))
        assertTrue(ChannelCatalog.isTntChannel("CStar.fr"))
        assertTrue(ChannelCatalog.isTntChannel("CStar.ch"))
        assertTrue(ChannelCatalog.isTntChannel("Cherie25.fr"))
        assertTrue(ChannelCatalog.isTntChannel("RMCLife.fr"))
        assertTrue(ChannelCatalog.isTntChannel("CanalPlus.fr"))
        assertTrue(ChannelCatalog.isTntChannel("France24.fr"))

        assertFalse(ChannelCatalog.isTntChannel("ParisPremiere.fr"))
        assertFalse(ChannelCatalog.isTntChannel("RTL9.fr"))
        assertFalse(ChannelCatalog.isTntChannel("13eRue.fr"))
        assertFalse(ChannelCatalog.isTntChannel("01TV.fr"))
    }

    @Test
    fun testPresetChannelsDataIntegrity() {
        val presets = PresetChannelsData.all
        assertTrue(presets.isNotEmpty())

        val ids = presets.map { it.id }
        assertEquals(ids.size, ids.distinct().size)

        val tntNormalized = ChannelCatalog.tntChannelIds.map { ChannelCatalog.normalizeId(it) }.toSet()
        presets.forEach { preset ->
            val norm = ChannelCatalog.normalizeId(preset.id)
            assertFalse(
                "Preset ${preset.id} must not be in TNT",
                tntNormalized.contains(norm)
            )
            assertFalse(
                "Preset ${preset.id} must not be a dead channel",
                ChannelCatalog.isDeadChannel(preset.id)
            )
        }

        for (i in 0 until presets.size - 1) {
            val current = presets[i].displayName
            val next = presets[i + 1].displayName
            val cmp = String.CASE_INSENSITIVE_ORDER.compare(current, next)
            assertTrue("Presets should be sorted alphabetically: '$current' <= '$next'", cmp <= 0)
        }

        val techAndCo = presets.firstOrNull { it.id == "01TV.fr" }
        org.junit.Assert.assertNotNull(techAndCo)
        assertEquals("TECH & CO", techAndCo!!.displayName)
        val techIndex = presets.indexOf(techAndCo)
        assertTrue("TECH & CO should not be near the top: index=$techIndex", techIndex > 200)

        for (i in presets.indices) {
            assertEquals(29 + i, presets[i].channelNumber)
        }
    }

    @Test
    fun testChannelClassification() {
        assertEquals(ChannelRegion.FRANCE, ChannelClassification.getRegion("CStar.fr"))
        assertEquals(ChannelRegion.FRANCE, ChannelClassification.getRegion("CStar.ch"))
        assertEquals(ChannelRegion.FRANCE, ChannelClassification.getRegion("CanalPlus.fr"))
        assertEquals(ChannelRegion.FRANCE, ChannelClassification.getRegion("CanalPlusCinema.fr"))
        assertEquals(ChannelRegion.SWITZERLAND, ChannelClassification.getRegion("CanalPlusSport.ch"))

        assertEquals(ChannelTheme.CINEMA, ChannelClassification.getTheme("CanalPlus.fr", "Canal+"))
        assertEquals(ChannelTheme.CINEMA, ChannelClassification.getTheme("CanalPlusCinema.fr", "Canal+ Cinéma"))
        assertEquals(ChannelTheme.SPORT, ChannelClassification.getTheme("CanalPlusSport.fr", "Canal+ Sport"))
        assertEquals(ChannelTheme.DISCOVERY, ChannelClassification.getTheme("CanalPlusDocs.fr", "Canal+ Docs"))
        assertEquals(ChannelTheme.MUSIC, ChannelClassification.getTheme("CStar.fr", "CStar"))
        assertEquals(ChannelTheme.NEWS, ChannelClassification.getTheme("01TV.fr", "TECH & CO"))
    }

    @Test
    fun testDeduplicationAndTntPriority() {
        val tntNormalized = ChannelCatalog.tntChannelIds.map { ChannelCatalog.normalizeId(it) }.toSet()
        val customCandidates = listOf(
            Triple("CStar.ch", "CStar", 173),
            Triple("LaChaineParlementaire.fr", "LCP", 427),
            Triple("Cherie25.fr", "Chérie 25", 183),
            Triple("NT1.fr", "NT1", 11),
            Triple("Numero23.fr", "Numéro 23", 23),
            Triple("ParisPremiere.fr", "Paris Première", 29),
            Triple("01TV.fr", "TECH & CO", 570)
        )

        data class SimpleChannel(val id: String, val displayName: String, val channelNumber: Int)

        val result = mutableListOf<SimpleChannel>()
        val seenNormalized = mutableSetOf<String>()

        ChannelCatalog.tntChannelIds.forEach { tntId ->
            val norm = ChannelCatalog.normalizeId(tntId)
            if (seenNormalized.add(norm)) {
                val canonical = ChannelCatalog.canonicalChannelId(tntId)
                result.add(
                    SimpleChannel(
                        id = canonical,
                        displayName = ChannelCatalog.getDisplayName(canonical),
                        channelNumber = ChannelCatalog.getChannelNumber(canonical)
                    )
                )
            }
        }

        customCandidates.forEach { (id, name, num) ->
            val norm = ChannelCatalog.normalizeId(id)
            if (!tntNormalized.contains(norm) && !ChannelCatalog.isDeadChannel(id) && seenNormalized.add(norm)) {
                val canonical = ChannelCatalog.canonicalChannelId(id)
                val resolvedNumber = ChannelCatalog.getChannelNumber(canonical).takeIf { it > 28 }
                    ?: num.takeIf { it > 28 }
                    ?: (result.size + 1)
                result.add(
                    SimpleChannel(
                        id = canonical,
                        displayName = ChannelCatalog.getDisplayName(canonical).ifEmpty { name },
                        channelNumber = resolvedNumber
                    )
                )
            }
        }

        result.sortWith(
            compareBy<SimpleChannel> { if (it.channelNumber > 0) 0 else 1 }
                .thenBy { it.channelNumber }
                .thenBy { it.displayName }
        )

        assertEquals(30, result.size)

        for (i in 1..28) {
            assertEquals(i, result[i - 1].channelNumber)
        }

        val lcp = result.first { it.id == "LCP.fr" }
        assertEquals(8, lcp.channelNumber)

        val cstar = result.first { it.id == "CStar.fr" }
        assertEquals(17, cstar.channelNumber)

        val cherie25 = result.first { it.id == "Cherie25.fr" }
        assertEquals(25, cherie25.channelNumber)

        assertFalse(result.any { it.id == "CStar.ch" || it.channelNumber == 173 })
        assertFalse(result.any { it.id == "LaChaineParlementaire.fr" || it.channelNumber == 427 })
        assertFalse(result.any { it.id == "NT1.fr" })
        assertFalse(result.any { it.id == "Numero23.fr" })

        val uniqueNormalized = result.map { ChannelCatalog.normalizeId(it.id) }.distinct()
        assertEquals(result.size, uniqueNormalized.size)

        val parisPremiere = result.first { it.id == "ParisPremiere.fr" }
        assertTrue(parisPremiere.channelNumber >= 29)

        val techAndCo = result.first { it.id == "01TV.fr" }
        assertTrue(techAndCo.channelNumber >= 29)
    }
}

