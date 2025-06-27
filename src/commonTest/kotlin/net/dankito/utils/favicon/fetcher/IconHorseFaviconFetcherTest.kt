package net.dankito.utils.favicon.fetcher

import net.dankito.web.client.WebClient
import kotlin.test.Ignore

@Ignore
class IconHorseFaviconFetcherTest : FaviconFetcherTestBase() {

    override fun getFaviconFetcher(webClient: WebClient) = IconHorseFaviconFetcher(webClient)

}