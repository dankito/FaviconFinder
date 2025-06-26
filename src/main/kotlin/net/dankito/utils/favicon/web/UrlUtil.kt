package net.dankito.utils.favicon.web

import java.net.URI


open class UrlUtil {

    companion object {
        val Default = UrlUtil()
    }


    open fun isRelativeUrl(url: String): Boolean =
        url.startsWith("http", true) == false

    open fun makeUrlAbsolute(url: String, appendWwwDot: Boolean = false): String {
        if (url.startsWith("http", true) || url.contains("://")) {
            return url
        }

        var absoluteUrl = url

        if (appendWwwDot && absoluteUrl.startsWith("www.", true) == false) {
            absoluteUrl = "www." + absoluteUrl
        }

        return "https://" + absoluteUrl
    }

    // see e.g. https://developer.mozilla.org/en-US/docs/Web/API/URL_API/Resolving_relative_references for all possible use cases (except relative url starting with '//')
    open fun makeLinkAbsolute(url: String, baseUrl: String): String {
        var absoluteUrl = url

        if (url.startsWith("//")) {
            if (baseUrl.startsWith("https:")) {
                absoluteUrl = "https:" + url
            } else {
                absoluteUrl = "http:" + url
            }
        } else if (url.startsWith("/") || url.startsWith("./") || url.startsWith("../")) {
            tryToMakeUrlAbsolute(url, baseUrl)?.let { absoluteUrl = it }
        } else if (url.startsWith("http", true) == false) {
            // url does not start with '/' (we checked above) -> prepend '/' so that resolving url works
            tryToMakeUrlAbsolute(url, baseUrl)?.let { absoluteUrl = it }
        }

        return absoluteUrl
    }

    protected open fun tryToMakeUrlAbsolute(relativeUrl: String, baseUrl: String): String? {
        try {
            val relativeUri = URI(relativeUrl)
            if (relativeUri.isAbsolute && relativeUri.scheme.startsWith("http") == false) {
                return relativeUrl // it's an absolute uri but just doesn't start with http, e.g. mailto: for file:
            }
        } catch (_: Throwable) { }

        try {
            val uri = URI(baseUrl)
            val relativeUrlToUse = if (uri.path.isNullOrEmpty() && relativeUrl.startsWith("/") == false) {
                // fix that for empty paths URI.resolve() does not add a '/' between baseUrl and relative url, e.g.
                // "https://codinux.net" + "icon.png" -> "https://codinux.neticon.png" (honestly, URI.resolve()!?)
                "/$relativeUrl"
            } else {
                relativeUrl
            }

            return uri.resolve(relativeUrlToUse).toString()
                .replace("../", "") // fix bug if relative url contains more '../' than baseUrl has path segments, that '../' stays in url instead of removing it
        } catch (_: Throwable) { }

        try {
            val uri = URI(baseUrl)

            val port = if (uri.port > 0) ":" + uri.port else ""
            val separator = if (relativeUrl.startsWith("/")) "" else "/"

            val manuallyCreatedUriString = uri.scheme + "://" + uri.host + port + separator + relativeUrl
            val manuallyCreatedUri = URI(manuallyCreatedUriString)
            return manuallyCreatedUri.toString()
        } catch (_: Throwable) { }

        return null
    }


    open fun ensureStartsWithHttpOrHttps(url: String): String =
        if (url.startsWith("http://", true) || url.startsWith("https://")) {
            url
        } else {
            "https://$url"
        }

    open fun removeProtocolAndWww(url: String): String {
        var result = url

        val protocolSeparatorIndex = result.indexOf("://")
        if (protocolSeparatorIndex != -1) {
            result = result.substring(protocolSeparatorIndex + "://".length)
        }

        if (result.startsWith("www.", true)) {
            result = result.substring(4)
        }

        return result
    }

}