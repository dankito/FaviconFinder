package net.dankito.utils.favicon.extractor

import net.dankito.utils.favicon.Favicon

interface WebManifestFaviconsExtractor {

    fun extractIconsFromWebManifest(manifestUrl: String, siteUrl: String): List<Favicon>

}