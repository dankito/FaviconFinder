package net.dankito.utils.favicon.fetcher

import net.dankito.utils.favicon.web.IWebClient

class YandexFaviconFetcherTest : FaviconFetcherTestBase() {

    override fun getFaviconFetcher(webClient: IWebClient) = YandexFaviconFetcher(webClient)

}