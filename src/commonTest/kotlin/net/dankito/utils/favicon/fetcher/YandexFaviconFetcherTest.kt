package net.dankito.utils.favicon.fetcher

import net.dankito.web.client.WebClient

class YandexFaviconFetcherTest : FaviconFetcherTestBase() {

    override fun getFaviconFetcher(webClient: WebClient) = YandexFaviconFetcher(webClient)

}