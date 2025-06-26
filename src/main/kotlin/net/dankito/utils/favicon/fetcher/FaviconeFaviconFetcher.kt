package net.dankito.utils.favicon.fetcher

import net.dankito.web.client.WebClient

/**
 * Fetches the best matching favicon for an url with Favicone's
 * favicon service (https://favicone.com/<url>?s=<optional size>).
 *
 * Support the `preferredSize` parameter but max supported value is `256`.
 *
 * For documentation see: [https://favicone.com/](https://favicone.com/).
 */
open class FaviconeFaviconFetcher(webClient: WebClient) : FaviconFetcherBase(webClient) {

    companion object {
        const val MaxSize = 256
    }

    override val supportsPreferredSizeParameter = true


    override fun getFaviconFetcherUrl(url: String, preferredSize: Int?) =
        "https://favicone.com/${removeProtocolAndWww(url)}" +
                if (preferredSize == null) ""
                else "?s${mapToSupportedSize(preferredSize)}"

    protected open fun mapToSupportedSize(preferredSize: Int): Int =
        if (preferredSize > MaxSize) {
            MaxSize
        } else {
            preferredSize
        }

}