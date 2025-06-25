package net.dankito.utils.favicon

import net.dankito.utils.favicon.web.IWebClient
import net.dankito.utils.favicon.web.UrlConnectionWebClient
import org.slf4j.LoggerFactory
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


open class FaviconComparator(open val webClient : IWebClient = UrlConnectionWebClient.Default) {

    companion object {
        const val DEFAULT_MIN_SIZE = 32
    }

    private val log = LoggerFactory.getLogger(FaviconComparator::class.java)


    open fun getBestIcon(favicons: List<Favicon>, preferredSizes: Collection<Int>, returnSquarishOneIfPossible: Boolean = false,
                         fileTypesToExclude: List<String> = listOf(), ignoreParametersAsLastResort: Boolean = false) : Favicon? =
        preferredSizes.firstNotNullOfOrNull { getBestIcon(favicons, it, it, returnSquarishOneIfPossible, fileTypesToExclude, false) }
            ?: if (ignoreParametersAsLastResort) getBestIcon(favicons, min(DEFAULT_MIN_SIZE, preferredSizes.firstOrNull() ?: DEFAULT_MIN_SIZE), preferredSizes.firstOrNull(), returnSquarishOneIfPossible, fileTypesToExclude, ignoreParametersAsLastResort)
                else null

    // we have to set ignoreParametersAsLastResort to true for now as otherwise this would be a breaking change
    open fun getBestIcon(favicons: List<Favicon>, minSize: Int = DEFAULT_MIN_SIZE, maxSize: Int? = null, returnSquarishOneIfPossible: Boolean = false,
                         fileTypesToExclude: List<String> = listOf(), ignoreParametersAsLastResort: Boolean = true) : Favicon? {
        // retrieve sizes of icons which's size isn't known yet
        favicons.filter { it.size == null }.forEach {
            retrieveIconSize(it)
        }

        val faviconsToCheck = favicons.filter { it.size != null } // we cannot check if favicon matches size if its size is not known
            .filter { it.iconType != FaviconType.SafariMaskIcon } // we ignore Safari mask icons for finding a good favicon

        // return icon with largest size
        faviconsToCheck.filter { doesFitSize(it, minSize, maxSize, returnSquarishOneIfPossible, fileTypesToExclude) }.sortedByDescending { it.size }.firstOrNull()?.let {
            return it
        }

        if (returnSquarishOneIfPossible && ignoreParametersAsLastResort) { // then try without returnSquarishOneIfPossible
            faviconsToCheck.filter { doesFitSize(it, minSize, maxSize, false, fileTypesToExclude) }.sortedByDescending { it.size }.firstOrNull()?.let {
                return it
            }
        }

        if (maxSize != null && ignoreParametersAsLastResort) { // if maxSize is set, try next without maxSize
            // find the size that has the closest distance to maxSize
            val distances = faviconsToCheck.associateWith { abs(max(it.size!!.width, it.size!!.height) - maxSize) }
            return distances.minBy { it.value }.key
        }

        if (fileTypesToExclude.isNotEmpty() && ignoreParametersAsLastResort) { // then try to find any icon that matches other parameters
            return getBestIcon(favicons, minSize, maxSize, returnSquarishOneIfPossible, listOf(), ignoreParametersAsLastResort)
        }

        return if (ignoreParametersAsLastResort) {
            favicons.firstOrNull { it.size == null && it.iconType != FaviconType.SafariMaskIcon }
        } else {
            null
        }
    }


    protected open fun doesFitSize(favicon: Favicon, minSize: Int, maxSize: Int? = null, mustBeSquarish: Boolean,
                                   fileTypesToExclude: List<String> = listOf()) : Boolean {

        if (isExcludedFileType(favicon, fileTypesToExclude)) {
            return false
        }

        favicon.size?.let { faviconSize ->
            return doesFitSize(faviconSize, minSize, maxSize, mustBeSquarish)
        }

        return false
    }

    protected open fun doesFitSize(faviconSize: Size, minSize: Int, maxSize: Int? = null, mustBeSquarish: Boolean) : Boolean {
        var result = hasMinSize(faviconSize, minSize)

        maxSize?.let { result = result.and(hasMaxSize(faviconSize, maxSize)) }

        if(mustBeSquarish) {
            result = result.and(faviconSize.isSquare())
        }

        return result
    }

    protected open fun hasMinSize(iconSize: Size?, minSize: Int = DEFAULT_MIN_SIZE): Boolean {
        if(iconSize != null) {
            return iconSize.width >= minSize && iconSize.height >= minSize
        }

        return false
    }

    protected open fun hasMaxSize(iconSize: Size?, maxSize: Int): Boolean {
        if(iconSize != null) {
            return iconSize.width <= maxSize && iconSize.height <= maxSize
        }

        return false
    }


    protected open fun isExcludedFileType(favicon: Favicon, fileTypesToExclude: List<String>): Boolean {
        return isExcludedFileType(favicon.url, fileTypesToExclude)
    }

    protected open fun isExcludedFileType(faviconUrl: String, fileTypesToExclude: List<String>): Boolean {
        return fileTypesToExclude.any { faviconUrl.endsWith(it) }
    }


    protected open fun retrieveIconSize(favicon: Favicon) {
        if (favicon.triedToRetrieveSize == false) {
            val (imageBytes, imageInfo) = retrieveIconSize(favicon.url)
            favicon.triedToRetrieveSize = true
            favicon.imageBytes = imageBytes

            if (imageInfo != null) {
                favicon.size = Size(imageInfo.width, imageInfo.height)
                if (favicon.imageMimeType == null) {
                    favicon.imageMimeType = imageInfo.mimeType
                }
            }
        }
    }

    protected open fun retrieveIconSize(iconUrl: String): Pair<ByteArray?, SimpleImageInfo?> {
        try {
            if (iconUrl.endsWith(".svg", true) == false) { // for .svg size cannot be determined
                val response = webClient.get(iconUrl)
                if (response.successful) {
                    response.receivedData?.let { receivedData ->
                        return receivedData to SimpleImageInfo(receivedData)
                    }
                }
            }
        } catch(e: Exception) { log.error("Could not retrieve icon size for url $iconUrl", e) }

        return null to null
    }

}