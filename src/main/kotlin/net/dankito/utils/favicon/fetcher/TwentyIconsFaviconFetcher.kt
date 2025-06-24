package net.dankito.utils.favicon.fetcher

import net.dankito.utils.favicon.web.IWebClient
import kotlin.math.abs

/**
 * Fetches the best matching favicon for an url with Twenty Icons
 * favicon service (`https://twenty-icons.com/<url>/<optional size>`).
 *
 * Support the `preferredSize` parameter but only these size (of course specify only
 * the first number for `preferredSize` parameter):
 * 16x16, 32x32, 64x64, 128x128, 180x180, 192x192.
 *
 * For documentation see: [https://github.com/twentyhq/favicon](https://github.com/twentyhq/favicon).
 */
open class TwentyIconsFaviconFetcher(webClient: IWebClient) : FaviconFetcherBase(webClient) {

    companion object {
        val SupportedSizes = listOf(16, 32, 64, 128, 180, 192)
    }


    override val supportsPreferredSizeParameter = true


    override fun getFaviconFetcherUrl(url: String, preferredSize: Int?) =
        "https://twenty-icons.com/${removeProtocolAndWww(url)}" +
                if (preferredSize == null) ""
                else "/${mapToSupportedSize(preferredSize)}"

    protected open fun mapToSupportedSize(preferredSize: Int): Int =
        if (preferredSize in SupportedSizes) {
            preferredSize
        } else {
            // find the supported size that has the closest distance to preferredSize
            val distances = SupportedSizes.associateWith { abs(it - preferredSize) }
            distances.minBy { it.value }.key
        }

}