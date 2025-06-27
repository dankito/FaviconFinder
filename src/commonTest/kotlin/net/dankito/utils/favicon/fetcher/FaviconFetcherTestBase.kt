package net.dankito.utils.favicon.fetcher

import assertk.assertThat
import assertk.assertions.isGreaterThan
import assertk.assertions.isNotNull
import kotlinx.coroutines.test.runTest
import net.dankito.web.client.KtorWebClient
import net.dankito.web.client.WebClient
import kotlin.test.Test

abstract class FaviconFetcherTestBase {

    protected abstract fun getFaviconFetcher(webClient: WebClient): FaviconFetcher

    protected val underTest = getFaviconFetcher(KtorWebClient())


    @Test
    fun heise_de_32x32() = runTest {
        val result = underTest.fetch("heise.de", 32)

        assertThat(result).isNotNull()
        assertThat(result!!::size).isGreaterThan(500)
    }

    @Test
    fun https_heise_de_32x32() = runTest {
        val result = underTest.fetch("https://heise.de", 32)

        assertThat(result).isNotNull()
        assertThat(result!!::size).isGreaterThan(500)
    }

}