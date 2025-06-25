package net.dankito.utils.favicon.fetcher

import net.dankito.utils.favicon.web.IWebClient

/**
 * Fetches the best matching favicon for an url with Icon Horse's
 * favicon service (`https://icon.horse/icon/<url>`).
 *
 * Support the `preferredSize` parameter only in paid version if apiKey is specified.
 *
 * For documentation see: [https://icon.horse/usage](https://icon.horse/usage).
 *
 * Warning: Returns often HTTP status 503!
 */
open class IconHorseFaviconFetcher(webClient: IWebClient, protected val apiKey: String? = null) : FaviconFetcherBase(webClient) {

    override val supportsPreferredSizeParameter = apiKey != null


    override fun getFaviconFetcherUrl(url: String, preferredSize: Int?): String {
        var fetcherUrl = "https://icon.horse/icon/${removeProtocolAndWww(url)}"

        if (apiKey != null) {
            fetcherUrl += "?apikey=$apiKey"

            if (preferredSize != null) {
                fetcherUrl += "&size=${mapPreferredSize(preferredSize)}"
            }
        }

        return fetcherUrl
    }

    protected open fun mapPreferredSize(preferredSize: Int): String =
        if (preferredSize <= 64) "small"
        else if (preferredSize <= 192) "medium"
        else "large"

}