package net.dankito.utils.favicon.fetcher

import net.dankito.web.client.WebClient

open class FaviconFetcherSelector(
    protected val fetchers: Collection<FaviconFetcher>
) {

    companion object {
        fun createDefaultFetchers(webClient: WebClient): List<FaviconFetcher> = listOf(
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


    constructor(webClient: WebClient) : this(createDefaultFetchers(webClient))


    suspend fun firstMatching(url: String, preferredSize: Int? = null): ByteArray? {
        val fetchersToQuery = if (preferredSize == null) fetchers else fetchers.filter { it.supportsPreferredSizeParameter }

        return fetchersToQuery.firstNotNullOfOrNull { it.fetch(url, preferredSize) }
    }

}