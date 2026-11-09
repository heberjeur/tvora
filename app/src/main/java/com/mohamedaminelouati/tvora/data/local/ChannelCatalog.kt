package com.mohamedaminelouati.tvora.data.local

object ChannelCatalog {

    val tntChannelIds: List<String> = listOf(
        "TF1.fr",
        "France2.fr",
        "France3.fr",
        "France4.fr",
        "France5.fr",
        "M6.fr",
        "Arte.fr",
        "LCP.fr",
        "W9.fr",
        "TMC.fr",
        "TFX.fr",
        "Gulli.fr",
        "BFMTV.fr",
        "CNews.fr",
        "LCI.fr",
        "FranceInfo.fr",
        "CStar.fr",
        "T18.fr",
        "NOVO19.fr",
        "TF1SeriesFilms.fr",
        "LEquipe.fr",
        "6ter.fr",
        "RMCStory.fr",
        "RMCDecouverte.fr",
        "Cherie25.fr",
        "CanalPlus.fr",
        "TV5Monde.fr",
        "France24.fr"
    )

    private val tntChannelMap = mapOf(
        "TF1.fr" to 1,
        "France2.fr" to 2,
        "France3.fr" to 3,
        "France4.fr" to 4,
        "France5.fr" to 5,
        "M6.fr" to 6,
        "Arte.fr" to 7,
        "LCP.fr" to 8,
        "LaChaineParlementaire.fr" to 8,
        "W9.fr" to 9,
        "TMC.fr" to 10,
        "TFX.fr" to 11,
        "NT1.fr" to 11,
        "Gulli.fr" to 12,
        "BFMTV.fr" to 13,
        "CNews.fr" to 14,
        "LCI.fr" to 15,
        "FranceInfo.fr" to 16,
        "CStar.fr" to 17,
        "CStar.ch" to 17,
        "T18.fr" to 18,
        "NOVO19.fr" to 19,
        "TF1SeriesFilms.fr" to 20,
        "LEquipe.fr" to 21,
        "LEquipe21.fr" to 21,
        "6ter.fr" to 22,
        "RMCStory.fr" to 23,
        "Numero23.fr" to 23,
        "RMCDecouverte.fr" to 24,
        "Cherie25.fr" to 25,
        "RMCLife.fr" to 25,
        "CanalPlus.fr" to 26,
        "CanalPlus" to 26,
        "canalplus" to 26,
        "Canal+" to 26,
        "TV5Monde.fr" to 27,
        "France24.fr" to 28
    )

