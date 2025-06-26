package net.dankito.utils.favicon.fetcher

import net.dankito.web.client.WebClient

/**
 * Fetches the best matching favicon for an url with Google's
 * favicon service (https://t0.gstatic.com/faviconV2).
 *
 * Support the `preferredSize` parameter.
 *
 * And also a `minSize` and `maxSize` parameter.
 */
open class GoogleFaviconFetcher(webClient: WebClient) : FaviconFetcherBase(webClient) {

    override val supportsPreferredSizeParameter = true


    override fun getFaviconFetcherUrl(url: String, preferredSize: Int?) =
        getFaviconFetcherUrl(url, preferredSize, 16)

    open fun getFaviconFetcherUrl(url: String, preferredSize: Int?, minSize: Int = 16, maxSize: Int? = preferredSize?.let { it * 2 }): String {
        // https://t0.gstatic.com/faviconV2?client=chrome&nfrp=2&check_seen=true&size=32&min_size=16&max_size=256&fallback_opts=TYPE,SIZE,URL&url=
        // other version as used e.g. by Keepass (https://github.com/navossoc/KeePass-Yet-Another-Favicon-Downloader/issues/69):
        // https://t0.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=http://www.pluimen.nl&size=128
        // https://t2.gstatic.com/faviconV2?.. does also seem to work
        // There's also a proxy by Brave, so that Google doesn't see user's IP address, but we are not allowed to use it (https://github.com/brave/brave-browser/issues/42127):
        // https://t0.proxy.brave.com/faviconV2?client=chrome&nfrp=2&check_seen=true&size=32&min_size=16&max_size=256&fallback_opts=TYPE,SIZE,URL&url=https://search.brave.com/

        var fetcherUrl = "https://t0.gstatic.com/faviconV2?client=chrome&nfrp=2&check_seen=true&min_size=$minSize"

        if (preferredSize != null) {
            fetcherUrl += "&size=$preferredSize"
        }
        if (maxSize != null) {
            fetcherUrl += "&max_size=$maxSize"
        }

        return "$fetcherUrl&fallback_opts=TYPE,SIZE,URL&url=${ensureStartsWithHttpOrHttps(url)}"
    }

}