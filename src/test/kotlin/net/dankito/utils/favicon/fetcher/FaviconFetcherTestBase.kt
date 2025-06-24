package net.dankito.utils.favicon.fetcher

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isNotNull
import net.dankito.utils.favicon.web.IWebClient
import net.dankito.utils.favicon.web.UrlConnectionWebClient
import kotlin.test.Test

abstract class FaviconFetcherTestBase {

    protected abstract fun getFaviconFetcher(webClient: IWebClient): FaviconFetcher

    protected val underTest = getFaviconFetcher(UrlConnectionWebClient())


    @Test
    fun heise_de_32x32() {
        val result = underTest.fetch("heise.de", 32)

        assertThat(result).isNotNull()
        if (underTest.supportsPreferredSizeParameter) {
            assertThat(result!!).hasSize(725)
        } else {
            assertThat(result!!).hasSize(540)
        }
    }

    @Test
    fun https_heise_de_32x32() {
        val result = underTest.fetch("https://heise.de", 32)

        assertThat(result).isNotNull()
        if (underTest.supportsPreferredSizeParameter) {
            assertThat(result!!).hasSize(725)
        } else {
            assertThat(result!!).hasSize(540)
        }
    }

}