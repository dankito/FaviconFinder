package net.dankito.utils.favicon.fetcher

import net.codinux.log.logger
import net.dankito.utils.favicon.web.IWebClient
import net.dankito.utils.favicon.web.UrlUtil

abstract class FaviconFetcherBase(protected val webClient: IWebClient, protected val urlUtil: UrlUtil = UrlUtil.Default) : FaviconFetcher {

    protected abstract fun getFaviconFetcherUrl(url: String, preferredSize: Int?): String


    protected val log by logger()


    override fun fetch(url: String, preferredSize: Int?): ByteArray? = try {
        val fetcherUrl = getFaviconFetcherUrl(url, preferredSize)
        val response = webClient.get(fetcherUrl, true) // actually only required for FaviconExtractor to set the User-Agent (requestDesktopWebsite=true)

        if (response.successful) {
            response.receivedData
        } else {
            null
        }
    } catch (e: Throwable) {
        log.error(e) { "Could not extract favicon from url '$url'" }
        null
    }

    protected open fun ensureStartsWithHttpOrHttps(url: String): String =
        urlUtil.ensureStartsWithHttpOrHttps(url)

    protected open fun removeProtocolAndWww(url: String): String =
        urlUtil.removeProtocolAndWww(url)

}