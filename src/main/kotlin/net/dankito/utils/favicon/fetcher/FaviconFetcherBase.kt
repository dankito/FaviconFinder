package net.dankito.utils.favicon.fetcher

import net.dankito.utils.favicon.web.IWebClient

abstract class FaviconFetcherBase(protected val webClient: IWebClient) : FaviconFetcher {

    protected abstract fun getFaviconFetcherUrl(url: String, preferredSize: Int?): String


    override fun fetch(url: String, preferredSize: Int?): ByteArray? {
        val fetcherUrl = getFaviconFetcherUrl(url, preferredSize)
        val response = webClient.get(fetcherUrl)

        return if (response.successful) {
            response.receivedData
        } else {
            null
        }
    }

    protected open fun ensureStartsWithHttpOrHttps(url: String): String =
        if (url.startsWith("http://", true) || url.startsWith("https://")) {
            url
        } else {
            "https://$url"
        }

}