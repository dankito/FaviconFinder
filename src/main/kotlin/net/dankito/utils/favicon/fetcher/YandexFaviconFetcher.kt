package net.dankito.utils.favicon.fetcher

import net.dankito.web.client.WebClient

/**
 * Fetches the best matching favicon for an url with Yandex'
 * favicon service (https://favicon.yandex.net/favicon/<url>).
 *
 * Does not support the `preferredSize` parameter and simply ignores its value.
 */
open class YandexFaviconFetcher(webClient: WebClient) : FaviconFetcherBase(webClient) {

    override val supportsPreferredSizeParameter = false


    override fun getFaviconFetcherUrl(url: String, preferredSize: Int?): String =
        // don't know why by when [url] starts with "https://" then returned icon has a large gray background
        "https://favicon.yandex.net/favicon/${removeProtocolAndWww(url)}"

}