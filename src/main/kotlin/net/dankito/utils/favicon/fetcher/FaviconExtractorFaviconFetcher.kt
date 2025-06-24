package net.dankito.utils.favicon.fetcher

import net.dankito.utils.favicon.web.IWebClient

/**
 * Fetches the best matching favicon for an url with Favicon Extractor's
 * favicon service (https://www.faviconextractor.com/favicon/<url>?large=true).
 *
 * Does not support the `preferredSize` parameter, only a `larger=true` query param. But it's
 * not possible to specify desired size directly.
 *
 * For FaviconExtractor it's important to set the User-Agent header.
 *
 * For documentation see: [https://github.com/seadfeng/favicon-downloader](https://github.com/seadfeng/favicon-downloader).
 */
open class FaviconExtractorFaviconFetcher(webClient: IWebClient) : FaviconFetcherBase(webClient) {

    override val supportsPreferredSizeParameter = false


    override fun getFaviconFetcherUrl(url: String, preferredSize: Int?) =
        "https://www.faviconextractor.com/favicon/${removeProtocolAndWww(url)}" +
                if (preferredSize != null && preferredSize > 64) "?larger=true"
                else ""

}