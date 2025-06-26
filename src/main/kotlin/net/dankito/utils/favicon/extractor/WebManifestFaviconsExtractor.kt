package net.dankito.utils.favicon.extractor

import net.dankito.utils.favicon.Favicon
import net.dankito.utils.favicon.webmanifest.WebManifest

interface WebManifestFaviconsExtractor {

    fun extractIconsFromWebManifest(manifestAbsoluteUrl: String): List<Favicon>

    fun extractIconsFromWebManifest(manifest: WebManifest, manifestAbsoluteUrl: String): List<Favicon>

}