package com.mohamedaminelouati.tvora.data.local

import com.mohamedaminelouati.tvora.R

enum class ChannelRegion(val code: String, val flagEmoji: String, val labelRes: Int) {
    ALL("all", "🌐", R.string.filter_country_all),
    FRANCE("fr", "🇫🇷", R.string.region_france),
    BELGIUM("be", "🇧🇪", R.string.region_belgium),
    SWITZERLAND("ch", "🇨🇭", R.string.region_switzerland),
    CANADA("ca", "🇨🇦", R.string.region_canada),
    MONACO("mc", "🇲🇨", R.string.region_monaco),
    AFRICA("af", "🌍", R.string.region_africa),
    INTERNATIONAL("intl", "🌐", R.string.region_intl)
}

enum class ChannelTheme(val code: String, val iconEmoji: String, val labelRes: Int) {
    ALL("all", "📺", R.string.filter_genre_all),
    GENERAL("general", "📺", R.string.theme_general),
    SPORT("sport", "⚽", R.string.theme_sport),
    CINEMA("cinema", "🎬", R.string.theme_cinema),
    NEWS("news", "📰", R.string.theme_news),
    KIDS("kids", "🧒", R.string.theme_kids),
    DISCOVERY("discovery", "🌿", R.string.theme_discovery),
    MUSIC("music", "🎵", R.string.theme_music)
}

data class ChannelPack(
    val id: String,
    val name: String,
    val description: String,
    val iconEmoji: String,
    val channelIds: List<String>
)

object ChannelClassification {

    fun getRegion(channelId: String): ChannelRegion {
        val lower = channelId.lowercase()
        if (lower.contains("tvmonaco")) return ChannelRegion.MONACO
        if (lower.endsWith(".fr") || lower.endsWith(".frdrom")) return ChannelRegion.FRANCE
        if (lower.endsWith(".be")) return ChannelRegion.BELGIUM
        if (lower.endsWith(".ch")) return ChannelRegion.SWITZERLAND
        if (lower.endsWith(".ca")) return ChannelRegion.CANADA
        if (lower.endsWith(".af") || lower.endsWith(".ci") || lower.endsWith(".sn") || lower.endsWith(".ne")) return ChannelRegion.AFRICA
        return ChannelRegion.INTERNATIONAL
    }

    fun getTheme(channelId: String, channelName: String): ChannelTheme {
        val text = "$channelId $channelName".lowercase()
        if (listOf("sport", "foot", "golf", "auto", "moteur", "bola", "equidia", "fight", "racing", "tennis", "rugby", "eurosport", "bein", "lequipe").any { text.contains(it) }) {
            return ChannelTheme.SPORT
        }
        if (listOf("cinema", "cine", "series", "film", "action", "tcm", "paramount", "warner", "polar", "comedie", "novelas", "thriller", "serie").any { text.contains(it) }) {
            return ChannelTheme.CINEMA
        }
        if (listOf("info", "news", "euronews", "bfm", "cnews", "lci", "lcp", "senat", "france24", "01tv", "business", "meteo", "parlement").any { text.contains(it) }) {
            return ChannelTheme.NEWS
        }
        if (listOf("disney", "gulli", "mangas", "cartoon", "nickelodeon", "toons", "boing", "boomerang", "kids", "tiptv", "canalj", "tiji", "jeunesse", "anime", "ludikids").any { text.contains(it) }) {
            return ChannelTheme.KIDS
        }
        if (listOf("ushuaia", "histoire", "planete", "geo", "national geographic", "discovery", "science", "nature", "animaux", "voyage", "trek", "chasse", "peche", "terres", "museum", "doc", "seasons").any { text.contains(it) }) {
            return ChannelTheme.DISCOVERY
        }
        if (listOf("trace", "mcm", "music", "hits", "mtv", "mezzo", "melody", "radio", "clubbing", "rfm", "nrj").any { text.contains(it) }) {
            return ChannelTheme.MUSIC
        }
        return ChannelTheme.GENERAL
    }

    fun getAvailablePacks(channels: List<ChannelCatalog.PresetChannel>): List<ChannelPack> {
        val sportIds = channels.filter { getTheme(it.id, it.displayName) == ChannelTheme.SPORT }.map { it.id }
        val cinemaIds = channels.filter { getTheme(it.id, it.displayName) == ChannelTheme.CINEMA }.map { it.id }
        val belgiumIds = channels.filter { getRegion(it.id) == ChannelRegion.BELGIUM }.map { it.id }
        val swissIds = channels.filter { getRegion(it.id) == ChannelRegion.SWITZERLAND }.map { it.id }
        val canadaIds = channels.filter { getRegion(it.id) == ChannelRegion.CANADA }.map { it.id }
        val kidsIds = channels.filter { getTheme(it.id, it.displayName) == ChannelTheme.KIDS }.map { it.id }
        val discoveryIds = channels.filter { getTheme(it.id, it.displayName) == ChannelTheme.DISCOVERY }.map { it.id }
        val musicIds = channels.filter { getTheme(it.id, it.displayName) == ChannelTheme.MUSIC }.map { it.id }

        return listOf(
            ChannelPack("sport", "Pack Sport", "beIN, Eurosport, Automoto...", "⚽", sportIds),
            ChannelPack("cinema", "Pack Cinéma & Séries", "Canal+, Ciné+, Warner TV...", "🎬", cinemaIds),
            ChannelPack("belgium", "Pack Belgique", "RTBF, RTL-TVI, Tipik...", "🇧🇪", belgiumIds),
            ChannelPack("switzerland", "Pack Suisse", "RTS, 3+, Teleboy...", "🇨🇭", swissIds),
            ChannelPack("canada", "Pack Canada", "TVA, Radio-Canada, Noovo...", "🇨🇦", canadaIds),
            ChannelPack("kids", "Pack Jeunesse", "Disney, Nickelodeon, Mangas...", "🧒", kidsIds),
            ChannelPack("discovery", "Pack Découverte", "Ushuaïa, Histoire, Planète+...", "🌿", discoveryIds),
            ChannelPack("music", "Pack Musique", "Trace, MCM, MTV, NRJ Hits...", "🎵", musicIds)
        )
    }
}

