package net.dankito.utils.favicon.fetcher

import net.dankito.utils.favicon.web.IWebClient

class DuckDuckGoFaviconFetcherTest : FaviconFetcherTestBase() {

    override fun getFaviconFetcher(webClient: IWebClient) = DuckDuckGoFaviconFetcher(webClient)

}