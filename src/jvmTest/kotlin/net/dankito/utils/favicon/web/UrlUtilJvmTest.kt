package net.dankito.utils.favicon.web

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test


class UrlUtilJvmTest {

    private val underTest = UrlUtilJvm()


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


    @Test
    fun `makeLinkAbsolute - Path does not end with a slash - Last path segment gets removed`() {
        val relativeUrl = "android-chrome-192x192.png"
        val baseUrl = "https://codinux.net/images/favicons/site.webmanifest"

        val result = underTest.makeLinkAbsolute(relativeUrl, baseUrl)

        assertThat(result).isEqualTo("${baseUrl.substringBeforeLast('/')}/$relativeUrl")
    }

    @Test
    fun `makeLinkAbsolute - Path ends with slash - Last path segment is retained`() {
        val relativeUrl = "android-chrome-192x192.png"
        val baseUrl = "https://codinux.net/images/favicons/"

        val result = underTest.makeLinkAbsolute(relativeUrl, baseUrl)

        assertThat(result).isEqualTo("$baseUrl$relativeUrl")
    }


    /*              Current directory relative              */

    @Test
    fun `makeLinkAbsolute - Path does not end with a slash - relative URL starts without dot or slash`() {
        val relativeUrl = "android-chrome-192x192.png"
        val baseUrl = "https://codinux.net/images/favicons/site.webmanifest"

        val result = underTest.makeLinkAbsolute(relativeUrl, baseUrl)

        assertThat(result).isEqualTo("${baseUrl.substringBeforeLast('/')}/$relativeUrl")
    }

    @Test
    fun `makeLinkAbsolute - Path does not end with a slash - relative URL starts with dot and slash`() {
        val relativeUrl = "./android-chrome-192x192.png"
        val baseUrl = "https://codinux.net/images/favicons/site.webmanifest"

        val result = underTest.makeLinkAbsolute(relativeUrl, baseUrl)

        assertThat(result).isEqualTo("${baseUrl.substringBeforeLast('/')}/${relativeUrl.substringAfter('/')}")
    }

    @Test
    fun `makeLinkAbsolute - Path ends with slash - relative URL starts without dot or slash`() {
        val relativeUrl = "android-chrome-192x192.png"
        val baseUrl = "https://codinux.net/images/favicons/"

        val result = underTest.makeLinkAbsolute(relativeUrl, baseUrl)

        assertThat(result).isEqualTo("${baseUrl.substringBeforeLast('/')}/$relativeUrl")
    }

    @Test
    fun `makeLinkAbsolute - Path ends with slash - relative URL starts with dot and slash`() {
        val relativeUrl = "./android-chrome-192x192.png"
        val baseUrl = "https://codinux.net/images/favicons/"

        val result = underTest.makeLinkAbsolute(relativeUrl, baseUrl)

        assertThat(result).isEqualTo("${baseUrl.substringBeforeLast('/')}/${relativeUrl.substringAfter('/')}")
    }


    /*              Parent-directory relative               */

    @Test
    fun `makeLinkAbsolute - Path does not end with a slash - relative URL starts with dot dot slash`() {
        val relativeUrl = "../android-chrome-192x192.png"
        val baseUrl = "https://codinux.net/images/favicons/site.webmanifest"

        val result = underTest.makeLinkAbsolute(relativeUrl, baseUrl)

        assertThat(result).isEqualTo("https://codinux.net/images/${relativeUrl.substringAfter('/')}")
    }

    @Test
    fun `makeLinkAbsolute - Path does not end with a slash - relative URL starts with two dot dot slash`() {
        val relativeUrl = "../../android-chrome-192x192.png"
        val baseUrl = "https://codinux.net/images/favicons/site.webmanifest"

        val result = underTest.makeLinkAbsolute(relativeUrl, baseUrl)

        assertThat(result).isEqualTo("https://codinux.net/${relativeUrl.substringAfterLast('/')}")
    }

    @Test
    fun `makeLinkAbsolute - Path does not end with a slash - relative URL starts with more dot dot slash than path segments`() {
        val relativeUrl = "../../../android-chrome-192x192.png"
        val baseUrl = "https://codinux.net/images/favicons/site.webmanifest"

        val result = underTest.makeLinkAbsolute(relativeUrl, baseUrl)

        assertThat(result).isEqualTo("https://codinux.net/${relativeUrl.substringAfterLast('/')}")
    }

    @Test
    fun `makeLinkAbsolute - Path ends with slash - relative URL starts with dot dot slash`() {
        val relativeUrl = "../android-chrome-192x192.png"
        val baseUrl = "https://codinux.net/images/favicons/"

        val result = underTest.makeLinkAbsolute(relativeUrl, baseUrl)

        assertThat(result).isEqualTo("https://codinux.net/images/${relativeUrl.substringAfter('/')}")
    }

    @Test
    fun `makeLinkAbsolute - Path ends with slash - relative URL starts with two dot dot slash`() {
        val relativeUrl = "../../android-chrome-192x192.png"
        val baseUrl = "https://codinux.net/images/favicons/"

        val result = underTest.makeLinkAbsolute(relativeUrl, baseUrl)

        assertThat(result).isEqualTo("https://codinux.net/${relativeUrl.substringAfterLast('/')}")
    }

    @Test
    fun `makeLinkAbsolute - Path ends with slash - relative URL starts with more dot dot slash than path segments`() {
        val relativeUrl = "../../../android-chrome-192x192.png"
        val baseUrl = "https://codinux.net/images/favicons/"

        val result = underTest.makeLinkAbsolute(relativeUrl, baseUrl)

        assertThat(result).isEqualTo("https://codinux.net/${relativeUrl.substringAfterLast('/')}")
    }


    /*              Root relative               */

    @Test
    fun `makeLinkAbsolute - Path does not end with a slash - relative URL starts with slash`() {
        val relativeUrl = "/android-chrome-192x192.png"
        val baseUrl = "https://codinux.net/images/favicons/site.webmanifest"

        val result = underTest.makeLinkAbsolute(relativeUrl, baseUrl)

        assertThat(result).isEqualTo("https://codinux.net$relativeUrl")
    }

    @Test
    fun `makeLinkAbsolute - Path ends with slash - relative URL starts with slash`() {
        val relativeUrl = "/android-chrome-192x192.png"
        val baseUrl = "https://codinux.net/images/favicons/"

        val result = underTest.makeLinkAbsolute(relativeUrl, baseUrl)

        assertThat(result).isEqualTo("https://codinux.net$relativeUrl")
    }

}