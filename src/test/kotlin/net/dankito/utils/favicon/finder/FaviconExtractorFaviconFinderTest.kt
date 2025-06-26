package net.dankito.utils.favicon.finder

import assertk.assertThat
import assertk.assertions.isGreaterThanOrEqualTo
import assertk.assertions.isNotEmpty
import kotlinx.coroutines.test.runTest
import net.dankito.web.client.KtorWebClient
import kotlin.test.Test

class FaviconExtractorFaviconFinderTest {

    private val underTest = FaviconExtractorFaviconFinder(KtorWebClient())


    @Test
    fun findFavicons() = runTest {
        val result = underTest.findFavicons("https://www.heise.de")

        assertThat(result).isNotEmpty()
        assertThat(result::size).isGreaterThanOrEqualTo(4)
    }

}