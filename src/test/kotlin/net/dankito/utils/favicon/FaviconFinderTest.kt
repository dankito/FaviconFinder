package net.dankito.utils.favicon

import net.dankito.utils.favicon.web.UrlConnectionWebClient
import org.assertj.core.api.Assertions.assertThat
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.jupiter.api.Test


class FaviconFinderTest {

    companion object {
        const val TestSiteUrl = "https://www.test.com"
    }


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
        val extractedIcons = getFaviconsForUrl("https://www.zeit.de/")


        testExtractedFavicons(extractedIcons, 2)
    }

    @Test
    fun extractHeiseFavicons() {
        val extractedIcons = getFaviconsForUrl("https://www.heise.de")


        testExtractedFavicons(extractedIcons, 7)
    }

    @Test
    fun extractDerPostillonFavicons() {
        val extractedIcons = getFaviconsForUrl("https://www.der-postillon.com")


        testExtractedFavicons(extractedIcons, 2)
    }


    @Test
    fun spardaBank() {
        val extractedIcons = getFaviconsForUrl("https://www.sparda-b.de")

        testExtractedFavicons(extractedIcons, 1)
    }

    @Test
    fun psdBankMuenchen_QueryHasBeenRemoved() {
        val extractedIcons = getFaviconsForUrl("https://www.psd-muenchen.de")

        testExtractedFavicons(extractedIcons, 6)

        extractedIcons.forEach { favicon ->
            assertThat(favicon.url.contains('?')).isFalse() // check if all queries have been removed from url
        }
    }

    @Test
    fun deutscheBank() {
        val extractedIcons = getFaviconsForUrl("https://www.deutsche-bank.de")

        testExtractedFavicons(extractedIcons, 2)
    }


    @Test
    fun removeQuery() {
        val relativeUrlWithoutQuery = "/favicon_196px.png"
        val relativeUrlWithQuery = "$relativeUrlWithoutQuery?v=1603434540317"

        val extractedIcons = getFaviconsForHtml("<link rel=\"shortcut icon\" href=\"$relativeUrlWithQuery\">")

        testExtractedFavicons(extractedIcons, 1)

        assertThat(extractedIcons.first().url).isEqualTo(TestSiteUrl + relativeUrlWithoutQuery)
    }


    private fun getFaviconsForUrl(url: String): List<Favicon> {
        return underTest.extractFavicons(url)
    }

    private fun getFaviconsForHtml(html: String): List<Favicon> {
        return getFaviconsForDocument(Jsoup.parse(html))
    }

    private fun getFaviconsForDocument(document: Document): List<Favicon> {
        return underTest.extractFavicons(document, TestSiteUrl)
    }


    private fun testExtractedFavicons(extractedIcons: List<Favicon>, countMinimumFavicons: Int) {
        assertThat(extractedIcons.size).isGreaterThanOrEqualTo(countMinimumFavicons)

        for (favicon in extractedIcons) {
            assertThat(favicon.url).isNotNull()
            assertThat(favicon.url).startsWith("http")
        }
    }

}