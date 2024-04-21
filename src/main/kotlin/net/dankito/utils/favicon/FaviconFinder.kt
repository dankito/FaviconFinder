package net.dankito.utils.favicon

import com.fasterxml.jackson.module.kotlin.readValue
import net.dankito.utils.favicon.json.JsonSerializer
import net.dankito.utils.favicon.web.*
import net.dankito.utils.favicon.webmanifest.WebManifest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory
import java.net.URL
import kotlin.concurrent.thread


open class FaviconFinder @JvmOverloads constructor(
    protected open val webClient : IWebClient = UrlConnectionWebClient(),
    protected open val urlUtil: UrlUtil = UrlUtil()
) {

    companion object {
        val IconSizeRegex = Regex("\\d{2,4}[xX×]\\d{2,4}")
    }

    private val log = LoggerFactory.getLogger(FaviconFinder::class.java)


    init {
      AllowAllCertificatsTrustManager.allowAllCertificates() // ignore certificates error
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

    open fun extractFavicons(url: String) : List<Favicon> =
        extractFavicons(url, false) // try relative URLs without "www." first

    protected open fun extractFavicons(url: String, appendWwwDot: Boolean, requestDesktopWebsite: Boolean = false) : List<Favicon> {
        val isRelativeUrl = urlUtil.isRelativeUrl(url)
        val absoluteUrl = urlUtil.makeUrlAbsolute(url, appendWwwDot)

        webClient.get(absoluteUrl, requestDesktopWebsite).let { response ->
            if (response.successful) {
                val favicons = extractFavicons(response, absoluteUrl)
                if (favicons.isNotEmpty() && ((isRelativeUrl == false || appendWwwDot == true) && requestDesktopWebsite == true || // appendWwwDot == true && requestDesktopWebsite == true is the last possible check
                            (favicons.size != 1 || favicons[0].iconType != FaviconType.ShortcutIcon))) { // if only default favicon has been added try one of the options below
                    return favicons
                }

                if (requestDesktopWebsite == false) {
                    return extractFavicons(url, appendWwwDot, true)
                }

                if (appendWwwDot == false && isRelativeUrl) {
                    return extractFavicons(url, true)
                }
            } else if (appendWwwDot == false && isRelativeUrl && url.contains("www.", true) == false) {
                return extractFavicons(url, true) // then, if it does not succeed, append "www."
            }
        }

        return listOf()
    }

    protected open fun extractFavicons(response: WebResponse, url: String): List<Favicon> {
        val document = Jsoup.parse(response.body, url)

        return extractFavicons(document, url)
    }

    open fun extractFavicons(document: Document, url: String): List<Favicon> {
        val linkAndMetaElements = document.head().select("link, meta")

        val extractedFavicons = linkAndMetaElements
            .mapNotNull { mapElementToFavicon(it, url, linkAndMetaElements) }
            .toMutableList()

        extractIconsFromWebManifest(linkAndMetaElements, url).forEach { favicon ->
            if (containsIconWithUrl(extractedFavicons, favicon.url) == false) {
                extractedFavicons.add(favicon)
            }
        }

        tryToFindDefaultFavicon(url, extractedFavicons)?.let { defaultFavicon ->
            extractedFavicons.add(defaultFavicon)
        }

        return extractedFavicons
    }

    protected open fun tryToFindDefaultFavicon(url: String, extractedFavicons: List<Favicon>): Favicon? {
        val urlInstance = URL(url)
        val defaultFaviconUrl = urlInstance.protocol + "://" + urlInstance.host + "/favicon.ico"
        if (containsIconWithUrl(extractedFavicons, defaultFaviconUrl) == false) {
            webClient.head(defaultFaviconUrl).let { response ->
                if (response.successful &&
                    (response.contentType == null || response.contentType?.startsWith("text/") == false)) { // filter out e.g. error pages
                    return Favicon(defaultFaviconUrl, FaviconType.ShortcutIcon)
                }
            }
        }

        return null
    }

    protected open fun extractIconsFromWebManifest(linkAndMetaElements: Elements, siteUrl: String): List<Favicon> {
        linkAndMetaElements.firstOrNull { it.attr("rel") == "manifest" }?.attr("href")?.takeIf { it.isNotBlank() }?.let { manifestUrl ->
            try {
                // don't know why but when requested with URLConnection then web manifest string starts with ﻿ leading to that Jackson deserialization fails
                val manifest = JsonSerializer.default.readValue<WebManifest>(URL(urlUtil.makeLinkAbsolute(manifestUrl, siteUrl)))
                return manifest.icons.mapNotNull {
                    val type = if (it.src.contains("apple-touch", true)) FaviconType.AppleTouch else FaviconType.Icon
                    // some web manifests contain relative icon urls, e.g. spiegel.de:
                    // Manifest URL:
                    //  https://www.spiegel.de/public/spon/json/manifest.json
                    // Icons URLs:
                    // - "./../images/icons/icon-512.png"
                    // - ./../images/icons/icon-192.png -> (https://www.spiegel.de/public/spon/images/icons/icon-192.png)
                    // -> use manifest's url to create absolute favicon url
                    val baseUrl = if (it.src.startsWith(".")) manifestUrl else siteUrl
                    createFaviconFromSizesString(it.src, baseUrl, type, it.type, it.sizes)
                }
            } catch (e: Throwable) {
                log.error("Could not read icons from web manifest of site $siteUrl", e)
            }
        }

        return emptyList()
    }

    protected open fun containsIconWithUrl(extractedFavicons: List<Favicon>, faviconUrl: String): Boolean {
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
    protected open fun mapElementToFavicon(linkOrMetaElement: Element, siteUrl: String, linkAndMetaElements: Elements): Favicon? {
        if (linkOrMetaElement.nodeName() == "link") {
            return mapLinkElementToFavicon(linkOrMetaElement, siteUrl)
        }
        else if (linkOrMetaElement.nodeName() == "meta") {
            return mapMetaElementToFavicon(linkOrMetaElement, siteUrl, linkAndMetaElements)
        }

        return null
    }

    protected open fun mapLinkElementToFavicon(linkElement: Element, siteUrl: String): Favicon? {
        if (linkElement.hasAttr("rel")) {
            getFaviconTypeForLinkElements(linkElement)?.let { faviconType ->
                val href = linkElement.attr("href")
                val sizes = linkElement.attr("sizes")
                val imageMimeType = linkElement.attr("type")

                if (href.startsWith("data:;base64") == false) {
                    return createFaviconFromSizesString(href, siteUrl, faviconType, imageMimeType, sizes)
                }
            }
        }

        return null
    }

    protected open fun getFaviconTypeForLinkElements(linkElement: Element): FaviconType? {
        val relValue = linkElement.attr("rel").lowercase()

        return when (relValue) {
            "icon" -> FaviconType.Icon
            "apple-touch-icon-precomposed" -> FaviconType.AppleTouchPrecomposed
            "apple-touch-icon" -> FaviconType.AppleTouch
            "shortcut icon" -> FaviconType.ShortcutIcon
            else -> return null
        }
    }

    protected open fun mapMetaElementToFavicon(metaElement: Element, siteUrl: String, linkAndMetaElements: Elements): Favicon? {
        if (isOpenGraphImageDeclaration(metaElement)) {
            val imageMimeType = linkAndMetaElements.firstOrNull { it.attr("property") == "og:image:type" }?.attr("content")
            val imageWidth = linkAndMetaElements.firstOrNull { it.attr("property") == "og:image:width" }?.attr("content")?.toIntOrNull()
            val imageHeight = linkAndMetaElements.firstOrNull { it.attr("property") == "og:image:height" }?.attr("content")?.toIntOrNull()

            return createFavicon(metaElement.attr("content"), siteUrl, FaviconType.OpenGraphImage, imageMimeType, imageWidth, imageHeight)
        }
        else if (isMsTileMetaElement(metaElement)) {
            return createFavicon(metaElement.attr("content"), siteUrl, FaviconType.MsTileImage, null, null)
        }

        return null
    }

    protected open fun isOpenGraphImageDeclaration(metaElement: Element) = metaElement.hasAttr("property") && metaElement.attr("property") == "og:image" && metaElement.hasAttr("content")

    protected open fun isMsTileMetaElement(metaElement: Element) = metaElement.hasAttr("name") && metaElement.attr("name") == "msapplication-TileImage" && metaElement.hasAttr("content")


    protected open fun createFaviconFromSizesString(url: String?, siteUrl: String, iconType: FaviconType, iconMimeType: String?, sizesString: String?): Favicon? =
        if (sizesString.isNullOrBlank() == false) {
            val sizes = extractSizesFromString(sizesString)

            createFavicon(url, siteUrl, iconType, iconMimeType, sizes.maxOrNull())
        } else {
            createFavicon(url, siteUrl, iconType, iconMimeType, null)
        }

    protected open fun createFavicon(url: String?, siteUrl: String, iconType: FaviconType, iconMimeType: String?, imageWidth: Int?, imageHeight: Int?): Favicon? =
        if (imageWidth != null && imageHeight != null) {
            createFavicon(url, siteUrl, iconType, iconMimeType, Size(imageWidth, imageHeight))
        } else {
            createFavicon(url, siteUrl, iconType, iconMimeType, null)
        }

    protected open fun createFavicon(url: String?, siteUrl: String, iconType: FaviconType, iconMimeType: String?, size: Size?): Favicon? {
        if (url != null) {
            val urlWithoutQuery = removeQueryFromUrl(url)

            return Favicon(urlUtil.makeLinkAbsolute(urlWithoutQuery, siteUrl), iconType, size ?: extractSizeFromUrl(url), iconMimeType)
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