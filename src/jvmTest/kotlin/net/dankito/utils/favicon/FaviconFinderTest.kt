package net.dankito.utils.favicon

import assertk.assertThat
import assertk.assertions.*
import kotlinx.coroutines.test.runTest
import net.dankito.web.client.ClientConfig
import net.dankito.web.client.KtorWebClient
import kotlin.test.Test


class FaviconFinderTest {

    companion object {
        const val TestSiteUrl = "https://www.test.com"
    }


    private val underTest : FaviconFinder = FaviconFinder(KtorWebClient(ClientConfig(requestTimeoutMillis = 5_000)))


    @Test
    fun extractWikipediaFavicons() = runTest {
        val extractedIcons = getFaviconsForUrl("https://www.wikipedia.org/")


        testExtractedFavicons(extractedIcons, 3)
    }

    @Test
    fun extractTheGuardianFavicons() = runTest {
        val extractedIcons = getFaviconsForUrl("https://www.theguardian.com")


        testExtractedFavicons(extractedIcons, 7)
    }

    @Test
    fun extractNewYorkTimesFavicons() = runTest {
        val extractedIcons = getFaviconsForUrl("https://www.nytimes.com/")


        testExtractedFavicons(extractedIcons, 7)
    }

    @Test
    fun extractZeitFavicons() = runTest {
        val extractedIcons = getFaviconsForUrl("https://www.zeit.de/")


        testExtractedFavicons(extractedIcons, 2)
    }

    @Test
    fun extractHeiseFavicons() = runTest {
        val extractedIcons = getFaviconsForUrl("https://www.heise.de")


        testExtractedFavicons(extractedIcons, 6)
    }

    @Test
    fun extractDerPostillonFavicons() = runTest {
        val extractedIcons = getFaviconsForUrl("https://www.der-postillon.com")


        testExtractedFavicons(extractedIcons, 2)
    }


    @Test
    fun spardaBank() = runTest {
        val extractedIcons = getFaviconsForUrl("https://www.sparda-b.de")

        testExtractedFavicons(extractedIcons, 1)
    }

    @Test
    fun deutscheBank() = runTest {
        val extractedIcons = getFaviconsForUrl("https://www.deutsche-bank.de")

        testExtractedFavicons(extractedIcons, 2)
    }


    @Test
    fun `Relative url`() = runTest {
        val extractedIcons = getFaviconsForUrl("codinux.net")

        testExtractedFavicons(extractedIcons, 5)

        extractedIcons.forEach { favicon ->
            assertThat(favicon::url).startsWith("https://codinux.net/")
        }
    }

    @Test
    fun `Relative url with www`() = runTest {
        val extractedIcons = getFaviconsForUrl("www.codinux.net")

        testExtractedFavicons(extractedIcons, 5)

        extractedIcons.forEach { favicon ->
            assertThat(favicon::url).startsWith("https://www.codinux.net/")
        }
    }

    @Test
    fun `Relative url that only succeeds without www`() = runTest {
        val extractedIcons = getFaviconsForUrl("coroot.com")

        testExtractedFavicons(extractedIcons, 2)
    }

    @Test
    fun `Assert slash gets added between host and icon relative url`() = runTest {
        val extractedIcons = getFaviconsForUrl("https://www.codinux.net")

        extractedIcons.forEach { favicon ->
            assertThat(favicon::url).startsWith("https://www.codinux.net/")
        }
    }


    @Test
    fun `Follow redirect location`() = runTest {
        val extractedIcons = getFaviconsForUrl("tagesschau.de") // tagesschau.de returns a redirect to www.tagesschau.de

        testExtractedFavicons(extractedIcons, 19)
    }

    @Test
    fun `Request desktop web site`() = runTest {
        val extractedIcons = getFaviconsForUrl("baidu.com") // by default only default favicon gets returned, but in desktop site there are more

        testExtractedFavicons(extractedIcons, 3)
    }

    @Test
    fun `Non breaking space at start of web manifest gets ignored`() = runTest {
        val extractedIcons = getFaviconsForUrl("kicker.de") // don't know why but when requested with URLConnection then web manifest string starts with ﻿ leading to that Jackson deserialization fails

        testExtractedFavicons(extractedIcons, 16)
    }

    @Test
    fun removeQuery() = runTest {
        val relativeUrlWithoutQuery = "/favicon_196px.png"
        val relativeUrlWithQuery = "$relativeUrlWithoutQuery?v=1603434540317"

        val extractedIcons = getFaviconsForHtml("<link rel=\"shortcut icon\" href=\"$relativeUrlWithQuery\">")

        testExtractedFavicons(extractedIcons, 1)

        assertThat(extractedIcons.first()::url).isEqualTo(TestSiteUrl + relativeUrlWithoutQuery)
    }

