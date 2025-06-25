package net.dankito.utils.favicon.fetcher

import assertk.assertThat
import assertk.assertions.isGreaterThan
import assertk.assertions.isNotNull
import net.dankito.utils.favicon.web.IWebClient
import net.dankito.utils.favicon.web.UrlConnectionWebClient
import kotlin.test.Test

abstract class FaviconFetcherTestBase {

    protected abstract fun getFaviconFetcher(webClient: IWebClient): FaviconFetcher

    protected val underTest = getFaviconFetcher(UrlConnectionWebClient.Default)


    @Test
    fun heise_de_32x32() {
        val result = underTest.fetch("heise.de", 32)

        assertThat(result).isNotNull()
        assertThat(result!!.size).isGreaterThan(500)
    }

    @Test
    fun https_heise_de_32x32() {
        val result = underTest.fetch("https://heise.de", 32)

        assertThat(result).isNotNull()
        assertThat(result!!.size).isGreaterThan(500)
    }

}