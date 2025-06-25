package net.dankito.utils.favicon.finder

import assertk.assertThat
import assertk.assertions.isGreaterThanOrEqualTo
import assertk.assertions.isNotEmpty
import kotlin.test.Test

class FaviconExtractorFaviconFinderTest {

    private val underTest = FaviconExtractorFaviconFinder()


    @Test
    fun findFavicons() {
        val result = underTest.findFavicons("https://www.heise.de")

        assertThat(result).isNotEmpty()
        assertThat(result.size).isGreaterThanOrEqualTo(4)
    }

}