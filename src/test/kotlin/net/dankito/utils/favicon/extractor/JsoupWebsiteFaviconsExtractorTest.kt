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
        val imageUrl = "https://codinux.net/images/favicons/mstile-${size}x$size.png"

        val html = """<html>
            <head>
                <meta name="msapplication-TileImage" content="$imageUrl">
            </head>
        </html>""".trimMargin()


        val result = underTest.extractFavicons("", html)

        assertIcon(result, imageUrl, FaviconType.MsTileImage, null, size)
    }


    private fun assertIcon(result: List<Favicon>, imageUrl: String, iconType: FaviconType, mimeType: String? = null, size: Int? = null) {
        assertThat(result).hasSize(1)

        val favicon = result.first()
        assertThat(favicon.url).isEqualTo(imageUrl)
        assertThat(favicon.iconType).isEqualTo(iconType)
        assertThat(favicon.imageMimeType).isEqualTo(mimeType)

        if (size == null) {
            assertThat(favicon.size).isNull()
        } else {
            assertThat(favicon.size).isNotNull()
            assertThat(favicon.size!!.width).isEqualTo(size)
            assertThat(favicon.size!!.height).isEqualTo(size)
        }
    }

}