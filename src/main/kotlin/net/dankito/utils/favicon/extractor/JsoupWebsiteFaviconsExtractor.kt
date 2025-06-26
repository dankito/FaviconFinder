package net.dankito.utils.favicon.extractor

import net.dankito.utils.favicon.Favicon
import net.dankito.utils.favicon.FaviconType
import net.dankito.utils.favicon.Size
import net.dankito.utils.favicon.extensions.attrOrNull
import net.dankito.utils.favicon.location.StandardLocationFaviconFinder
import net.dankito.utils.favicon.web.IWebClient
import net.dankito.utils.favicon.web.UrlConnectionWebClient
import net.dankito.utils.favicon.web.UrlUtil
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

open class JsoupWebsiteFaviconsExtractor(
    protected val webClient: IWebClient = UrlConnectionWebClient.Default,
    protected val faviconCreator: FaviconCreator = FaviconCreator.Default,
    protected val webManifestFaviconsExtractor: WebManifestFaviconsExtractor = JacksonWebManifestFaviconsExtractor.Default,
    protected val standardLocationFaviconFinder: StandardLocationFaviconFinder = StandardLocationFaviconFinder.Default,
    protected val urlUtil: UrlUtil = UrlUtil.Default,
) : WebsiteFaviconsExtractor {

    override fun extractFavicons(url: String, webSiteHtml: String): List<Favicon> {
        val document = Jsoup.parse(webSiteHtml, url)

        return extractFavicons(document, url)
    }

    open fun extractFavicons(document: Document, url: String): List<Favicon> {
        val head = document.head()
        val linkElements = head.select("link")
        val metaElements = head.select("meta")

        /**
         * Possible formats are documented here https://stackoverflow.com/questions/21991044/how-to-get-high-resolution-website-logo-favicon-for-a-given-url#answer-22007642
         * and here https://en.wikipedia.org/wiki/Favicon
         */
        val extractedFavicons = (linkElements.mapNotNull { mapLinkElementToFavicon(it, url) } +
                metaElements.mapNotNull { mapMetaElementToFavicon(it, url, metaElements) })
            .toMutableList()

        val faviconsInWebManifest = extractIconsFromWebManifest(linkElements, url)
        addIfNotAlreadyAdded(extractedFavicons, faviconsInWebManifest)

        standardLocationFaviconFinder.tryToFindStandardFavicon(url, extractedFavicons)?.let { defaultFavicon ->
            addIfNotAlreadyAdded(extractedFavicons, defaultFavicon)
        }

        return extractedFavicons
    }

    protected open fun extractIconsFromWebManifest(linkAndMetaElements: Elements, siteUrl: String): List<Favicon> =
        linkAndMetaElements.firstOrNull { it.attr("rel") == "manifest" }
            ?.attr("href")
            ?.takeIf { it.isNotBlank() }
            ?.let { manifestUrl -> extractIconsFromWebManifest(manifestUrl, siteUrl) }

            ?: emptyList()

    protected open fun extractIconsFromWebManifest(manifestUrl: String, siteUrl: String): List<Favicon> =
        webManifestFaviconsExtractor.extractIconsFromWebManifest(urlUtil.makeLinkAbsolute(manifestUrl, siteUrl))


    protected open fun mapLinkElementToFavicon(linkElement: Element, siteUrl: String): Favicon? =
        linkElement.attr("rel").takeUnless { it.isBlank() }?.let { linkRelation ->
            getFaviconTypeForLinkElements(linkRelation)?.let { faviconType ->
                val href = linkElement.attr("href")
                val sizes = linkElement.attrOrNull("sizes")
                val imageMimeType = linkElement.attrOrNull("type")
                val color = linkElement.attrOrNull("color")

                if (href.startsWith("data:;base64") == false) {
                    createFaviconFromSizesString(href, siteUrl, faviconType, imageMimeType, sizes, color)
                } else {
                    null // TODO: handle data favicons
                }
            }
        }

    protected open fun getFaviconTypeForLinkElements(linkRelation: String): FaviconType? = when (linkRelation.lowercase()) {
        "icon" -> FaviconType.Icon
        "shortcut icon" -> FaviconType.ShortcutIcon
        "apple-touch-icon" -> FaviconType.AppleTouch
        "apple-touch-icon-precomposed" -> FaviconType.AppleTouchPrecomposed
        "mask-icon" -> FaviconType.SafariMaskIcon
        else -> null
    }


    protected open fun mapMetaElementToFavicon(metaElement: Element, siteUrl: String, metaElements: Elements): Favicon? {
        if (isOpenGraphImageDeclaration(metaElement)) { // for open graph image url, image type, width and height are all on different <meta> elements
            val imageMimeType = metaElements.firstOrNull { it.attr("property") == "og:image:type" }?.attr("content")
            val imageWidth = metaElements.firstOrNull { it.attr("property") == "og:image:width" }?.attr("content")?.toIntOrNull()
            val imageHeight = metaElements.firstOrNull { it.attr("property") == "og:image:height" }?.attr("content")?.toIntOrNull()

            return createFavicon(metaElement.attr("content"), siteUrl, FaviconType.OpenGraphImage, imageMimeType, imageWidth, imageHeight)
        }
        else if (isMsTileMetaElement(metaElement)) {
            val color = metaElements.firstOrNull { it.attr("name") == "msapplication-TileColor" }?.attrOrNull("content")
            return createFavicon(metaElement.attr("content"), siteUrl, FaviconType.MsTileImage, null, null, color)
        }

        return null
    }

    protected open fun isOpenGraphImageDeclaration(metaElement: Element) = metaElement.hasAttr("property") && metaElement.attr("property") == "og:image" && metaElement.hasAttr("content")

    protected open fun isMsTileMetaElement(metaElement: Element) = metaElement.hasAttr("name") && metaElement.attr("name") == "msapplication-TileImage" && metaElement.hasAttr("content")


    protected open fun addIfNotAlreadyAdded(extractedFavicons: MutableList<Favicon>, additionalCandidates: List<Favicon>) {
        additionalCandidates.forEach { favicon ->
            addIfNotAlreadyAdded(extractedFavicons, favicon)
        }
    }

    protected open fun addIfNotAlreadyAdded(extractedFavicons: MutableList<Favicon>, candidate: Favicon) {
        if (doesNotContainIconWithUrl(extractedFavicons, candidate.url)) {
            extractedFavicons.add(candidate)
        }
    }

    protected open fun doesNotContainIconWithUrl(extractedFavicons: List<Favicon>, faviconUrl: String): Boolean {
        extractedFavicons.forEach { favicon ->
            if (favicon.url == faviconUrl) {
                return false
            }
        }

        return true
    }


    protected open fun createFaviconFromSizesString(url: String?, siteUrl: String, iconType: FaviconType, iconMimeType: String?, sizesString: String?, color: String? = null): Favicon? =
        faviconCreator.createFaviconFromSizesString(url, siteUrl, iconType, iconMimeType, sizesString, color)

    protected open fun createFavicon(url: String?, siteUrl: String, iconType: FaviconType, iconMimeType: String?, imageWidth: Int?, imageHeight: Int?, color: String? = null): Favicon? =
        faviconCreator.createFavicon(url, siteUrl, iconType, iconMimeType, imageWidth, imageHeight, color)

    protected open fun createFavicon(url: String?, siteUrl: String, iconType: FaviconType, iconMimeType: String?, size: Size?, color: String? = null): Favicon? =
        faviconCreator.createFavicon(url, siteUrl, iconType, iconMimeType, size, color)

}