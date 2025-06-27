package net.dankito.utils.favicon.extractor

import net.codinux.log.logger
import net.dankito.utils.favicon.Favicon
import net.dankito.utils.favicon.FaviconType
import net.dankito.utils.favicon.Size
import net.dankito.utils.favicon.web.UrlUtilJvm

open class FaviconCreator(
    protected val urlUtil: UrlUtilJvm = UrlUtilJvm.Default
) {

    companion object {
        val Default = FaviconCreator()

        val IconSizeRegex = Regex("\\d{2,4}[xX×]\\d{2,4}")
    }


    protected val log by logger()


    open fun createFaviconFromSizesString(url: String?, siteUrl: String, iconType: FaviconType, iconMimeType: String?, sizesString: String?, color: String? = null): Favicon? =
        if (sizesString.isNullOrBlank() == false) {
            val sizes = extractSizesFromString(sizesString)

            createFavicon(url, siteUrl, iconType, iconMimeType, sizes.maxOrNull(), color)
        } else {
            createFavicon(url, siteUrl, iconType, iconMimeType, null, color)
        }

    open fun createFavicon(url: String?, siteUrl: String, iconType: FaviconType, iconMimeType: String?, imageWidth: Int?, imageHeight: Int?, color: String? = null): Favicon? =
        if (imageWidth != null && imageHeight != null) {
            createFavicon(url, siteUrl, iconType, iconMimeType, Size(imageWidth, imageHeight), color)
        } else {
            createFavicon(url, siteUrl, iconType, iconMimeType, null, color)
        }

    open fun createFavicon(url: String?, siteUrl: String, iconType: FaviconType, iconMimeType: String?, size: Size?, color: String? = null): Favicon? {
        if (url != null) {
            val urlWithoutQuery = removeQueryFromUrl(url)
            val (mimeType, isDerivedFromFilename) = if (iconMimeType != null) iconMimeType to false
                                                    else getMimeTypeFromUrl(urlWithoutQuery) to true

            return Favicon(urlUtil.makeLinkAbsolute(urlWithoutQuery, siteUrl), iconType, size ?: extractSizeFromUrl(url),
                mimeType, isDerivedFromFilename, color)
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
            log.error(e) { "Could not remove query from url $url" }
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

    protected open fun getMimeTypeFromUrl(url: String): String? = when (url.substringAfterLast('.').lowercase()) {
        "png" -> "image/png"
        "svg" -> "image/svg+xml"
        "ico" -> "image/x-icon" // "image/vnd.microsoft.icon" is the official IANA-registered type, but "image/x-icon" is more widely used
        "gif" -> "image/gif"
        "jpg", "jpeg" -> "image/jpeg"
        "tif", "tiff" -> "image/tiff"
        "bmp" -> "image/bmp"
        "webp" -> "image/webp"
        else -> null
    }

}