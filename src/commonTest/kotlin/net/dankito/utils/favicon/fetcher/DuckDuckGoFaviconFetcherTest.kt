package net.dankito.utils.favicon.fetcher

import net.dankito.web.client.WebClient

class DuckDuckGoFaviconFetcherTest : FaviconFetcherTestBase() {

    override fun getFaviconFetcher(webClient: WebClient) = DuckDuckGoFaviconFetcher(webClient)

}