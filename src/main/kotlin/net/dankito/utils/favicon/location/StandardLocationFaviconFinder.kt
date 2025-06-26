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


    /**
     * Tries to find favicon in its standard location, that is <url_root_path>/favicon.ico.
     *
     * If location does not exist and [siteUrl] is a subdomain, also checks <domain>/favicon.ico.
     */
    open fun tryToFindStandardFavicon(siteUrl: String, extractedFavicons: List<Favicon>): Favicon? =
        tryToFindStandardFaviconWithName(siteUrl, "favicon.ico", FaviconType.ShortcutIcon, extractedFavicons)

    /**
     * Tries to find Android Chrome icon in its standard location, that is <url_root_path>/android-chrome-192x192.png
     * and /android-chrome-512x512.png.
     *
     * If location does not exist and [siteUrl] is a subdomain, also checks <domain>/android-chrome-192x192.png
     * and /android-chrome-512x512.png.
     */
    open fun tryToFindStandardAndroidChromeIcon(siteUrl: String, extractedFavicons: List<Favicon>): Favicon? =
        tryToFindStandardFaviconWithName(siteUrl, "android-chrome-192x192.png", FaviconType.AndroidChrome, extractedFavicons)
            ?: tryToFindStandardFaviconWithName(siteUrl, "android-chrome-512x512.png", FaviconType.AndroidChrome, extractedFavicons)

    /**
     * Tries to find Apple touch icon in its standard location, that is <url_root_path>/apple-touch-icon.png.
     *
     * If location does not exist and [siteUrl] is a subdomain, also checks <domain>/apple-touch-icon.png.
     */
    open fun tryToFindStandardAppleTouchIcon(siteUrl: String, extractedFavicons: List<Favicon>): Favicon? =
        tryToFindStandardFaviconWithName(siteUrl, "apple-touch-icon.png", FaviconType.AppleTouch, extractedFavicons)

    protected open fun tryToFindStandardFaviconWithName(siteUrl: String, faviconName: String, type: FaviconType, extractedFavicons: List<Favicon>): Favicon? = try {
        val url = URL(siteUrl)
        val standardFaviconUrl = url.protocol + "://" + url.host + "/" + faviconName

        if (doesNotContainIconWithUrl(extractedFavicons, standardFaviconUrl)) {
            webClient.head(standardFaviconUrl).let { response ->
                if (response.successful &&
                    (response.contentType == null || response.contentType?.startsWith("image/") == true)) { // filter out e.g. error pages
                    return Favicon(standardFaviconUrl, type, null, response.contentType) // TODO: extract size from image url and derive mime type from url
                } else {
                    // if it's a subdomain, also check domain for standard favicon icon
                    getDomainIfIsSubdomain(url)?.let { domain ->
                        return tryToFindStandardFaviconWithName(domain, faviconName, type, extractedFavicons)
                    }
                }
            }
        }

        null
    } catch (e: Throwable) {
        log.error("Could not extract standard favicon with name '$faviconName' for url '$siteUrl'", e)
        null
    }

    protected open fun getDomainIfIsSubdomain(url: URL): String? {
        val host = url.host
        val indexOfSecondLastDot = host.lastIndexOf('.', host.lastIndexOf('.') - 1)

        return if (indexOfSecondLastDot != -1) {
            url.protocol + "://" + host.substring(indexOfSecondLastDot + 1)
        } else {
            null
        }
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