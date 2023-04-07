package net.dankito.utils.favicon

import net.dankito.utils.favicon.web.IWebClient
import net.dankito.utils.favicon.web.UrlConnectionWebClient
import net.dankito.utils.favicon.web.UrlUtil
import net.dankito.utils.favicon.web.WebResponse
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import java.net.URL
import kotlin.concurrent.thread


open class FaviconFinder @JvmOverloads constructor(
    protected open val webClient : IWebClient = UrlConnectionWebClient(),
    protected open val urlUtil: UrlUtil = UrlUtil()
) {

    companion object {
        val IconSizeRegex = Regex("\\d{2,4}[xX×]\\d{2,4}")

        private val log = LoggerFactory.getLogger(FaviconFinder::class.java)
    }


    open fun extractFaviconsAsync(url: String, callback: (AsyncResult<List<Favicon>>) -> Unit) {
        thread {
            try {
                callback(AsyncResult(true, result = extractFavicons(url)))
            } catch(e: Exception) {
                log.error("Could not get favicons for $url", e)

                callback(AsyncResult(false, e))
            }
        }
    }

    open fun extractFavicons(url: String) : List<Favicon> {
        webClient.get(url).let { response ->
            if (response.successful) {
                return extractFavicons(response, url)
            }
        }

        return listOf()
    }

    protected open fun extractFavicons(response: WebResponse, url: String): List<Favicon> {
        val document = Jsoup.parse(response.body, url)

        return extractFavicons(document, url)
    }

    open fun extractFavicons(document: Document, url: String): List<Favicon> {
        val extractedFavicons = document.head().select("link, meta").mapNotNull { mapElementToFavicon(it, url) }.toMutableList()

        tryToFindDefaultFavicon(url, extractedFavicons)

        return extractedFavicons
    }

    protected open fun tryToFindDefaultFavicon(url: String, extractedFavicons: MutableList<Favicon>) {
        val urlInstance = URL(url)
        val defaultFaviconUrl = urlInstance.protocol + "://" + urlInstance.host + "/favicon.ico"
        if (containsIconWithUrl(extractedFavicons, defaultFaviconUrl) == false) {
            webClient.head(defaultFaviconUrl).let { response ->
                if (response.successful) {
                    extractedFavicons.add(Favicon(defaultFaviconUrl, FaviconType.ShortcutIcon))
                }
            }
        }
    }

    protected open fun containsIconWithUrl(extractedFavicons: MutableList<Favicon>, faviconUrl: String): Boolean {
        extractedFavicons.forEach { favicon ->
            if (favicon.url == faviconUrl) {
                return true
            }
        }

        return false
    }

    /**
     * Possible formats are documented here https://stackoverflow.com/questions/21991044/how-to-get-high-resolution-website-logo-favicon-for-a-given-url#answer-22007642
     * and here https://en.wikipedia.org/wiki/Favicon
     */
    protected open fun mapElementToFavicon(linkOrMetaElement: Element, siteUrl: String): Favicon? {
        if (linkOrMetaElement.nodeName() == "link") {
            return mapLinkElementToFavicon(linkOrMetaElement, siteUrl)
        }
        else if (linkOrMetaElement.nodeName() == "meta") {
            return mapMetaElementToFavicon(linkOrMetaElement, siteUrl)
        }

        return null
    }

    protected open fun mapLinkElementToFavicon(linkElement: Element, siteUrl: String): Favicon? {
        if (linkElement.hasAttr("rel")) {
            getFaviconTypeForLinkElements(linkElement)?.let { faviconType ->
                val href = linkElement.attr("href")
                val sizes = linkElement.attr("sizes")
                val type = linkElement.attr("type")

                if (href.startsWith("data:;base64") == false) {
                    return createFavicon(href, siteUrl, faviconType, sizes, type)
                }
            }
        }

        return null
    }

    protected open fun getFaviconTypeForLinkElements(linkElement: Element): FaviconType? {
        val relValue = linkElement.attr("rel")

        return when (relValue) {
            "icon" -> FaviconType.Icon
            "apple-touch-icon-precomposed" -> FaviconType.AppleTouchPrecomposed
            "apple-touch-icon" -> FaviconType.AppleTouch
            "shortcut icon" -> FaviconType.ShortcutIcon
            else -> return null
        }
    }

    protected open fun mapMetaElementToFavicon(metaElement: Element, siteUrl: String): Favicon? {
        if (isOpenGraphImageDeclaration(metaElement)) {
            return createFavicon(metaElement.attr("content"), siteUrl, FaviconType.OpenGraphImage, null, null)
        }
        else if (isMsTileMetaElement(metaElement)) {
            return createFavicon(metaElement.attr("content"), siteUrl, FaviconType.MsTileImage, null, null)
        }

        return null
    }

    protected open fun isOpenGraphImageDeclaration(metaElement: Element) = metaElement.hasAttr("property") && metaElement.attr("property") == "og:image" && metaElement.hasAttr("content")

    protected open fun isMsTileMetaElement(metaElement: Element) = metaElement.hasAttr("name") && metaElement.attr("name") == "msapplication-TileImage" && metaElement.hasAttr("content")


    protected open fun createFavicon(url: String?, siteUrl: String, iconType: FaviconType, sizesString: String?, type: String?): Favicon? {
        if (url != null) {
            val urlWithoutQuery = removeQueryFromUrl(url)

            val favicon = Favicon(urlUtil.makeLinkAbsolute(urlWithoutQuery, siteUrl), iconType, type = type)

            if (sizesString.isNullOrBlank() == false) {
                val sizes = extractSizesFromString(sizesString)

                sizes.maxOrNull()?.let { size ->
                    favicon.size = size
                }
            }
            else {
                extractSizeFromUrl(url)?.let { size ->
                    favicon.size = size
                }
            }

            return favicon
        }

        return null
    }

    protected open fun removeQueryFromUrl(url: String): String {
        try {
            val indexOfQuestionMark = url.indexOf('?')
            if (indexOfQuestionMark > 0) {
                return url.substring(0, indexOfQuestionMark)
            }
        } catch (e: Exception) {
            log.error("Could not remove query from url $url", e)
        }

        return url
    }

    protected open fun extractSizesFromString(sizesString: String): List<Size> {
        val sizes = sizesString.split(" ").mapNotNull { sizeString -> mapSizeString(sizeString) }

        return sizes
    }

    protected open fun extractSizeFromUrl(url: String): Size? {
        val matchResult = IconSizeRegex.find(url)

        matchResult?.value?.let { sizeString ->
            return mapSizeString(sizeString)
        }

        return null
    }

    protected open fun mapSizeString(sizeString: String) : Size? {
        var parts = sizeString.split('x')
        if (parts.size != 2) {
            parts = sizeString.split('×') // actually doesn't meet specification, see https://www.w3schools.com/tags/att_link_sizes.asp, but New York Times uses it
        }
        if (parts.size != 2) {
            parts = sizeString.split('X')
        }

        if (parts.size == 2) {
            val width = parts[0].toIntOrNull()
            val height = parts[1].toIntOrNull()

            if (width != null && height != null) {
                return Size(width, height)
            }
        }

        return null
    }

}