package net.dankito.utils.favicon.extractor

import net.dankito.utils.favicon.Favicon

interface WebManifestFaviconsExtractor {

    fun extractIconsFromWebManifest(manifestAbsoluteUrl: String): List<Favicon>

}