package com.mohamedshihaa.faselhd

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.MainAPI
import org.jsoup.nodes.Element

class FaselHDProvider : MainAPI() {
    override var mainUrl = "https://faselhd.link"
    override var name = "FaselHD"
    override var lang = "ar"
    override val hasMainPage = false
    override val supportedTypes = setOf(TvType.Movie)

    override suspend fun search(query: String): List<SearchResponse> {
        val url = "$mainUrl/search/${query.fixSearch()}"
        val document = app.get(url).document

        return document.select("div.GridItem.Card").mapNotNull {
            it.toSearchResponse()
        }
    }

   private fun Element.toSearchResponse(): MovieSearchResponse? {
    val aTag = selectFirst("a") ?: return null
    val href = fixUrl(aTag.attr("href"))
    val img = selectFirst("img") ?: return null
    val title = selectFirst("h3.Title")?.text()?.trim() ?: return null
    val posterUrl = fixUrl(img.attr("data-src"))

    return newMovieSearchResponse {
        this.name = title
        this.url = href
        this.apiName = this@FaselHDProvider.name
        this.type = TvType.Movie
        this.posterUrl = posterUrl
    }
}


    private fun String.fixSearch(): String {
        return this.trim().replace(" ", "-")
    }
}
