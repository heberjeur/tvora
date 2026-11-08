package com.mohamedaminelouati.tvora.data.local

import com.mohamedaminelouati.tvora.R

enum class ChannelRegion(val code: String, val flagEmoji: String, val labelRes: Int) {
    ALL("all", "🌐", R.string.filter_country_all),
    FRANCE("fr", "🇫🇷", R.string.region_france),
    BELGIUM("be", "🇧🇪", R.string.region_belgium),
    SWITZERLAND("ch", "🇨🇭", R.string.region_switzerland),
    CANADA("ca", "🇨🇦", R.string.region_canada),
    AFRICA("af", "🌍", R.string.region_africa),
    INTERNATIONAL("intl", "🌐", R.string.region_intl)
}

enum class ChannelTheme(val code: String, val iconEmoji: String, val labelRes: Int) {
    ALL("all", "📺", R.string.filter_genre_all),
    GENERAL("general", "📺", R.string.theme_general),
    SPORT("sport", "⚽", R.string.theme_sport),
    CINEMA("cinema", "🎬", R.string.theme_cinema),
    NEWS("news", "📰", R.string.theme_news),
    KIDS("kids", "🧸", R.string.theme_kids),
    DISCOVERY("discovery", "🌍", R.string.theme_discovery),
    MUSIC("music", "🎵", R.string.theme_music)
}

object ChannelClassification {

    private val belgianSpecialIds = setOf("ab3.fr", "abxplorefr.fr")

    private val africanSpecialIds = setOf(
        "2mmonde.fr",
        "tv5mondeafrique.fr",
        "bblackafrica.fr",
        "traceafrica.fr",
        "tracetoca.fr",
        "tracetropical.fr"
    )

    private val internationalSpecialIds = setOf(
        "cgtn.fr",
        "m6international.fr",
        "eurosportinternational1.fr",
        "euronews.fr",
        "tvmonaco.fr"
    )

    fun getRegion(channelId: String): ChannelRegion {
        val lower = channelId.lowercase()
        if (lower.contains("cstar")) return ChannelRegion.FRANCE
        if (lower.contains("canalplus") || lower.contains("canal+")) {
            if (lower.endsWith(".ch")) return ChannelRegion.SWITZERLAND
            return ChannelRegion.FRANCE
        }
        if (belgianSpecialIds.contains(lower) || lower.endsWith(".be")) return ChannelRegion.BELGIUM
        if (lower.endsWith(".ch")) return ChannelRegion.SWITZERLAND
        if (lower.endsWith(".ca")) return ChannelRegion.CANADA
        if (africanSpecialIds.contains(lower) || lower.endsWith(".af") || lower.endsWith(".ci") || lower.endsWith(".sn") || lower.endsWith(".ne")) return ChannelRegion.AFRICA
        if (internationalSpecialIds.contains(lower) || lower.endsWith(".uk") || lower.endsWith(".de") || lower.endsWith(".pt") || lower.endsWith(".es") || lower.endsWith(".ar") || lower.endsWith(".us") || lower.endsWith(".cn") || lower.endsWith(".br")) return ChannelRegion.INTERNATIONAL
        if (lower.endsWith(".fr") || lower.endsWith(".frdrom")) return ChannelRegion.FRANCE
        return ChannelRegion.INTERNATIONAL
    }

    fun getTheme(channelId: String, channelName: String): ChannelTheme {
        val text = "$channelId $channelName".lowercase()

        if (text.contains("j'irai dormir") || text.contains("jdcv")) {
            return ChannelTheme.DISCOVERY
        }

        if (listOf(
                "sport", "foot", "golf", "auto", "moteur", "bola", "equidia", "fight",
                "racing", "tennis", "rugby", "eurosport", "bein", "lequipe", "dazn",
                "eleven", "voosport", "rds", "tsn", "tvasports", "chevaltv", "ligue1",
                "premierleague", "nautical", "automoto", "proximus11", "rmcmecanic",
                "talksport", "extreme"
            ).any { text.contains(it) } || Regex("""pickx.*sport|live\d+|canal\+.*sport|canal\+.*foot|sport\+""").containsMatchIn(text)
        ) {
            return ChannelTheme.SPORT
        }

        if (listOf(
                "disney", "gulli", "mangas", "cartoon", "nickelodeon", "toons", "boing",
                "boomerang", "kids", "tiptv", "canalj", "tiji", "jeunesse", "anime",
                "ludikids", "babytv", "piwi", "teletoon", "vrak", "yoopa", "ketnet",
                "vtmkids", "junior", "ludo", "gong", "adultswim"
            ).any { text.contains(it) }
        ) {
            return ChannelTheme.KIDS
        }

        if (text.contains("canalplus") || text.contains("canal+")) {
            if (listOf("docs", "planete").any { text.contains(it) }) {
                return ChannelTheme.DISCOVERY
            }
            return ChannelTheme.CINEMA
        }

        if (listOf(
                "cinema", "cine", "series", "film", "action", "tcm", "paramount", "warner",
                "polar", "comedie", "novelas", "thriller", "serie", "13erue", "13eme",
                "syfy", "altice", "frissons", "superecran", "addik", "prise2", "fiction",
                "bet.fr", "comedy", "crimedistrict", "sonyone"
            ).any { text.contains(it) } || Regex("""grand.*ecran|box.*office""").containsMatchIn(text)
        ) {
            return ChannelTheme.CINEMA
        }

        if (listOf(
                "info", "news", "euronews", "bfm", "cnews", "lci", "lcp", "senat",
                "france24", "01tv", "business", "meteo", "parlement", "ln24", "lcn",
                "rdi", "cpac", "cnn", "cnbc", "bloomberg", "figaro", "actua", "public",
                "aljazeera", "alarabiya", "africa24", "canalz", "kanaalz", "cgtn",
                "bsmart", "bbcworld", "reginfof", "talkinfo"
            ).any { text.contains(it) } || Regex("""\bdw\b""").containsMatchIn(text)
        ) {
            return ChannelTheme.NEWS
        }

        if (listOf(
                "ushuaia", "histoire", "planete", "geo", "national geographic", "discovery",
                "science", "nature", "animaux", "voyage", "trek", "chasse", "peche",
                "terres", "museum", "seasons", "decouverte", "explora", "canald",
                "historia", "evasion", "zeste", "casa", "investigation", "savoi",
                "earth", "archeo", "myste", "enquetes", "rmcwow", "zoneinterdite"
            ).any { text.contains(it) } || Regex("""alerte.*secours|\bdocs?\b""").containsMatchIn(text)
        ) {
            return ChannelTheme.DISCOVERY
        }

        if (listOf(
                "trace", "mcm", "music", "hits", "mtv", "mezzo", "melody", "radio",
                "clubbing", "rfm", "nrj", "cstar", "stingray", "classica", "festival",
                "palmares", "singa", "afro", "zik", "bblack", "musique", "beurfmtv"
            ).any { text.contains(it) }
        ) {
            return ChannelTheme.MUSIC
        }

        return ChannelTheme.GENERAL
    }
}
