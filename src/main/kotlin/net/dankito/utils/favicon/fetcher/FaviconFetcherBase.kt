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

    protected open fun removeProtocolAndWww(url: String): String {
        var result = url

        val protocolSeparatorIndex = result.indexOf("://")
        if (protocolSeparatorIndex != -1) {
            result = result.substring(protocolSeparatorIndex + "://".length)
        }

        if (result.startsWith("www.", true)) {
            result = result.substring(4)
        }

        return result
    }

}