package net.dankito.utils.favicon.fetcher

import net.dankito.web.client.WebClient

class FaviconeFaviconFetcherTest : FaviconFetcherTestBase() {

    override fun getFaviconFetcher(webClient: WebClient) = FaviconeFaviconFetcher(webClient)

}