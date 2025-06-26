package net.dankito.utils.favicon.fetcher

import net.codinux.log.logger
import net.dankito.utils.favicon.web.UrlUtil
import net.dankito.web.client.WebClient
import net.dankito.web.client.get

abstract class FaviconFetcherBase(protected val webClient: WebClient, protected val urlUtil: UrlUtil = UrlUtil.Default) : FaviconFetcher {

    protected abstract fun getFaviconFetcherUrl(url: String, preferredSize: Int?): String


    protected val log by logger()


    override suspend fun fetch(url: String, preferredSize: Int?): ByteArray? = try {
        val fetcherUrl = getFaviconFetcherUrl(url, preferredSize)
        val response = webClient.get<ByteArray>(fetcherUrl)

        if (response.successful) {
            response.body
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