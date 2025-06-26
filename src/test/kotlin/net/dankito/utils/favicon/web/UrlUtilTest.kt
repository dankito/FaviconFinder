package net.dankito.utils.favicon.web

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test


class UrlUtilTest {

    private val underTest = UrlUtil()


    @Test
    fun `makeLinkAbsolute - Make relative url absolute with baseUrl`() {
        val relativeUrl = "assets/favicons/apple-touch-icon.png"
        val baseUrl = "https://www.codinux.net"

        val result = underTest.makeLinkAbsolute(relativeUrl, baseUrl)

        assertThat(result).isEqualTo("$baseUrl/$relativeUrl")
    }

    @Test
    fun `makeLinkAbsolute - Make relative url absolute with baseUrl that ends with a slash`() {
        val relativeUrl = "assets/favicons/apple-touch-icon.png"
        val baseUrl = "https://www.codinux.net/"

        val result = underTest.makeLinkAbsolute(relativeUrl, baseUrl)

        assertThat(result).isEqualTo("$baseUrl$relativeUrl")
    }

}