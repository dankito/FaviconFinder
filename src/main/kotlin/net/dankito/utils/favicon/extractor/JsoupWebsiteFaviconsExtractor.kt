package net.dankito.utils.favicon.extractor

import com.fasterxml.jackson.module.kotlin.readValue
import net.dankito.utils.favicon.Favicon
import net.dankito.utils.favicon.FaviconFinder.Companion.IconSizeRegex
import net.dankito.utils.favicon.FaviconType
import net.dankito.utils.favicon.Size
import net.dankito.utils.favicon.json.JsonSerializer
import net.dankito.utils.favicon.web.IWebClient
import net.dankito.utils.favicon.web.UrlConnectionWebClient
import net.dankito.utils.favicon.web.UrlUtil
import net.dankito.utils.favicon.webmanifest.WebManifest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory
import java.net.URL

open class JsoupWebsiteFaviconsExtractor(
    protected val webClient: IWebClient = UrlConnectionWebClient.Default,
    protected val urlUtil: UrlUtil = UrlUtil.Default,
    protected val faviconCreator: FaviconCreator = FaviconCreator.Default,
) : WebsiteFaviconsExtractor {

    private val log = LoggerFactory.getLogger(JsoupWebsiteFaviconsExtractor::class.java)


    override fun extractFavicons(url: String, webSiteHtml: String): List<Favicon> {
        val document = Jsoup.parse(webSiteHtml, url)

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

    protected open fun extractIconsFromWebManifest(linkAndMetaElements: Elements, siteUrl: String): List<Favicon> =
        linkAndMetaElements.firstOrNull { it.attr("rel") == "manifest" }
            ?.attr("href")
            ?.takeIf { it.isNotBlank() }
            ?.let { manifestUrl -> extractIconsFromWebManifest(manifestUrl, siteUrl) }

            ?: emptyList()

    protected open fun extractIconsFromWebManifest(manifestUrl: String, siteUrl: String): List<Favicon> =
        try {
            // don't know why but when requested with URLConnection then web manifest string starts with ﻿ leading to that Jackson deserialization fails
            val manifest = JsonSerializer.default.readValue<WebManifest>(URL(urlUtil.makeLinkAbsolute(manifestUrl, siteUrl)))
            manifest.icons.mapNotNull {
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
            emptyList()
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
    protected open fun mapElementToFavicon(linkOrMetaElement: Element, siteUrl: String, linkAndMetaElements: Elements): Favicon? =
        when (linkOrMetaElement.nodeName().lowercase()) {
            "link" -> mapLinkElementToFavicon(linkOrMetaElement, siteUrl)
            "meta" -> mapMetaElementToFavicon(linkOrMetaElement, siteUrl, linkAndMetaElements)
            else -> null
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
        faviconCreator.createFaviconFromSizesString(url, siteUrl, iconType, iconMimeType, sizesString)

    protected open fun createFavicon(url: String?, siteUrl: String, iconType: FaviconType, iconMimeType: String?, imageWidth: Int?, imageHeight: Int?): Favicon? =
        faviconCreator.createFavicon(url, siteUrl, iconType, iconMimeType, imageWidth, imageHeight)

    protected open fun createFavicon(url: String?, siteUrl: String, iconType: FaviconType, iconMimeType: String?, size: Size?): Favicon? =
        faviconCreator.createFavicon(url, siteUrl, iconType, iconMimeType, size)

}