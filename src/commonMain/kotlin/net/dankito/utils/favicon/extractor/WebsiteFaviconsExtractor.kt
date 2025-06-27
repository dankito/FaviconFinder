package net.dankito.utils.favicon.extractor

import net.dankito.utils.favicon.Favicon

interface WebsiteFaviconsExtractor {

    suspend fun extractFavicons(url: String, webSiteHtml: String): List<Favicon>

}