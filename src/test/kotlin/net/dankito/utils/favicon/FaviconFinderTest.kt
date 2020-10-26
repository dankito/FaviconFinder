package net.dankito.utils.favicon

import net.dankito.utils.favicon.web.UrlConnectionWebClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class FaviconFinderTest {

    private val underTest : FaviconFinder = FaviconFinder(UrlConnectionWebClient())


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


    @Test
    fun spardaBank() {
        val extractedIcons = getFaviconsForUrl("https://www.sparda-b.de")

        testExtractedFavicons(extractedIcons, 1)
    }

    @Test
    fun deutscheBank() {
        val extractedIcons = getFaviconsForUrl("https://www.deutsche-bank.de/pfb/content/blz-finden.html?c=6923861946&t=query&bank=...")

        testExtractedFavicons(extractedIcons, 2)
    }


    private fun getFaviconsForUrl(url: String): List<Favicon> {
        return underTest.extractFavicons(url)
    }

    private fun testExtractedFavicons(extractedIcons: List<Favicon>, countMinimumFavicons: Int) {
        assertThat(extractedIcons.size).isGreaterThanOrEqualTo(countMinimumFavicons)

        for (favicon in extractedIcons) {
            assertThat(favicon.url).isNotNull()
            assertThat(favicon.url).startsWith("http")
        }
    }

}