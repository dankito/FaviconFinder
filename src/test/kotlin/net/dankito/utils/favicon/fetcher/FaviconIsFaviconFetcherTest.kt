package net.dankito.utils.favicon.fetcher

import net.dankito.utils.favicon.web.IWebClient

class FaviconIsFaviconFetcherTest : FaviconFetcherTestBase() {

    override fun getFaviconFetcher(webClient: IWebClient) = FaviconIsFaviconFetcher(webClient)

}