package net.dankito.utils.favicon.location

import net.dankito.utils.favicon.Favicon
import net.dankito.utils.favicon.FaviconType
import net.dankito.utils.favicon.web.IWebClient
import net.dankito.utils.favicon.web.UrlConnectionWebClient
import org.slf4j.LoggerFactory
import java.net.URL

/**
 * Tries to find favicons in standard locations like `domaain/favicon.ico`.
 */
open class StandardLocationFaviconFinder(
    protected val webClient: IWebClient = UrlConnectionWebClient.Default,
) {

    companion object {
        val Default: StandardLocationFaviconFinder = StandardLocationFaviconFinder()
    }


    private val log = LoggerFactory.getLogger(StandardLocationFaviconFinder::class.java)


    open fun tryToFindDefaultFavicon(siteUrl: String, extractedFavicons: List<Favicon>): Favicon? = try {
        val url = URL(siteUrl)
        val defaultFaviconUrl = url.protocol + "://" + url.host + "/favicon.ico"

        if (doesNotContainIconWithUrl(extractedFavicons, defaultFaviconUrl)) {
            webClient.head(defaultFaviconUrl).let { response ->
                if (response.successful &&
                    (response.contentType == null || response.contentType?.startsWith("image/") == true)) { // filter out e.g. error pages
                    return Favicon(defaultFaviconUrl, FaviconType.ShortcutIcon)
                }
            }
        }

        null
    } catch (e: Throwable) {
        log.error("Could not extract default favicon for url '$siteUrl'", e)
        null
    }

    // TODO: this is the same code as in JsoupWebsiteFaviconsExtractor.doesNotContainIconWithUrl()
    protected open fun doesNotContainIconWithUrl(extractedFavicons: List<Favicon>, faviconUrl: String): Boolean {
        extractedFavicons.forEach { favicon ->
            if (favicon.url == faviconUrl) {
                return false
            }
        }

        return true
    }

}