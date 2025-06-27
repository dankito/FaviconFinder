package net.dankito.utils.favicon.fetcher

import net.dankito.web.client.WebClient

class FaviconIsFaviconFetcherTest : FaviconFetcherTestBase() {

    override fun getFaviconFetcher(webClient: WebClient) = FaviconIsFaviconFetcher(webClient)

}