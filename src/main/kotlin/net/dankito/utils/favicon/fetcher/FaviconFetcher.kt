package net.dankito.utils.favicon.fetcher

/**
 * Tries to determine the best matching favicon for an url and **returns the bytes of the favicon** directly
 * (instead of returning a **list of all available favicon urls** as [net.dankito.utils.favicon.FaviconFinder] does).
 */
interface FaviconFetcher {

    val supportsPreferredSizeParameter: Boolean

    fun fetch(url: String, preferredSize: Int? = null): ByteArray?

}