    @Test
    fun `Default favicon URL does not return an icon (but e g an error page)`() = runTest {
        val extractedIcons = getFaviconsForUrl("coroot.com")

        extractedIcons.map { it.url }.none { it.endsWith("coroot.com/favicon.ico") }
    }

    @Test
    fun `Check rel value case insensitive`() = runTest {
        val extractedIcons = getFaviconsForUrl("https://www.live.com") // live.com specifies rel value in upper case: <link rel="SHORTCUT ICON" href="/favicon.ico?v2" type="image/x-icon">

        testExtractedFavicons(extractedIcons, 1)
    }


    @Test
    fun `Extract favicons from web manifest`() = runTest {
        val extractedIcons = getFaviconsForUrl("github.com")

        testExtractedFavicons(extractedIcons, 10) // without the icons from web manifest for github only 3 favicons would be returned
    }

    @Test
    fun `Extract favicons with relative URLs from web manifest`() = runTest {
        val extractedIcons = getFaviconsForUrl("spiegel.de")

        testExtractedFavicons(extractedIcons, 12) // without the icons from web manifest for github only 11 favicons would be returned

        extractedIcons.forEach { favicon ->
            assertThat(favicon::url).doesNotContain("./")
        }
    }


    @Test
    fun extractIconSizeFromSizesAttribute() = runTest {

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
            assertThat(favicon::size).isNotNull()
        }
    }


    @Test
    fun extractIconSizeFromUrl() = runTest {

        var html = "<html><head>"

        // TODO: also test these?
        // https://assets.guim.co.uk/images/favicons/023dafadbf5ef53e0865e4baaaa32b3b/windows_tile_144_b.png
        // https://4.bp.blogspot.com/-46xU6sntzl4/UVHLh1NGfwI/AAAAAAAAUlY/RiARs4-toWk/s800/Logo.jpg
        // https://www.sparda-b.de/content/dam/f3132-0/webseite/Grafiken/IOS_Appstore_W1024H1024_abgerundet_transparent.png
        // https://www.test.com/favicon_196px.png

        html += "<link data-rh=\"true\" rel=\"apple-touch-icon-precomposed\" href=\"/vi-assets/static-assets/ios-ipad-144x144-28865b72953380a40aa43318108876cb.png\">" // nytimes.com
        html += "<link rel=\"apple-touch-icon\" href=\"https://assets.guim.co.uk/images/favicons/fee5e2d638d1c35f6d501fa397e53329/152x152.png\">" // theguardian.com
        html += "<link rel=\"shortcut icon\" type=\"image/png\" href=\"https://assets.guim.co.uk/images/favicons/46bd2faa1ab438684a6d4528a655a8bd/32x32.ico\">" // theguardian.com
        html += "<link rel=\"icon\" type=\"image/png\" href=\"/scripts/favicon/favicon-16x16.png?v=ng98AGkzJy\">" // heise.de
        html += "<link href=\"/icons/ho/touch-icons/apple-touch-icon-180x180.png\" rel=\"apple-touch-icon\">" // heise.de
        html += "<link rel=\"icon\" type=\"image/png\" href=\"/scripts/favicon/android-chrome-192x192.png?v=ng98AGkzJy\">" // psd-muenchen.de
        html += "<meta name=\"msapplication-TileImage\" content=\"/scripts/favicon/mstile-144x144.png?v=ng98AGkzJy\">" // psd-muenchen.de

        html += "</head><body></body></html"

        val extractedIcons = getFaviconsForHtml(html)

        testExtractedFavicons(extractedIcons, 7)

        extractedIcons.forEach { favicon ->
            if (favicon.url != TestSiteUrl + "/favicon.ico") { // FaviconFinder adds default icon for passed siteUrl TestSiteUrl
                assertThat(favicon::size).isNotNull()
            }
        }
    }


    private suspend fun getFaviconsForUrl(url: String): List<Favicon> {
        return underTest.extractFavicons(url)
    }

    private suspend fun getFaviconsForHtml(html: String): List<Favicon> {
        return underTest.extractFavicons(TestSiteUrl, html)
    }


    private fun testExtractedFavicons(extractedIcons: List<Favicon>, countMinimumFavicons: Int) {
        assertThat(extractedIcons::size).isGreaterThanOrEqualTo(countMinimumFavicons)

        for (favicon in extractedIcons) {
            assertThat(favicon::url).isNotNull()
            assertThat(favicon::url).startsWith("http")
        }
    }

}