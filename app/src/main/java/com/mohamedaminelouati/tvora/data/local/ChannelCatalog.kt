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
        "RMCLife.fr",
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
        "W9.fr" to 9,
        "TMC.fr" to 10,
        "TFX.fr" to 11,
        "Gulli.fr" to 12,
        "BFMTV.fr" to 13,
        "CNews.fr" to 14,
        "LCI.fr" to 15,
        "FranceInfo.fr" to 16,
        "CStar.fr" to 17,
        "T18.fr" to 18,
        "NOVO19.fr" to 19,
        "TF1SeriesFilms.fr" to 20,
        "LEquipe.fr" to 21,
        "6ter.fr" to 22,
        "RMCStory.fr" to 23,
        "RMCDecouverte.fr" to 24,
        "RMCLife.fr" to 25,
        "Cherie25.fr" to 25,
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
        "W9.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~15acfc705e2d4dd8.png/480x480/quality/100/w9.png",
        "TMC.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~b2c9618d914140d0.png/480x480/quality/100/tmc.png",
        "TFX.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~71e551462d6114e5.png/480x480/quality/100/tfx.png",
        "Gulli.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~059a8175b83117fd.png/480x480/quality/100/gulli.png",
        "BFMTV.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~9048e02338fb1134.png/480x480/quality/100/bfmtv.png",
        "CNews.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~952ec4ae63b20c1b.png/480x480/quality/100/cnews.png",
        "LCI.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~1314d39cfeea2a5c.png/480x480/quality/100/lci-la-chaine-info.png",
        "FranceInfo.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~367eff2d23ab1357.png/480x480/quality/100/franceinfo.png",
        "CStar.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~9583d8a6fa9d6ade.png/480x480/quality/100/cstar.png",
        "T18.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~3deccc95e7e75463.png/480x480/quality/100/t18.png",
        "NOVO19.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~113b817bb2e5c3a6.png/480x480/quality/100/novo19.png",
        "TF1SeriesFilms.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~3104ccb3015a8669.png/480x480/quality/100/tf1-series-films.png",
        "LEquipe.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~060edf53b072af43.png/480x480/quality/100/l-equipe.png",
        "6ter.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~ceee54072118570a.png/480x480/quality/100/6ter.png",
        "RMCStory.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~56b83775344955b8.png/480x480/quality/100/rmc-story.png",
        "RMCDecouverte.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~a95c9fa646201300.png/480x480/quality/100/rmc-decouverte.png",
        "RMCLife.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~91bf2b7762746d0c.png/480x480/quality/100/rmc-life.png",
        "Cherie25.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~91bf2b7762746d0c.png/480x480/quality/100/rmc-life.png",
        "CanalPlus.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~897223a57e2e8ae5.png/480x480/quality/100/canal.png",
        "CanalPlus" to "https://www.programme-tv.net/imgre/fit/~2~channel~897223a57e2e8ae5.png/480x480/quality/100/canal.png",
        "Canal+" to "https://www.programme-tv.net/imgre/fit/~2~channel~897223a57e2e8ae5.png/480x480/quality/100/canal.png",
        "TV5Monde.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~0231873fab336015.png/480x480/quality/100/tv5monde.png",
        "France24.fr" to "https://www.programme-tv.net/imgre/fit/~2~channel~afbbeba076c073d9.png/480x480/quality/100/france-24.png"
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
        "LCP.fr" to 28
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
        "W9.fr" to "W9",
        "TMC.fr" to "TMC",
        "TFX.fr" to "TFX",
        "Gulli.fr" to "Gulli",
        "BFMTV.fr" to "BFMTV",
        "CNews.fr" to "CNews",
        "LCI.fr" to "LCI",
        "FranceInfo.fr" to "franceinfo",
        "CStar.fr" to "CStar",
        "T18.fr" to "T18",
        "NOVO19.fr" to "NOVO19",
        "TF1SeriesFilms.fr" to "TF1 Séries Films",
        "LEquipe.fr" to "L'Équipe",
        "6ter.fr" to "6ter",
        "RMCStory.fr" to "RMC Story",
        "RMCDecouverte.fr" to "RMC Découverte",
        "RMCLife.fr" to "RMC Life",
        "Cherie25.fr" to "RMC Life",
        "CanalPlus.fr" to "Canal+",
        "CanalPlus" to "Canal+",
        "Canal+" to "Canal+",
        "TV5Monde.fr" to "TV5 Monde",
        "France24.fr" to "France 24"
    )

    fun getChannelNumber(channelId: String): Int {
        val direct = tntChannelMap[channelId]
        if (direct != null) return direct
        val normalized = normalizeId(channelId)
        val tntNum = tntChannelMap.entries.firstOrNull {
            normalizeId(it.key) == normalized
        }?.value
        if (tntNum != null) return tntNum
        return popularExtraChannels.firstOrNull {
            normalizeId(it.id) == normalized
        }?.channelNumber ?: 0
    }

    fun getPopularityRank(channelId: String): Int {
        val direct = popularityRankMap[channelId]
        if (direct != null) return direct
        val normalized = normalizeId(channelId)
        return popularityRankMap.entries.firstOrNull {
            normalizeId(it.key) == normalized
        }?.value ?: Int.MAX_VALUE
    }

    fun getDisplayName(channelId: String): String {
        val direct = channelNameMap[channelId]
        if (direct != null) return direct
        val normalized = normalizeId(channelId)
        val tntName = channelNameMap.entries.firstOrNull {
            normalizeId(it.key) == normalized
        }?.value
        if (tntName != null) return tntName
        val extraName = popularExtraChannels.firstOrNull {
            normalizeId(it.id) == normalized
        }?.displayName
        if (extraName != null) return extraName
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
        return popularExtraChannels.firstOrNull {
            normalizeId(it.id) == normalized
        }?.iconUrl
    }

    fun isCatalogChannel(channelId: String): Boolean {
        if (channelNameMap.containsKey(channelId)) return true
        val normalized = normalizeId(channelId)
        return channelNameMap.keys.any { normalizeId(it) == normalized }
    }

    fun getAllLogoUrls(): List<String> {
        return logoMap.values.distinct()
    }

    fun isFavorite(channelId: String, favoriteChannels: Set<String>): Boolean {
        if (favoriteChannels.isEmpty()) return false
        if (favoriteChannels.contains(channelId)) return true
        val normalized = normalizeId(channelId)
        return favoriteChannels.any { normalizeId(it) == normalized }
    }

    fun normalizeId(id: String): String {
        return id.trim().lowercase().removeSuffix(".fr")
            .replace("+", "plus")
            .replace("[^a-z0-9]".toRegex(), "")
    }

    data class PresetChannel(
        val id: String,
        val displayName: String,
        val channelNumber: Int,
        val iconUrl: String
    )

    val popularExtraChannels: List<PresetChannel> = listOf(
        PresetChannel("ParisPremiere.fr", "Paris Première", 29, "https://www.programme-tv.net/imgre/fit/~2~channel~8ff2cb380fa0e52b.png/480x480/quality/100/paris-premiere.png"),
        PresetChannel("RTL9.fr", "RTL9", 30, "https://www.programme-tv.net/imgre/fit/~2~channel~4e29729864fe78d6.png/480x480/quality/100/rtl9.png"),
        PresetChannel("Teva.fr", "Téva", 31, "https://www.programme-tv.net/imgre/fit/~2~channel~f5be979f4ea5475e.png/480x480/quality/100/teva.png"),
        PresetChannel("CanalPlusCinema.fr", "Canal+ Cinéma", 32, "https://www.programme-tv.net/imgre/fit/~2~channel~b0cb329c362140bb.png/480x480/quality/100/canal-cinema.png"),
        PresetChannel("CanalPlusSport.fr", "Canal+ Sport", 33, "https://www.programme-tv.net/imgre/fit/~2~channel~bf7d99a9a08e6f1a.png/480x480/quality/100/canal-sport.png"),
        PresetChannel("CanalPlusSeries.fr", "Canal+ Séries", 34, "https://www.programme-tv.net/imgre/fit/~2~channel~cf7494191c95a02f.png/480x480/quality/100/canal-series.png"),
        PresetChannel("BeInSports1.fr", "beIN SPORTS 1", 35, "https://www.programme-tv.net/imgre/fit/~2~channel~6ae2e70e9a5e12f6.png/480x480/quality/100/bein-sports-1.png"),
        PresetChannel("BeInSports2.fr", "beIN SPORTS 2", 36, "https://www.programme-tv.net/imgre/fit/~2~channel~6e31b67e5bbdb25d.png/480x480/quality/100/bein-sports-2.png"),
        PresetChannel("Eurosport1.fr", "Eurosport 1", 37, "https://www.programme-tv.net/imgre/fit/~2~channel~bbdf25db8ec3dc76.png/480x480/quality/100/eurosport-1.png"),
        PresetChannel("Eurosport2.fr", "Eurosport 2", 38, "https://www.programme-tv.net/imgre/fit/~2~channel~cb5ec1dbf6b553e1.png/480x480/quality/100/eurosport-2.png"),
        PresetChannel("TVBreizh.fr", "TV Breizh", 39, "https://www.programme-tv.net/imgre/fit/~2~channel~1cebf08d23e59002.png/480x480/quality/100/tv-breizh.png"),
        PresetChannel("UshuaiaTV.fr", "Ushuaïa TV", 40, "https://www.programme-tv.net/imgre/fit/~2~channel~3a37dc68fec3dc87.png/480x480/quality/100/ushuaia-tv.png"),
        PresetChannel("HistoireTV.fr", "Histoire TV", 41, "https://www.programme-tv.net/imgre/fit/~2~channel~9b67482ea0824b42.png/480x480/quality/100/histoire-tv.png"),
        PresetChannel("AB1.fr", "AB1", 42, "https://www.programme-tv.net/imgre/fit/~2~channel~4e89bbde92cfb302.png/480x480/quality/100/ab-1.png"),
        PresetChannel("WarnerTV.fr", "Warner TV", 43, "https://www.programme-tv.net/imgre/fit/~2~channel~cf7494191c95a02f.png/480x480/quality/100/warner-tv.png"),
        PresetChannel("ComediePlus.fr", "Comédie+", 44, "https://www.programme-tv.net/imgre/fit/~2~channel~1a9e34fe37e50c42.png/480x480/quality/100/comedie.png"),
        PresetChannel("PlanetePlus.fr", "Planète+", 45, "https://www.programme-tv.net/imgre/fit/~2~channel~d36c57f20e7df65a.png/480x480/quality/100/planete.png"),
        PresetChannel("NationalGeographic.fr", "National Geographic", 46, "https://www.programme-tv.net/imgre/fit/~2~channel~ea693cb83e921d60.png/480x480/quality/100/national-geographic.png"),
        PresetChannel("13emeRue.fr", "13ème Rue", 47, "https://www.programme-tv.net/imgre/fit/~2~channel~b0cb329c362140bb.png/480x480/quality/100/13eme-rue.png"),
        PresetChannel("Syfy.fr", "Syfy", 48, "https://www.programme-tv.net/imgre/fit/~2~channel~f5be979f4ea5475e.png/480x480/quality/100/syfy.png"),
        PresetChannel("DisneyChannel.fr", "Disney Channel", 49, "https://www.programme-tv.net/imgre/fit/~2~channel~1cebf08d23e59002.png/480x480/quality/100/disney-channel.png"),
        PresetChannel("GameOne.fr", "Game One", 50, "https://www.programme-tv.net/imgre/fit/~2~channel~6ae2e70e9a5e12f6.png/480x480/quality/100/game-one.png"),
        PresetChannel("Mangas.fr", "Mangas", 51, "https://www.programme-tv.net/imgre/fit/~2~channel~bbdf25db8ec3dc76.png/480x480/quality/100/mangas.png"),
        PresetChannel("BFMBusiness.fr", "BFM Business", 52, "https://www.programme-tv.net/imgre/fit/~2~channel~9048e02338fb1134.png/480x480/quality/100/bfm-business.png"),
        PresetChannel("Euronews.fr", "Euronews", 53, "https://www.programme-tv.net/imgre/fit/~2~channel~afbbeba076c073d9.png/480x480/quality/100/euronews.png")
    )
}
