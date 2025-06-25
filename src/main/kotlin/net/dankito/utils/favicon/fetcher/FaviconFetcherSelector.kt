package net.dankito.utils.favicon.fetcher

import net.dankito.utils.favicon.web.IWebClient
import net.dankito.utils.favicon.web.UrlConnectionWebClient

open class FaviconFetcherSelector(
    protected val fetchers: Collection<FaviconFetcher> = createDefaultFetchers(UrlConnectionWebClient.Default)
) {

    companion object {
        fun createDefaultFetchers(webClient: IWebClient): List<FaviconFetcher> = listOf(
            GoogleFaviconFetcher(webClient),
            DuckDuckGoFaviconFetcher(webClient),
//            IconHorseFaviconFetcher(webClient), // returns often 503
            FaviconExtractorFaviconFetcher(webClient),
            TwentyIconsFaviconFetcher(webClient),
            FaviconeFaviconFetcher(webClient),
            FaviconIsFaviconFetcher(webClient),
            YandexFaviconFetcher(webClient),
        )
    }


    constructor(webClient: IWebClient) : this(createDefaultFetchers(webClient))


    fun firstMatching(url: String, preferredSize: Int? = null): ByteArray? {
        val fetchersToQuery = if (preferredSize == null) fetchers else fetchers.filter { it.supportsPreferredSizeParameter }

        return fetchersToQuery.firstNotNullOfOrNull { it.fetch(url, preferredSize) }
    }

}