    private val logoMap = mapOf(
        "TF1.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~f2b6de26acc4dc3e.png/480x480/quality/100/tf1.png",
        "France2.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~606394a5b095653a.png/480x480/quality/100/france-2.png",
        "France3.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~61eebbb6e38360ae.png/480x480/quality/100/france-3.png",
        "France4.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~e02946cfb82e5310.png/480x480/quality/100/france-4.png",
        "France5.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~5f611b705ca3f017.png/480x480/quality/100/france-5.png",
        "M6.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~46c2dbade9ac9fed.png/480x480/quality/100/m6.png",
        "Arte.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~d6bb7624abff43ac.png/480x480/quality/100/arte.png",
        "LCP.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~080eb27517dd16c6.png/480x480/quality/100/la-chaine-parlementaire.png",
        "LaChaineParlementaire.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~080eb27517dd16c6.png/480x480/quality/100/la-chaine-parlementaire.png",
        "W9.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~15acfc705e2d4dd8.png/480x480/quality/100/w9.png",
        "TMC.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~b2c9618d914140d0.png/480x480/quality/100/tmc.png",
        "TFX.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~71e551462d6114e5.png/480x480/quality/100/tfx.png",
        "NT1.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~71e551462d6114e5.png/480x480/quality/100/tfx.png",
        "Gulli.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~059a8175b83117fd.png/480x480/quality/100/gulli.png",
        "BFMTV.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~9048e02338fb1134.png/480x480/quality/100/bfmtv.png",
        "CNews.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~952ec4ae63b20c1b.png/480x480/quality/100/cnews.png",
        "LCI.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~1314d39cfeea2a5c.png/480x480/quality/100/lci-la-chaine-info.png",
        "FranceInfo.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~367eff2d23ab1357.png/480x480/quality/100/franceinfo.png",
        "CStar.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~9583d8a6fa9d6ade.png/480x480/quality/100/cstar.png",
        "CStar.ch" to "https://www.programme-tv.net/imgre/fit/~2~channel~9583d8a6fa9d6ade.png/480x480/quality/100/cstar.png",
        "T18.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~3deccc95e7e75463.png/480x480/quality/100/t18.png",
        "NOVO19.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~113b817bb2e5c3a6.png/480x480/quality/100/novo19.png",
        "TF1SeriesFilms.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~3104ccb3015a8669.png/480x480/quality/100/tf1-series-films.png",
        "LEquipe.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~060edf53b072af43.png/480x480/quality/100/l-equipe.png",
        "LEquipe21.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~060edf53b072af43.png/480x480/quality/100/l-equipe.png",
        "6ter.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~ceee54072118570a.png/480x480/quality/100/6ter.png",
        "RMCStory.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~56b83775344955b8.png/480x480/quality/100/rmc-story.png",
        "Numero23.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~56b83775344955b8.png/480x480/quality/100/rmc-story.png",
        "RMCDecouverte.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~a95c9fa646201300.png/480x480/quality/100/rmc-decouverte.png",
        "RMCLife.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~91bf2b7762746d0c.png/480x480/quality/100/rmc-life.png",
        "Cherie25.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~91bf2b7762746d0c.png/480x480/quality/100/rmc-life.png",
        "CanalPlus.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~897223a57e2e8ae5.png/480x480/quality/100/canal.png",
        "CanalPlus" to "https://www.programme-tv.net/imgre/fit/~2~channel~897223a57e2e8ae5.png/480x480/quality/100/canal.png",
        "Canal+" to "https://www.programme-tv.net/imgre/fit/~2~channel~897223a57e2e8ae5.png/480x480/quality/100/canal.png",
        "TV5Monde.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~0231873fab336015.png/480x480/quality/100/tv5monde.png",
        "France24.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~afbbeba076c073d9.png/480x480/quality/100/france-24.png",
        "ParisPremiere.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~ecf598fd002ccb9a.png/480x480/quality/100/paris-premiere.png",
        "RTL9.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~39e76725dfcc33a3.png/480x480/quality/100/rtl-9.png",
        "Teva.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~4421904d874fa072.png/480x480/quality/100/teva.png",
        "CanalPlusCinema.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~0b078790aeb2d121.png/480x480/quality/100/canal-cinema.png",
        "CanalPlusSport.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~2b97b0fb1264c28d.png/480x480/quality/100/canal-sport.png",
        "CanalPlusSeries.fr" to "https://focus.telerama.fr/500x500/0000/00/01/clear-1563.png",
        "beINSPORTS1.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~8884f09c3361af44.png/480x480/quality/100/bein-sports-1.png",
        "BeInSports1.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~8884f09c3361af44.png/480x480/quality/100/bein-sports-1.png",
        "beINSPORTS2.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~2fa633f39d6c3033.png/480x480/quality/100/bein-sports-2.png",
        "BeInSports2.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~2fa633f39d6c3033.png/480x480/quality/100/bein-sports-2.png",
        "Eurosport1.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~277882f801bd3f7b.png/480x480/quality/100/eurosport-1.png",
        "Eurosport2.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~196acf0142c66a68.png/480x480/quality/100/eurosport-2.png",
        "TvBreizh.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~eda4c23255b4fed2.png/480x480/quality/100/tvbreizh.png",
        "TVBreizh.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~eda4c23255b4fed2.png/480x480/quality/100/tvbreizh.png",
        "UshuaiaTV.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~778073cbbd827d7a.png/480x480/quality/100/ushuaia-tv.png",
        "Histoire.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~354060da3ea6eca2.png/480x480/quality/100/histoire-tv.png",
        "HistoireTV.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~354060da3ea6eca2.png/480x480/quality/100/histoire-tv.png",
        "AB1.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~a15b4a2cb74dfd54.png/480x480/quality/100/ab-1.png",
        "WarnerTV.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~6d75450123e766d6.png/480x480/quality/100/warner-tv.png",
        "ComediePlus.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~b419616020702bc2.png/480x480/quality/100/comedie.png",
        "PlanetePlus.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~475677d45988adc5.png/480x480/quality/100/planete.png",
        "NationalGeographic.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~c00912008deb7144.png/480x480/quality/100/national-geographic.png",
        "13eRue.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~c7a8275787244ffe.png/480x480/quality/100/13eme-rue.png",
        "13emeRue.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~c7a8275787244ffe.png/480x480/quality/100/13eme-rue.png",
        "Syfy.fr" to "https://raw.githubusercontent.com/tv-logo/tv-logos/main/countries/united-states/syfy-us.png",
        "DisneyChannel.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~ab11120cc94674c8.png/480x480/quality/100/disney-channel.png",
        "Mangas.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~a624ee4f7be827c4.png/480x480/quality/100/mangas.png",
        "BFMBusiness.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~7eb91da112d351e5.png/480x480/quality/100/bfm-business.png",
        "Euronews.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~648eb1fd8a151da0.png/480x480/quality/100/euronews.png"
    )

