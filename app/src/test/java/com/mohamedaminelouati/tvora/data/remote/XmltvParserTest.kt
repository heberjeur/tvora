package com.mohamedaminelouati.tvora.data.remote

import com.mohamedaminelouati.tvora.data.local.entity.ChannelEntity
import com.mohamedaminelouati.tvora.data.local.entity.ProgramEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream

class XmltvParserTest {

    @Test
    fun parse_extractsChannelsAndProgramsAccurately() = runBlocking {
        val xmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <tv source-info-name="TVora TNT+">
              <channel id="tf1.fr">
                <display-name>TF1</display-name>
                <icon src="https://example.com/tf1.png" />
              </channel>
              <programme start="20261025200000 +0100" stop="20261025210000 +0100" channel="tf1.fr">
                <title lang="fr">Le 20H</title>
                <sub-title lang="fr">Édition spéciale</sub-title>
                <desc lang="fr">Le grand journal de la rédaction avec les correspondants en direct.</desc>
                <category lang="fr">Info</category>
                <category lang="fr">Journal</category>
                <date>2026</date>
                <rating system="CSA">
                  <value>Tout public</value>
                </rating>
                <credits>
                  <presenter>Gilles Bouleau</presenter>
                </credits>
                <video>
                  <quality>HD</quality>
                </video>
              </programme>
            </tv>
        """.trimIndent()

        val parser = XmltvParser()
        val parsedChannels = mutableListOf<ChannelEntity>()
        val parsedPrograms = mutableListOf<ProgramEntity>()

        val inputStream = ByteArrayInputStream(xmlContent.toByteArray(Charsets.UTF_8))
        parser.parse(inputStream, packageId = "tnt") { channels, programs ->
            parsedChannels.addAll(channels)
            parsedPrograms.addAll(programs)
        }

        assertEquals(1, parsedChannels.size)
        assertEquals("tf1.fr", parsedChannels[0].id)
        assertEquals("TF1", parsedChannels[0].displayName)

        assertEquals(1, parsedPrograms.size)
        val program = parsedPrograms[0]
        assertEquals("tf1.fr", program.channelId)
        assertEquals("Le 20H", program.title)
        assertEquals("Édition spéciale", program.subTitle)
        assertNotNull(program.description)
        assertTrue(program.categories.contains("Info"))
        assertTrue(program.isHd)
        assertEquals("Tout public", program.contentRating)
        assertTrue(program.presenters.contains("Gilles Bouleau"))
    }

    @Test
    fun parse_handlesBadgesAndEpisodeNumbers() = runBlocking {
        val xmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <tv source-info-name="TVora TNT+">
              <channel id="france2.fr">
                <display-name>France 2</display-name>
              </channel>
              <programme start="20261025211000 +0100" stop="20261025220000 +0100" channel="france2.fr">
                <title lang="fr">Astrid et Raphaëlle</title>
                <sub-title lang="fr">Planète interdite</sub-title>
                <desc lang="fr">Une nouvelle enquête policière palpitante au cœur de Paris.</desc>
                <episode-num system="xmltv_ns">3.4.</episode-num>
                <premiere />
                <subtitles type="teletext" />
                <audio-description />
              </programme>
            </tv>
        """.trimIndent()

        val parser = XmltvParser()
        val parsedPrograms = mutableListOf<ProgramEntity>()

        val inputStream = ByteArrayInputStream(xmlContent.toByteArray(Charsets.UTF_8))
        parser.parse(inputStream, packageId = "tnt") { _, programs ->
            parsedPrograms.addAll(programs)
        }

        assertEquals(1, parsedPrograms.size)
        val program = parsedPrograms[0]
        assertEquals("Astrid et Raphaëlle", program.title)
        assertEquals(4, program.seasonNumber)
        assertEquals(5, program.episodeNumber)
        assertTrue(program.isUnreleased)
        assertTrue(program.hasSubtitles)
    }
}
