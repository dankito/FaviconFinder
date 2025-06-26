package net.dankito.utils.favicon

import net.codinux.log.logger
import net.dankito.utils.favicon.extractor.JsoupWebsiteFaviconsExtractor
import net.dankito.utils.favicon.extractor.WebsiteFaviconsExtractor
import net.dankito.utils.favicon.web.*
import kotlin.concurrent.thread


open class FaviconFinder @JvmOverloads constructor(
    protected open val faviconsExtractor: WebsiteFaviconsExtractor = JsoupWebsiteFaviconsExtractor(),
    protected open val webClient : IWebClient = UrlConnectionWebClient.Default,
    protected open val urlUtil: UrlUtil = UrlUtil.Default
) {

    companion object {
        val IconSizeRegex = Regex("\\d{2,4}[xX×]\\d{2,4}")
    }

    protected val log by logger()


    init {
      AllowAllCertificatsTrustManager.allowAllCertificates() // ignore certificates error
    }


    open fun extractFaviconsAsync(url: String, callback: (AsyncResult<List<Favicon>>) -> Unit) {
        thread {
            try {
                callback(AsyncResult(true, result = extractFavicons(url)))
            } catch(e: Exception) {
                log.error(e) { "Could not get favicons for $url" }

                callback(AsyncResult(false, e))
            }
        }
    }

    open fun extractFavicons(url: String) : List<Favicon> =
        extractFavicons(url, false) // try relative URLs without "www." first
            .filter { exists(it) }

    protected open fun extractFavicons(url: String, appendWwwDot: Boolean, requestDesktopWebsite: Boolean = false) : List<Favicon> {
        val isRelativeUrl = urlUtil.isRelativeUrl(url)
        val absoluteUrl = urlUtil.makeUrlAbsolute(url, appendWwwDot)

        webClient.get(absoluteUrl, requestDesktopWebsite).let { response ->
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
        }

        return listOf()
    }

    open fun extractFavicons(url: String, html: String): List<Favicon> =
        faviconsExtractor.extractFavicons(url, html)


    protected open fun exists(favicon: Favicon): Boolean =
        favicon.imageBytes != null || webClient.head(favicon.url).successful

}