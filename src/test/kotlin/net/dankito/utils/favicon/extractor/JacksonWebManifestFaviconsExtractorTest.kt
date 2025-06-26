package net.dankito.utils.favicon.extractor

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualByComparingTo
import assertk.assertions.isEqualTo
import net.dankito.utils.favicon.Favicon
import net.dankito.utils.favicon.FaviconType
import net.dankito.utils.favicon.Size
import net.dankito.utils.favicon.webmanifest.WebManifest
import net.dankito.utils.favicon.webmanifest.WebManifestIcon
import kotlin.test.Test

class JacksonWebManifestFaviconsExtractorTest {

    companion object {
        private const val ManifestUrl = "https://codinux.net/images/favicons/site.webmanifest"
        private const val AndroicChromeIconUrl = "https://codinux.net/images/favicons/android-chrome-192x192.png"
        private const val AndroicChromeMaskableIconUrl = "https://codinux.net/images/favicons/android-chrome-maskable-192x192.png"
        private const val IconSize = "192x192"
        private const val IconMimeType = "image/png"
    }


    private val underTest = JacksonWebManifestFaviconsExtractor()


    @Test
    fun purpose_Null_androidChrome() {
        val manifest = WebManifest(listOf(
            WebManifestIcon(AndroicChromeIconUrl, IconSize, IconMimeType, null)
        ))

        val result = underTest.extractIconsFromWebManifest(manifest, ManifestUrl)

        assertIcon(result, AndroicChromeIconUrl, FaviconType.AndroidChrome)
    }

    @Test
    fun purpose_Any_androidChrome() {
        val manifest = WebManifest(listOf(
            WebManifestIcon(AndroicChromeIconUrl, IconSize, IconMimeType, "any")
        ))

        val result = underTest.extractIconsFromWebManifest(manifest, ManifestUrl)

        assertIcon(result, AndroicChromeIconUrl, FaviconType.AndroidChrome)
    }

    @Test
    fun purpose_Maskable_androidChrome() {
        val manifest = WebManifest(listOf(
            WebManifestIcon(AndroicChromeMaskableIconUrl, IconSize, IconMimeType, "maskable")
        ))

        val result = underTest.extractIconsFromWebManifest(manifest, ManifestUrl)

        assertIcon(result, AndroicChromeMaskableIconUrl, FaviconType.AndroidChromeMaskable)
    }


    @Test
    fun relativeIconUrl_GetsResolvedCorrectly() {
        val manifest = WebManifest(listOf(
            WebManifestIcon("./android-chrome-192x192.png", IconSize, IconMimeType)
        ))

        val result = underTest.extractIconsFromWebManifest(manifest, ManifestUrl)

        assertIcon(result, AndroicChromeIconUrl, FaviconType.AndroidChrome)
    }


    private fun assertIcon(result: List<Favicon>, iconUrl: String, type: FaviconType, mimeType: String? = IconMimeType, size: String? = IconSize) {
        assertThat(result).hasSize(1)

        val icon = result.first()
        assertThat(icon.url).isEqualTo(iconUrl)
        assertThat(icon.iconType).isEqualByComparingTo(type)
        assertThat(icon.imageMimeType).isEqualTo(mimeType)

        val sizeObject = size?.let {
            val (width, height) = it.split('x', limit = 2)
            Size(width.toInt(), height.toInt())
        }
        assertThat(icon.size).isEqualTo(sizeObject)
    }

}