package net.dankito.utils.favicon.web

import java.net.URI


open class UrlUtil {

    open fun isRelativeUrl(url: String): Boolean =
        url.startsWith("http", true) == false

    open fun makeUrlAbsolute(url: String, appendWwwDot: Boolean = false): String {
        if (url.startsWith("http")) {
            return url
        }

        var absoluteUrl = url

        if (appendWwwDot && absoluteUrl.startsWith("www.", true) == false) {
            absoluteUrl = "www." + absoluteUrl
        }

        return "https://" + absoluteUrl
    }

    open fun makeLinkAbsolute(url: String, siteUrl: String): String {
        var absoluteUrl = url

        if(url.startsWith("//")) {
            if(siteUrl.startsWith("https:")) {
                absoluteUrl = "https:" + url
            }
            else {
                absoluteUrl = "http:" + url
            }
        }
        else if(url.startsWith("/") || url.startsWith("./") || url.startsWith("../")) {
            tryToMakeUrlAbsolute(url, siteUrl)?.let { absoluteUrl = it }
        }
        else if(url.startsWith("http") == false) {
            // url does not start with '/' (we checked above) -> prepend '/' so that resolving url works
            tryToMakeUrlAbsolute("/" + url, siteUrl)?.let { absoluteUrl = it }
        }

        return absoluteUrl
    }

    protected open fun tryToMakeUrlAbsolute(relativeUrl: String, siteUrl: String): String? {
        try {
            val relativeUri = URI(relativeUrl)
            if(relativeUri.isAbsolute && relativeUri.scheme.startsWith("http") == false) {
                return relativeUrl // it's an absolute uri but just doesn't start with http, e.g. mailto: for file:
            }
        } catch(ignored: Exception) { }

        try {
            val uri = URI(siteUrl)
            return uri.resolve(relativeUrl).toString()
        } catch(ignored: Exception) { }

        try {
            val uri = URI(siteUrl)

            val port = if(uri.port > 0) ":" + uri.port else ""
            val separator = if(relativeUrl.startsWith("/")) "" else "/"

            val manuallyCreatedUriString = uri.scheme + "://" + uri.host + port + separator + relativeUrl
            val manuallyCreatedUri = URI(manuallyCreatedUriString)
            return manuallyCreatedUri.toString()
        } catch(ignored: Exception) { }

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