    private val popularityRankMap = mapOf(
        "TF1.fr" to 1,
        "France2.fr" to 2,
        "France3.fr" to 3,
        "France4.fr" to 4,
        "France5.fr" to 5,
        "M6.fr" to 6,
        "Arte.fr" to 7,
        "TV5Monde.fr" to 8,
        "TMC.fr" to 9,
        "W9.fr" to 10,
        "TFX.fr" to 11,
        "6ter.fr" to 12,
        "Gulli.fr" to 13,
        "CanalPlus.fr" to 14,
        "T18.fr" to 15,
        "NOVO19.fr" to 16,
        "CStar.fr" to 17,
        "RMCDecouverte.fr" to 18,
        "RMCStory.fr" to 19,
        "RMCLife.fr" to 20,
        "Cherie25.fr" to 20,
        "TF1SeriesFilms.fr" to 21,
        "LEquipe.fr" to 22,
        "France24.fr" to 23,
        "FranceInfo.fr" to 24,
        "CNews.fr" to 25,
        "BFMTV.fr" to 26,
        "LCI.fr" to 27,
        "LCP.fr" to 28,
        "ParisPremiere.fr" to 29,
        "RTL9.fr" to 30,
        "Teva.fr" to 31,
        "CanalPlusCinema.fr" to 32,
        "CanalPlusSport.fr" to 33,
        "CanalPlusSeries.fr" to 34,
        "beINSPORTS1.fr" to 35,
        "beINSPORTS2.fr" to 36,
        "Eurosport1.fr" to 37,
        "Eurosport2.fr" to 38,
        "TvBreizh.fr" to 39,
        "UshuaiaTV.fr" to 40,
        "Histoire.fr" to 41,
        "AB1.fr" to 42,
        "WarnerTV.fr" to 43,
        "ComediePlus.fr" to 44,
        "PlanetePlus.fr" to 45,
        "NationalGeographic.fr" to 46,
        "13eRue.fr" to 47,
        "Syfy.fr" to 48,
        "DisneyChannel.fr" to 49,
        "Mangas.fr" to 50,
        "BFMBusiness.fr" to 51,
        "Euronews.fr" to 52
    )

