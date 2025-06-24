package net.dankito.utils.favicon.fetcher

import net.dankito.utils.favicon.web.IWebClient
import net.dankito.utils.favicon.web.UrlUtil

abstract class FaviconFetcherBase(protected val webClient: IWebClient, protected val urlUtil: UrlUtil = UrlUtil()) : FaviconFetcher {

    protected abstract fun getFaviconFetcherUrl(url: String, preferredSize: Int?): String


    override fun fetch(url: String, preferredSize: Int?): ByteArray? {
        val fetcherUrl = getFaviconFetcherUrl(url, preferredSize)
        val response = webClient.get(fetcherUrl, true) // actually only required for FaviconExtractor to set the User-Agent (requestDesktopWebsite=true)

        return if (response.successful) {
            response.receivedData
        } else {
            null
        }
    }

    protected open fun ensureStartsWithHttpOrHttps(url: String): String =
        urlUtil.ensureStartsWithHttpOrHttps(url)

    protected open fun removeProtocolAndWww(url: String): String =
        urlUtil.removeProtocolAndWww(url)

}