package net.dankito.utils.favicon.web

open class UrlUtil {

    companion object {
        val Default = UrlUtil()
    }


    open fun isRelativeUrl(url: String): Boolean =
        url.startsWith("http", true) == false


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