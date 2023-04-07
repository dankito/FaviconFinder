package net.dankito.utils.favicon

import net.dankito.utils.favicon.web.IWebClient
import net.dankito.utils.favicon.web.UrlConnectionWebClient
import org.slf4j.LoggerFactory


open class FaviconComparator(open val webClient : IWebClient = UrlConnectionWebClient()) {

    companion object {
        const val DEFAULT_MIN_SIZE = 32

        private val log = LoggerFactory.getLogger(FaviconComparator::class.java)
    }


    open fun getBestIcon(favicons: List<Favicon>, minSize: Int = DEFAULT_MIN_SIZE, maxSize: Int? = null, returnSquarishOneIfPossible: Boolean = false,
                         fileTypesToExclude: List<String> = listOf()) : Favicon? {
        // retrieve sizes of icons which's size isn't known yet
        favicons.filter { it.size == null }.forEach {
            it.size = retrieveIconSize(it)
        }

        // return icon with largest size
        favicons.filter { doesFitSize(it, minSize, maxSize, returnSquarishOneIfPossible, fileTypesToExclude) }.sortedByDescending { it.size }.firstOrNull()?.let {
            return it
        }

        if (returnSquarishOneIfPossible) { // then try without returnSquarishOneIfPossible
            favicons.filter { doesFitSize(it, minSize, maxSize, false, fileTypesToExclude) }.sortedByDescending { it.size }.firstOrNull()?.let {
                return it
            }
        }

        if (maxSize != null) { // if maxSize is set, try next without maxSize
            favicons.filter { doesFitSize(it, minSize, null, false, fileTypesToExclude) }.sortedBy { it.size }.firstOrNull()?.let {
                return it
            }
        }

        if (fileTypesToExclude.isNotEmpty()) { // then try to find any icon that matches other parameters
            return getBestIcon(favicons, minSize, maxSize, returnSquarishOneIfPossible, listOf())
        }

        return favicons.firstOrNull { it.size == null }
    }


    open fun doesFitSize(iconUrl: String, minSize: Int = DEFAULT_MIN_SIZE, maxSize: Int? = null, mustBeSquarish: Boolean = false,
                         fileTypesToExclude: List<String> = listOf()): Boolean {

        if (isExcludedFileType(iconUrl, fileTypesToExclude)) {
            return false
        }

        retrieveIconSize(iconUrl)?.let {
            return doesFitSize(it, minSize, maxSize, mustBeSquarish)
        }

        return false
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


    protected open fun retrieveIconSize(favicon: Favicon) : Size? {
        return retrieveIconSize(favicon.url)
    }

    protected open fun retrieveIconSize(iconUrl: String) : Size? {
        try {
            if (iconUrl.endsWith(".svg", true) == false) { // for .svg size cannot be determined
                val response = webClient.get(iconUrl)
                if (response.successful) {
                    response.receivedData?.let { receivedData ->
                        val imageInfo = SimpleImageInfo(receivedData)
                        return Size(imageInfo.width, imageInfo.height)
                    }
                }
            }
        } catch(e: Exception) { log.error("Could not retrieve icon size for url $iconUrl", e) }

        return null
    }

}