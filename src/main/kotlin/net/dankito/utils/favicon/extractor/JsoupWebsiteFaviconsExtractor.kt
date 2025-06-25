package net.dankito.utils.favicon.extractor

import net.dankito.utils.favicon.Favicon
import net.dankito.utils.favicon.FaviconType
import net.dankito.utils.favicon.Size
import net.dankito.utils.favicon.extensions.attrOrNull
import net.dankito.utils.favicon.web.IWebClient
import net.dankito.utils.favicon.web.UrlConnectionWebClient
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory
import java.net.URL

open class JsoupWebsiteFaviconsExtractor(
    protected val webClient: IWebClient = UrlConnectionWebClient.Default,
    protected val faviconCreator: FaviconCreator = FaviconCreator.Default,
    protected val webManifestFaviconsExtractor: WebManifestFaviconsExtractor = JacksonWebManifestFaviconsExtractor.Default,
) : WebsiteFaviconsExtractor {

    private val log = LoggerFactory.getLogger(JsoupWebsiteFaviconsExtractor::class.java)


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

        tryToFindDefaultFavicon(url, extractedFavicons)?.let { defaultFavicon ->
            addIfNotAlreadyAdded(extractedFavicons, defaultFavicon)
        }

        return extractedFavicons
    }

    protected open fun tryToFindDefaultFavicon(url: String, extractedFavicons: List<Favicon>): Favicon? = try {
        val urlInstance = URL(url)
        val defaultFaviconUrl = urlInstance.protocol + "://" + urlInstance.host + "/favicon.ico"
        if (doesNotContainIconWithUrl(extractedFavicons, defaultFaviconUrl)) {
            webClient.head(defaultFaviconUrl).let { response ->
                if (response.successful &&
                    (response.contentType == null || response.contentType?.startsWith("text/") == false)) { // filter out e.g. error pages
                    return Favicon(defaultFaviconUrl, FaviconType.ShortcutIcon)
                }
            }
        }

        null
    } catch (e: Throwable) {
        log.error("Could not extract default favicon for url '$url'", e)
        null
    }

    protected open fun extractIconsFromWebManifest(linkAndMetaElements: Elements, siteUrl: String): List<Favicon> =
        linkAndMetaElements.firstOrNull { it.attr("rel") == "manifest" }
            ?.attr("href")
            ?.takeIf { it.isNotBlank() }
            ?.let { manifestUrl -> extractIconsFromWebManifest(manifestUrl, siteUrl) }

            ?: emptyList()

    protected open fun extractIconsFromWebManifest(manifestUrl: String, siteUrl: String): List<Favicon> =
        webManifestFaviconsExtractor.extractIconsFromWebManifest(manifestUrl, siteUrl)


    protected open fun mapLinkElementToFavicon(linkElement: Element, siteUrl: String): Favicon? =
        linkElement.attr("rel").takeUnless { it.isBlank() }?.let { linkRelation ->
            getFaviconTypeForLinkElements(linkRelation)?.let { faviconType ->
                val href = linkElement.attr("href")
                val sizes = linkElement.attrOrNull("sizes")
                val imageMimeType = linkElement.attrOrNull("type")

                if (href.startsWith("data:;base64") == false) {
                    createFaviconFromSizesString(href, siteUrl, faviconType, imageMimeType, sizes)
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
            return createFavicon(metaElement.attr("content"), siteUrl, FaviconType.MsTileImage, null, null)
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


    protected open fun createFaviconFromSizesString(url: String?, siteUrl: String, iconType: FaviconType, iconMimeType: String?, sizesString: String?): Favicon? =
        faviconCreator.createFaviconFromSizesString(url, siteUrl, iconType, iconMimeType, sizesString)

    protected open fun createFavicon(url: String?, siteUrl: String, iconType: FaviconType, iconMimeType: String?, imageWidth: Int?, imageHeight: Int?): Favicon? =
        faviconCreator.createFavicon(url, siteUrl, iconType, iconMimeType, imageWidth, imageHeight)

    protected open fun createFavicon(url: String?, siteUrl: String, iconType: FaviconType, iconMimeType: String?, size: Size?): Favicon? =
        faviconCreator.createFavicon(url, siteUrl, iconType, iconMimeType, size)

}