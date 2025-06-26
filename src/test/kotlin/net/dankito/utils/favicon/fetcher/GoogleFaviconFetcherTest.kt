package net.dankito.utils.favicon.fetcher

import net.dankito.web.client.WebClient

class GoogleFaviconFetcherTest : FaviconFetcherTestBase() {

    override fun getFaviconFetcher(webClient: WebClient) = GoogleFaviconFetcher(webClient)

}