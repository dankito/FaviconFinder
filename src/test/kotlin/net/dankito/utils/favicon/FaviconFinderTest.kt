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


    @Test
    fun extractIconSizeFromSizesAttribute() {

        var html = "<html><head>"

        html += "<link data-rh=\"true\" rel=\"apple-touch-icon-precomposed\" sizes=\"144×144\" href=\"/vi-assets/static-assets/ios-ipad-144x144-28865b72953380a40aa43318108876cb.png\">" // nytimes.com
        html += "<link rel=\"apple-touch-icon\" sizes=\"152x152\" href=\"https://assets.guim.co.uk/images/favicons/fee5e2d638d1c35f6d501fa397e53329/152x152.png\">" // theguardian.com
        html += "<link rel=\"shortcut icon\" sizes=\"16x16 32x32\" href=\"https://www.zeit.de/favicon.ico\">" // zeit.de
        html += "<link rel=\"icon\" type=\"image/png\" sizes=\"16x16\" href=\"/scripts/favicon/favicon-16x16.png?v=ng98AGkzJy\">" // heise.de
        html += "<link href=\"/icons/ho/touch-icons/apple-touch-icon-180x180.png\" rel=\"apple-touch-icon\" sizes=\"180x180\">" // heise.de
        html += "<link rel=\"icon\" type=\"image/png\" sizes=\"192x192\" href=\"/scripts/favicon/android-chrome-192x192.png?v=ng98AGkzJy\">" // psd-muenchen.de
        html += "<link rel=\"shortcut icon\" sizes=\"16X16\" href=\"$TestSiteUrl/favicon.ico\">" // self created to test 'X' as sizes separator but has also already been seen in the wild

        html += "</head><body></body></html"

        val extractedIcons = getFaviconsForHtml(html)

        testExtractedFavicons(extractedIcons, 1)

        extractedIcons.forEach { favicon ->
            assertThat(favicon.size).isNotNull
        }
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