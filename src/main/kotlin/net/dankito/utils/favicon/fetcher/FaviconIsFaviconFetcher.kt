package net.dankito.utils.favicon.fetcher

import net.dankito.web.client.WebClient

/**
 * Fetches the best matching favicon for an url with Favicon.is'
 * favicon service (`https://favicon.is/<url>?larger=true`).
 *
 * Does not support the `preferredSize` parameter, only a `larger=true` query param. But it's
 * not possible to specify desired size directly.
 */
open class FaviconIsFaviconFetcher(webClient: WebClient) : FaviconFetcherBase(webClient) {

    override val supportsPreferredSizeParameter = false


    override fun getFaviconFetcherUrl(url: String, preferredSize: Int?) =
        "https://favicon.is/${removeProtocolAndWww(url)}" +
                if (preferredSize != null && preferredSize > 64) "?larger=true"
                else ""

}