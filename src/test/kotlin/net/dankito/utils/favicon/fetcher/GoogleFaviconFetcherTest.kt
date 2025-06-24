package net.dankito.utils.favicon.fetcher

import net.dankito.utils.favicon.web.IWebClient

class GoogleFaviconFetcherTest : FaviconFetcherTestBase() {

    override fun getFaviconFetcher(webClient: IWebClient) = GoogleFaviconFetcher(webClient)

}