package net.dankito.utils.favicon.fetcher

import net.dankito.utils.favicon.web.IWebClient

class TwentyIconsFaviconFetcherTest : FaviconFetcherTestBase() {

    override fun getFaviconFetcher(webClient: IWebClient) = TwentyIconsFaviconFetcher(webClient)

}