package com.mohamedaminelouati.tvora.domain.model

enum class ChannelPackage(
    val id: String,
    val feedFileName: String,
    val feedUrl: String
) {
    COMMUNITY_ENRICHED(
        id = "community_enriched",
        feedFileName = "epg_tnt.xml.gz",
        feedUrl = "https://heberjeur.github.io/tvora/epg_tnt.xml.gz"
    );

    fun getFeedUrl(lang: String): String = when (lang) {
        "en" -> "https://heberjeur.github.io/tvora/epg_tnt_en.xml.gz"
        else -> feedUrl
    }

    fun getFallbackUrl(lang: String): String = when (lang) {
        "en" -> "https://raw.githubusercontent.com/heberjeur/tvora/gh-pages/epg_tnt_en.xml.gz"
        else -> "https://raw.githubusercontent.com/heberjeur/tvora/gh-pages/epg_tnt.xml.gz"
    }

    fun getFeedFileName(lang: String): String = when (lang) {
        "en" -> "epg_tnt_en.xml.gz"
        else -> feedFileName
    }

    companion object {
        fun fromId(id: String): ChannelPackage = COMMUNITY_ENRICHED
    }
}