    private val channelNameMap = mapOf(
        "TF1.fr" to "TF1",
        "France2.fr" to "France 2",
        "France3.fr" to "France 3",
        "France4.fr" to "France 4",
        "France5.fr" to "France 5",
        "M6.fr" to "M6",
        "Arte.fr" to "Arte",
        "LCP.fr" to "LCP",
        "LaChaineParlementaire.fr" to "LCP",
        "W9.fr" to "W9",
        "TMC.fr" to "TMC",
        "TFX.fr" to "TFX",
        "NT1.fr" to "TFX",
        "Gulli.fr" to "Gulli",
        "BFMTV.fr" to "BFMTV",
        "CNews.fr" to "CNews",
        "LCI.fr" to "LCI",
        "FranceInfo.fr" to "franceinfo",
        "CStar.fr" to "CStar",
        "CStar.ch" to "CStar",
        "T18.fr" to "T18",
        "NOVO19.fr" to "NOVO19",
        "TF1SeriesFilms.fr" to "TF1 Séries Films",
        "LEquipe.fr" to "L'Équipe",
        "LEquipe21.fr" to "L'Équipe",
        "6ter.fr" to "6ter",
        "RMCStory.fr" to "RMC Story",
        "Numero23.fr" to "RMC Story",
        "RMCDecouverte.fr" to "RMC Découverte",
        "Cherie25.fr" to "Chérie 25",
        "RMCLife.fr" to "Chérie 25",
        "CanalPlus.fr" to "Canal+",
        "CanalPlus" to "Canal+",
        "Canal+" to "Canal+",
        "TV5Monde.fr" to "TV5 Monde",
        "France24.fr" to "France 24",
        "ParisPremiere.fr" to "Paris Première",
        "RTL9.fr" to "RTL9",
        "Teva.fr" to "Téva",
        "CanalPlusCinema.fr" to "Canal+ Cinéma",
        "CanalPlusSport.fr" to "Canal+ Sport",
        "CanalPlusSeries.fr" to "Canal+ Séries",
        "beINSPORTS1.fr" to "beIN SPORTS 1",
        "BeInSports1.fr" to "beIN SPORTS 1",
        "beINSPORTS2.fr" to "beIN SPORTS 2",
        "BeInSports2.fr" to "beIN SPORTS 2",
        "Eurosport1.fr" to "Eurosport 1",
        "Eurosport2.fr" to "Eurosport 2",
        "TvBreizh.fr" to "TV Breizh",
        "TVBreizh.fr" to "TV Breizh",
        "UshuaiaTV.fr" to "Ushuaïa TV",
        "Histoire.fr" to "Histoire TV",
        "HistoireTV.fr" to "Histoire TV",
        "AB1.fr" to "AB1",
        "WarnerTV.fr" to "Warner TV",
        "ComediePlus.fr" to "Comédie+",
        "PlanetePlus.fr" to "Planète+",
        "NationalGeographic.fr" to "National Geographic",
        "13eRue.fr" to "13ème Rue",
        "13emeRue.fr" to "13ème Rue",
        "Syfy.fr" to "Syfy",
        "DisneyChannel.fr" to "Disney Channel",
        "Mangas.fr" to "Mangas",
        "BFMBusiness.fr" to "BFM Business",
        "Euronews.fr" to "Euronews"
    )

    fun getChannelNumber(channelId: String): Int {
        val direct = tntChannelMap[channelId]
        if (direct != null) return direct
        val normalized = normalizeId(channelId)
        val tntNum = tntChannelMap.entries.firstOrNull {
            normalizeId(it.key) == normalized
        }?.value
        if (tntNum != null) return tntNum
        return extraChannelsMap[normalized]?.channelNumber ?: 0
    }

    fun getPopularityRank(channelId: String): Int {
        val direct = popularityRankMap[channelId]
        if (direct != null) return direct
        val normalized = normalizeId(channelId)
        return popularityRankMap.entries.firstOrNull {
            normalizeId(it.key) == normalized
        }?.value ?: (extraChannelsMap[normalized]?.channelNumber ?: Int.MAX_VALUE)
    }

    fun getDisplayName(channelId: String): String {
        val direct = channelNameMap[channelId]
        if (direct != null) return direct
        val normalized = normalizeId(channelId)
        val tntName = channelNameMap.entries.firstOrNull {
            normalizeId(it.key) == normalized
        }?.value
        if (tntName != null) return tntName
        val extra = extraChannelsMap[normalized]
        if (extra != null) return extra.displayName
        return channelId.removeSuffix(".fr")
    }

    fun getLogoUrl(channelId: String): String? {
        val direct = logoMap[channelId]
        if (direct != null) return direct
        val normalized = normalizeId(channelId)
        val tntLogo = logoMap.entries.firstOrNull {
            normalizeId(it.key) == normalized
        }?.value
        if (tntLogo != null) return tntLogo
        return extraChannelsMap[normalized]?.iconUrl
    }

    fun isCatalogChannel(channelId: String): Boolean {
        if (channelNameMap.containsKey(channelId)) return true
        val normalized = normalizeId(channelId)
        if (channelNameMap.keys.any { normalizeId(it) == normalized }) return true
        return extraChannelsMap.containsKey(normalized)
    }

    fun getAllLogoUrls(): List<String> {
        return (logoMap.values + extraChannelsMap.values.map { it.iconUrl }).filter { it.isNotBlank() }.distinct()
    }

    fun isFavorite(channelId: String, favoriteChannels: Set<String>): Boolean {
        if (favoriteChannels.isEmpty()) return false
        if (favoriteChannels.contains(channelId)) return true
        val normalized = normalizeId(channelId)
        return favoriteChannels.any { normalizeId(it) == normalized }
    }

    fun isDeadChannel(channelId: String): Boolean {
        val lower = channelId.trim().lowercase()
        return lower in listOf(
            "nt1.fr", "nt1",
            "numero23.fr", "numero23",
            "lequipe21.fr", "lequipe21",
            "d17.fr", "d17",
            "direct8.fr", "direct8",
            "directstar.fr", "directstar",
            "cstar.ch",
            "lachaineparlementaire.fr"
        )
    }

