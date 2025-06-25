package net.dankito.utils.favicon.extractor

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import net.dankito.utils.favicon.Favicon
import net.dankito.utils.favicon.FaviconType
import kotlin.test.Test

class JsoupWebsiteFaviconsExtractorTest {

    private val underTest = JsoupWebsiteFaviconsExtractor()


    @Test
    fun extractOpenGraphImage() {
        val imageUrl = "https://codinux.net/images/favicons/favicon-96x96.png"
        val mimeType = "image/png"
        val size = 200

        val html = """<html>
            <head>
                <meta name="image" property="og:image" content="$imageUrl">
                <meta property="og:image:type" content="$mimeType">
                <meta property="og:image:width" content="$size">
                <meta property="og:image:height" content="$size">
            </head>
        </html>""".trimMargin()


        val result = underTest.extractFavicons("", html)

        assertIcon(result, imageUrl, FaviconType.OpenGraphImage, mimeType, size)
    }

    @Test
    fun extractMsTileImage() {
        val size = 144
        val imageUrl = "https://codinux.net/images/favicons/mstile-${sizeString(size)}.png"
        val color = "#FBBB21"

        val html = """<html>
            <head>
                <meta name="msapplication-TileImage" content="$imageUrl">
                <meta name="msapplication-TileColor" content="$color">
            </head>
        </html>""".trimMargin()


        val result = underTest.extractFavicons("", html)

        assertIcon(result, imageUrl, FaviconType.MsTileImage, null, size, color)
    }


    @Test
    fun link_shortcutIcon() {
        val imageUrl = "https://codinux.net/favicon.ico"
        val mimeType = "image/x-icon"

        val html = """<html>
            <head>
                <link rel="shortcut icon" href="$imageUrl" type="$mimeType">
            </head>
        </html>""".trimMargin()


        val result = underTest.extractFavicons("", html)

        assertIcon(result, imageUrl, FaviconType.ShortcutIcon, mimeType)
    }

    @Test
    fun link_icon_typeSet() {
        val size = 96
        val mimeType = "image/png"
        val imageUrl = "https://codinux.net/images/favicons/favicon-${sizeString(size)}.png"

        val html = """<html>
            <head>
                <link rel="icon" type="$mimeType" sizes="${sizeString(size)}" href="$imageUrl">
            </head>
        </html>""".trimMargin()


        val result = underTest.extractFavicons("", html)

        assertIcon(result, imageUrl, FaviconType.Icon, mimeType, size)
    }

    @Test
    fun link_icon_typeNotSet() {
        val size = 48
        val imageUrl = "https://codinux.net/images/favicons/favicon.ico"

        val html = """<html>
            <head>
                <link rel="icon" sizes="${sizeString(size)}" href="$imageUrl">
            </head>
        </html>""".trimMargin()


        val result = underTest.extractFavicons("", html)

        assertIcon(result, imageUrl, FaviconType.Icon, null, size)
    }

    @Test
    fun link_appleTouchIcon() {
        val size = 32
        val imageUrl = "https://codinux.net/images/favicons/apple-touch-icon-${sizeString(size)}.png"

        val html = """<html>
            <head>
                <link rel="apple-touch-icon" sizes="${sizeString(size)}" href="$imageUrl">
            </head>
        </html>""".trimMargin()


        val result = underTest.extractFavicons("", html)

        assertIcon(result, imageUrl, FaviconType.AppleTouch, null, size)
    }

    @Test
    fun link_safariMaskIcon() {
        val imageUrl = "https://codinux.net/images/favicons/safari-pinned-tab.svg"
        val color = "#FBBB21"

        val html = """<html>
            <head>
                <link rel="mask-icon" color="$color" href="$imageUrl">
            </head>
        </html>""".trimMargin()


        val result = underTest.extractFavicons("", html)

        assertIcon(result, imageUrl, FaviconType.SafariMaskIcon, color = color)
    }

    @Test
    fun link_webManifest() {
        // TODO: mock retrieving WebManifest
        val html = """<html>
            <head>
                <link rel="manifest" href="https://codinux.net/images/favicons/site.webmanifest">
            </head>
        </html>""".trimMargin()


        val result = underTest.extractFavicons("", html)

        assertThat(result).hasSize(8)
    }


    private fun assertIcon(result: List<Favicon>, imageUrl: String, iconType: FaviconType, mimeType: String? = null, size: Int? = null, color: String? = null) {
        assertThat(result).hasSize(1)

        val favicon = result.first()
        assertThat(favicon::url).isEqualTo(imageUrl)
        assertThat(favicon::iconType).isEqualTo(iconType)
        assertThat(favicon::imageMimeType).isEqualTo(mimeType)

        if (size == null) {
            assertThat(favicon::size).isNull()
        } else {
            assertThat(favicon::size).isNotNull()
            assertThat(favicon.size!!::width).isEqualTo(size)
            assertThat(favicon.size!!::height).isEqualTo(size)
        }

        if (color != null) {
            assertThat(favicon::color).isEqualTo(color)
        }
    }

    private fun sizeString(size: Int): String =
        "${size}x$size"

}