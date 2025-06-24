package net.dankito.utils.favicon.fetcher

import net.dankito.utils.favicon.web.IWebClient

class FaviconeFaviconFetcherTest : FaviconFetcherTestBase() {

    override fun getFaviconFetcher(webClient: IWebClient) = FaviconeFaviconFetcher(webClient)

}