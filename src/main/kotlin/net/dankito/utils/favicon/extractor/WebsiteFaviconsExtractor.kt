package net.dankito.utils.favicon.extractor

import net.dankito.utils.favicon.Favicon

interface WebsiteFaviconsExtractor {

    fun extractFavicons(url: String, webSiteHtml: String): List<Favicon>

}