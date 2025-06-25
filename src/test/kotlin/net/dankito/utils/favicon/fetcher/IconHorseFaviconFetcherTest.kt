package net.dankito.utils.favicon.fetcher

import net.dankito.utils.favicon.web.IWebClient
import kotlin.test.Ignore

@Ignore
class IconHorseFaviconFetcherTest : FaviconFetcherTestBase() {

    override fun getFaviconFetcher(webClient: IWebClient) = IconHorseFaviconFetcher(webClient)

}