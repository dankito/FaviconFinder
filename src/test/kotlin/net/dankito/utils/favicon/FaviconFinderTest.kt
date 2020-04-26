package net.dankito.utils.favicon

import net.dankito.utils.web.client.OkHttpWebClient
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assert.assertThat
import org.junit.Test


class FaviconFinderTest {

    private val underTest : FaviconFinder = FaviconFinder(OkHttpWebClient())


    @Test
    fun extractWikipediaFavicons() {
        val extractedIcons = getFaviconsForUrl("https://www.wikipedia.org/")


        testExtractedFavicons(extractedIcons, 3)
    }

    @Test
    fun extractTheGuardianFavicons() {
        val extractedIcons = getFaviconsForUrl("https://www.theguardian.com")


        testExtractedFavicons(extractedIcons, 10)
    }

    @Test
    fun extractNewYorkTimesFavicons() {
        val extractedIcons = getFaviconsForUrl("https://www.nytimes.com/")


        testExtractedFavicons(extractedIcons, 7)
    }

    @Test
    fun extractZeitFavicons() {
        val extractedIcons = getFaviconsForUrl("http://www.zeit.de/")


        testExtractedFavicons(extractedIcons, 3)
    }

    @Test
    fun extractHeiseFavicons() {
        val extractedIcons = getFaviconsForUrl("https://www.heise.de")


        testExtractedFavicons(extractedIcons, 8)
    }

    @Test
    fun extractDerPostillonFavicons() {
        val extractedIcons = getFaviconsForUrl("http://www.der-postillon.com")


        testExtractedFavicons(extractedIcons, 3)
    }


    private fun getFaviconsForUrl(url: String): List<Favicon> {
        return underTest.extractFavicons(url)
    }

    private fun testExtractedFavicons(extractedIcons: List<Favicon>, countIconsToBe: Int) {
        assertThat(extractedIcons.size, `is`(countIconsToBe))

        for (favicon in extractedIcons) {
            assertThat(favicon.url, notNullValue())
            assertThat(favicon.url.startsWith("http"), `is`(true))
        }
    }

}