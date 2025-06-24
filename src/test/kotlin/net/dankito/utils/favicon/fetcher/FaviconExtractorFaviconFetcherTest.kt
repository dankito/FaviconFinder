package net.dankito.utils.favicon.fetcher

import net.dankito.utils.favicon.web.IWebClient

class FaviconExtractorFaviconFetcherTest : FaviconFetcherTestBase() {

    override fun getFaviconFetcher(webClient: IWebClient) = FaviconExtractorFaviconFetcher(webClient)

}