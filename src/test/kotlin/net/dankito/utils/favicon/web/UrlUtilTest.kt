package net.dankito.utils.favicon.web

import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test


class UrlUtilTest {

    private val underTest = UrlUtil()


    @Test
    fun `makeLinkAbsolute - Make relative url absolute with siteUrl`() {
        val relativeUrl = "assets/favicons/apple-touch-icon.png"
        val siteUrl = "https://www.codinux.net"

        val result = underTest.makeLinkAbsolute(relativeUrl, siteUrl)

        assertThat(result).isEqualTo("$siteUrl/$relativeUrl")
    }

    @Test
    fun `makeLinkAbsolute - Make relative url absolute with siteUrl that ends with a slash`() {
        val relativeUrl = "assets/favicons/apple-touch-icon.png"
        val siteUrl = "https://www.codinux.net/"

        val result = underTest.makeLinkAbsolute(relativeUrl, siteUrl)

        assertThat(result).isEqualTo("$siteUrl$relativeUrl")
    }

}