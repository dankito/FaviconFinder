package net.dankito.utils.favicon

import net.dankito.utils.favicon.extractor.JsoupWebsiteFaviconsExtractor
import net.dankito.utils.favicon.extractor.WebsiteFaviconsExtractor
import net.dankito.utils.favicon.web.*
import net.dankito.web.client.*


open class FaviconFinder @JvmOverloads constructor(
    protected open val webClient : WebClient,
    protected open val faviconsExtractor: WebsiteFaviconsExtractor = JsoupWebsiteFaviconsExtractor(webClient),
    protected open val urlUtil: UrlUtil = UrlUtil.Default
) {

    companion object {
        val IconSizeRegex = Regex("\\d{2,4}[xX×]\\d{2,4}")
    }


    open suspend fun extractFavicons(url: String) : List<Favicon> =
        extractFavicons(url, false) // try relative URLs without "www." first
            .filter { exists(it) }

    protected open suspend fun extractFavicons(url: String, appendWwwDot: Boolean, requestDesktopWebsite: Boolean = false) : List<Favicon> {
        val isRelativeUrl = urlUtil.isRelativeUrl(url)
        val absoluteUrl = urlUtil.makeUrlAbsolute(url, appendWwwDot)
        val userAgent = UserAgent.latest(!!!requestDesktopWebsite)

        val response = webClient.get(RequestParameters(absoluteUrl, String::class, accept = ContentTypes.HTML, userAgent = userAgent))
        if (response.successful) {
            val favicons = extractFavicons(absoluteUrl, response.body!!)
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

        return listOf()
    }

    open suspend fun extractFavicons(url: String, html: String): List<Favicon> =
        faviconsExtractor.extractFavicons(url, html)


    protected open suspend fun exists(favicon: Favicon): Boolean =
        favicon.imageBytes != null || webClient.head(favicon.url).successful

}