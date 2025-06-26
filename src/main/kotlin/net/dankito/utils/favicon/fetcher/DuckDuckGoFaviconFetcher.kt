package net.dankito.utils.favicon.fetcher

import net.dankito.web.client.WebClient

/**
 * Fetches the best matching favicon for an url with DuckDuckGo's
 * favicon service (https://icons.duckduckgo.com/ip3/<url>.ico).
 *
 * Does not support the `preferredSize` parameter.
 */
open class DuckDuckGoFaviconFetcher(webClient: WebClient) : FaviconFetcherBase(webClient) {

    override val supportsPreferredSizeParameter = false


    override fun getFaviconFetcherUrl(url: String, preferredSize: Int?) =
        "https://icons.duckduckgo.com/ip3/${removeProtocolAndWww(url)}.ico"

}