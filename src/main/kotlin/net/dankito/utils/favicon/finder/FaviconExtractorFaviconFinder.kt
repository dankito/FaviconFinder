package net.dankito.utils.favicon.finder

import com.fasterxml.jackson.module.kotlin.readValue
import net.dankito.utils.favicon.Favicon
import net.dankito.utils.favicon.FaviconType
import net.dankito.utils.favicon.Size
import net.dankito.utils.favicon.finder.dto.FaviconExtractorResponse
import net.dankito.utils.favicon.web.UrlUtil
import net.dankito.web.client.WebClient
import net.dankito.web.client.get

/**
 * Fetches the best matching favicon for an url with Favicon Extractor's
 * favicon API (`https://www.faviconextractor.com/api/favicon/<url>`).
 *
 * For FaviconExtractor it's important to set the User-Agent header.
 *
 * For documentation see: [https://github.com/seadfeng/favicon-downloader](https://github.com/seadfeng/favicon-downloader).
 */
open class FaviconExtractorFaviconFinder(
    protected val webClient: WebClient,
    protected val urlUtil: UrlUtil = UrlUtil.Default
) {

    open suspend fun findFavicons(url: String): List<Favicon> {
        val finderUrl = "https://www.faviconextractor.com/api/favicon/${urlUtil.removeProtocolAndWww(url)}"

        val result = webClient.get<FaviconExtractorResponse>(finderUrl) // FaviconExtractor requires that User-Agent header is set (requestDesktopWebsite=true)

        if (result.successful && result.body != null) {
            return result.body!!.icons.map { Favicon(it.href, FaviconType.Icon, mapSize(it.sizes)) } // TODO: actually we cannot know the FaviconType
        }

        return emptyList()
    }

    protected open fun mapSize(sizes: String?): Size? =
        if (sizes == null || "unknown".equals(sizes, ignoreCase = true)) {
            null
        } else if (sizes.contains('x', ignoreCase = true)) {
            val (width, height) = sizes.split('x', ignoreCase = true, limit = 2).map { it.toIntOrNull() }
            if (width != null && height != null) {
                Size(width, height)
            } else {
                null
            }
        } else if (sizes.toIntOrNull() != null) {
            val widthAndHeight = sizes.toInt()
            Size(widthAndHeight, widthAndHeight)
        } else {
            null
        }

}