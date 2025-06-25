package net.dankito.utils.favicon.extractor

import net.dankito.utils.favicon.Favicon
import net.dankito.utils.favicon.FaviconFinder.Companion.IconSizeRegex
import net.dankito.utils.favicon.FaviconType
import net.dankito.utils.favicon.Size
import net.dankito.utils.favicon.web.UrlUtil
import org.slf4j.LoggerFactory

open class FaviconCreator(
    protected val urlUtil: UrlUtil = UrlUtil.Default
) {

    companion object {
        val Default = FaviconCreator()
    }


    private val log = LoggerFactory.getLogger(FaviconCreator::class.java)


    open fun createFaviconFromSizesString(url: String?, siteUrl: String, iconType: FaviconType, iconMimeType: String?, sizesString: String?): Favicon? =
        if (sizesString.isNullOrBlank() == false) {
            val sizes = extractSizesFromString(sizesString)

            createFavicon(url, siteUrl, iconType, iconMimeType, sizes.maxOrNull())
        } else {
            createFavicon(url, siteUrl, iconType, iconMimeType, null)
        }

    open fun createFavicon(url: String?, siteUrl: String, iconType: FaviconType, iconMimeType: String?, imageWidth: Int?, imageHeight: Int?): Favicon? =
        if (imageWidth != null && imageHeight != null) {
            createFavicon(url, siteUrl, iconType, iconMimeType, Size(imageWidth, imageHeight))
        } else {
            createFavicon(url, siteUrl, iconType, iconMimeType, null)
        }

    open fun createFavicon(url: String?, siteUrl: String, iconType: FaviconType, iconMimeType: String?, size: Size?): Favicon? {
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