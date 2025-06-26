package net.dankito.utils.favicon.extractor

import com.fasterxml.jackson.module.kotlin.readValue
import net.dankito.utils.favicon.Favicon
import net.dankito.utils.favicon.FaviconType
import net.dankito.utils.favicon.json.JsonSerializer
import net.dankito.utils.favicon.webmanifest.WebManifest
import net.dankito.utils.favicon.webmanifest.WebManifestIcon
import org.slf4j.LoggerFactory
import java.net.URL

open class JacksonWebManifestFaviconsExtractor(
    protected val creator: FaviconCreator = FaviconCreator.Default,
) : WebManifestFaviconsExtractor {

    companion object {
        val Default = JacksonWebManifestFaviconsExtractor()
    }


    private val log = LoggerFactory.getLogger(JacksonWebManifestFaviconsExtractor::class.java)


    override fun extractIconsFromWebManifest(manifestAbsoluteUrl: String): List<Favicon> = try {
        // don't know why but when requested with URLConnection then web manifest string starts with ﻿ leading to that Jackson deserialization fails
        val manifest = JsonSerializer.default.readValue<WebManifest>(URL(manifestAbsoluteUrl))

        extractIconsFromWebManifest(manifest, manifestAbsoluteUrl)
    } catch (e: Throwable) {
        log.error("Could not read icons from web manifest of url '$manifestAbsoluteUrl'", e)
        emptyList()
    }


    // TODO: there can also be localized icons, see https://w3c.github.io/manifest/#localizing-image-resources:
    /*
            {
              "lang": "en-US",
              "icons": [
                { "src": "icon/lowres.png", "sizes": "64x64" },
                { "src": "icon/hires.png", "sizes": "256x256" }
              ],
              "icons_localized": {
                "fr": [
                  { "src": "icon/lowres_fr.png", "sizes": "64x64" },
                  { "src": "icon/hires_fr.png", "sizes": "256x256" }
                ]
              }

     */
    override fun extractIconsFromWebManifest(manifest: WebManifest, manifestAbsoluteUrl: String): List<Favicon> = manifest.icons.mapNotNull { icon ->
        val type = getFaviconType(icon)
        // a relative icon url is always resolved against manifest's url, see e.g.
        // https://developer.mozilla.org/en-US/docs/Web/Progressive_web_apps/Manifest/Reference/icons#src
        // TODO: to be standard conformant we should actually check if "Content-Security-Policy: img-src " HTTP header
        //  is specified for resolving relative icon urls, see https://w3c.github.io/manifest/#content-security-policy
        creator.createFaviconFromSizesString(icon.src, manifestAbsoluteUrl, type, icon.type, icon.sizes)
    }

    protected open fun getFaviconType(icon: WebManifestIcon): FaviconType =
        if (icon.src.contains("apple-touch", true)) { // TODO: where is this code coming from? Can this ever be true?
            FaviconType.AppleTouch
        } else if (icon.purpose?.contains("maskable") == true) {
            FaviconType.AndroidChromeMaskable
        } else { // actually only if `purpose` is null or contains any
            FaviconType.AndroidChrome
        }


}