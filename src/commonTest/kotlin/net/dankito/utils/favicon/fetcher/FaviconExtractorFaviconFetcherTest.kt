package net.dankito.utils.favicon.fetcher

import net.dankito.web.client.WebClient

class FaviconExtractorFaviconFetcherTest : FaviconFetcherTestBase() {

    override fun getFaviconFetcher(webClient: WebClient) = FaviconExtractorFaviconFetcher(webClient)

}