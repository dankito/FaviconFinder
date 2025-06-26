package net.dankito.utils.favicon.fetcher

import net.dankito.web.client.WebClient

class TwentyIconsFaviconFetcherTest : FaviconFetcherTestBase() {

    override fun getFaviconFetcher(webClient: WebClient) = TwentyIconsFaviconFetcher(webClient)

}