    fun isTntChannel(channelId: String): Boolean {
        val norm = normalizeId(channelId)
        return tntChannelIds.any { normalizeId(it) == norm }
    }

    fun normalizeId(id: String): String {
        val lower = id.trim().lowercase()
        val stripped = lower
            .removeSuffix(".frdrom")
            .removeSuffix(".fr")
            .replace("+", "plus")
        val clean = stripped.replace("[^a-z0-9]".toRegex(), "")
        return when (clean) {
            "canalplus", "canal", "c" -> "canalplus"
            "nt1", "tfx" -> "tfx"
            "lachaineparlementaire", "lcp" -> "lcp"
            "lequipe21", "lequipe" -> "lequipe"
            "numero23", "rmcstory" -> "rmcstory"
            "cherie25", "rmclife" -> "cherie25"
            "cstar", "d17", "cstarch" -> "cstar"
            "13emerue", "13erue" -> "13erue"
            "scifi", "syfy" -> "syfy"
            else -> clean
        }
    }

    fun canonicalChannelId(id: String): String {
        val norm = normalizeId(id)
        val direct = when (norm) {
            "canalplus" -> "CanalPlus.fr"
            "tmc" -> "TMC.fr"
            "tfx" -> "TFX.fr"
            "lcp" -> "LCP.fr"
            "lequipe" -> "LEquipe.fr"
            "rmcstory" -> "RMCStory.fr"
            "cherie25", "rmclife" -> "Cherie25.fr"
            "cstar" -> "CStar.fr"
            "parispremiere" -> "ParisPremiere.fr"
            "rtl9" -> "RTL9.fr"
            "teva" -> "Teva.fr"
            "canalpluscinema" -> "CanalPlusCinema.fr"
            "canalplussport" -> "CanalPlusSport.fr"
            "canalplusseries" -> "CanalPlusSeries.fr"
            "beinsports1" -> "beINSPORTS1.fr"
            "beinsports2" -> "beINSPORTS2.fr"
            "eurosport1" -> "Eurosport1.fr"
            "eurosport2" -> "Eurosport2.fr"
            "tvbreizh" -> "TvBreizh.fr"
            "ushuaia", "ushuaiatv" -> "UshuaiaTV.fr"
            "histoire", "histoiretv" -> "Histoire.fr"
            "ab1" -> "AB1.fr"
            "warnertv" -> "WarnerTV.fr"
            "comedieplus", "comedie" -> "ComediePlus.fr"
            "planeteplus", "planete" -> "PlanetePlus.fr"
            "nationalgeographic" -> "NationalGeographic.fr"
            "13erue" -> "13eRue.fr"
            "syfy" -> "Syfy.fr"
            "disneychannel" -> "DisneyChannel.fr"
            "mangas" -> "Mangas.fr"
            "bfmbusiness" -> "BFMBusiness.fr"
            "euronews" -> "Euronews.fr"
            "tv5monde" -> "TV5Monde.fr"
            "france24" -> "France24.fr"
            else -> null
        }
        if (direct != null) return direct
        val preset = extraChannelsMap[norm]
        if (preset != null) return preset.id
        return id
    }

    fun getChannelAliases(channelId: String): List<String> {
        val canonical = canonicalChannelId(channelId)
        val aliases = mutableSetOf(channelId, canonical)
        val norm = normalizeId(channelId)
        when (norm) {
            "canalplus" -> aliases.addAll(listOf("CanalPlus.fr", "CanalPlus", "canalplus", "Canal+", "c"))
            "tmc" -> aliases.addAll(listOf("TMC.fr", "TMC", "tmc", "TeleMonteCarlo"))
            "tfx" -> aliases.addAll(listOf("TFX.fr", "TFX", "tfx", "NT1.fr", "NT1", "nt1"))
            "lcp" -> aliases.addAll(listOf("LCP.fr", "LCP", "lcp", "LaChaineParlementaire.fr", "LaChaineParlementaire"))
            "lequipe" -> aliases.addAll(listOf("LEquipe.fr", "LEquipe", "lequipe", "LEquipe21.fr", "LEquipe21", "lequipe21"))
            "rmcstory" -> aliases.addAll(listOf("RMCStory.fr", "RMCStory", "rmcstory", "Numero23.fr", "Numero23", "numero23"))
            "rmcdecouverte" -> aliases.addAll(listOf("RMCDecouverte.fr", "RMCDecouverte", "rmcdecouverte"))
            "cherie25", "rmclife" -> aliases.addAll(listOf("Cherie25.fr", "Cherie25", "cherie25", "RMCLife.fr", "RMCLife", "rmclife", "Chérie 25"))
            "cstar" -> aliases.addAll(listOf("CStar.fr", "CStar", "cstar", "CStar.ch", "D17.fr", "D17"))
            "13erue" -> aliases.addAll(listOf("13eRue.fr", "13emeRue.fr", "13eme-rue.fr", "13erue", "13emerue"))
            "syfy" -> aliases.addAll(listOf("Syfy.fr", "SciFi.fr", "scifi", "syfy"))
            "tv5monde" -> aliases.addAll(listOf("TV5Monde.fr", "tv5monde", "TV5.ca", "TV5MondeEurope.fr"))
            "france24" -> aliases.addAll(listOf("France24.fr", "france24", "France 24"))
            "parispremiere" -> aliases.addAll(listOf("ParisPremiere.fr", "ParisPremiere", "parispremiere", "Paris Première"))
            "rtl9" -> aliases.addAll(listOf("RTL9.fr", "RTL9", "rtl9", "RTL 9"))
            "teva" -> aliases.addAll(listOf("Teva.fr", "Teva", "teva", "Téva"))
            "canalpluscinema" -> aliases.addAll(listOf("CanalPlusCinema.fr", "CanalPlusCinema", "canalpluscinema", "Canal+ Cinéma", "Canal+ Cinema"))
            "canalplussport" -> aliases.addAll(listOf("CanalPlusSport.fr", "CanalPlusSport", "canalplussport", "Canal+ Sport"))
            "canalplusseries" -> aliases.addAll(listOf("CanalPlusSeries.fr", "CanalPlusSeries", "canalplusseries", "Canal+ Séries", "Canal+ Series"))
            "tvbreizh" -> aliases.addAll(listOf("TVBreizh.fr", "TvBreizh.fr", "tvbreizh"))
            "histoire" -> aliases.addAll(listOf("Histoire.fr", "HistoireTV.fr", "histoire"))
            "beinsports1" -> aliases.addAll(listOf("beINSPORTS1.fr", "BeInSports1.fr", "beinsports1"))
            "beinsports2" -> aliases.addAll(listOf("beINSPORTS2.fr", "BeInSports2.fr", "beinsports2"))
            "eurosport1" -> aliases.addAll(listOf("Eurosport1.fr", "eurosport1", "Eurosport 1"))
            "eurosport2" -> aliases.addAll(listOf("Eurosport2.fr", "eurosport2", "Eurosport 2"))
            "ushuaia", "ushuaiatv" -> aliases.addAll(listOf("UshuaiaTV.fr", "UshuaiaTV", "ushuaia", "ushuaiatv", "Ushuaïa TV"))
            "ab1" -> aliases.addAll(listOf("AB1.fr", "ab1", "AB 1"))
            "warnertv" -> aliases.addAll(listOf("WarnerTV.fr", "warnertv", "Warner TV"))
            "comedieplus" -> aliases.addAll(listOf("ComediePlus.fr", "comedieplus", "Comédie+", "comedie"))
            "planeteplus" -> aliases.addAll(listOf("PlanetePlus.fr", "planeteplus", "Planète+", "planete"))
            "nationalgeographic" -> aliases.addAll(listOf("NationalGeographic.fr", "nationalgeographic", "National Geographic"))
            "disneychannel" -> aliases.addAll(listOf("DisneyChannel.fr", "disneychannel", "Disney Channel"))
            "mangas" -> aliases.addAll(listOf("Mangas.fr", "mangas", "Mangas"))
            "bfmbusiness" -> aliases.addAll(listOf("BFMBusiness.fr", "bfmbusiness", "BFM Business"))
            "euronews" -> aliases.addAll(listOf("Euronews.fr", "euronews", "Euronews"))
        }
        return aliases.toList()
    }

    data class PresetChannel(
        val id: String,
        val displayName: String,
        val channelNumber: Int,
        val iconUrl: String
    )

    val popularExtraChannels: List<PresetChannel> = PresetChannelsData.all

    private val extraChannelsMap: Map<String, PresetChannel> by lazy {
        popularExtraChannels.associateBy { normalizeId(it.id) }
    }
}
