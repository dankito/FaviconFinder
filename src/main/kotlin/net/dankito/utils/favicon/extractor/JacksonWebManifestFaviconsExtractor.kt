package net.dankito.utils.favicon.extractor

import com.fasterxml.jackson.module.kotlin.readValue
import net.dankito.utils.favicon.Favicon
import net.dankito.utils.favicon.FaviconType
import net.dankito.utils.favicon.json.JsonSerializer
import net.dankito.utils.favicon.webmanifest.WebManifest
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
        manifest.icons.mapNotNull {
            val type = if (it.src.contains("apple-touch", true)) FaviconType.AppleTouch else FaviconType.Icon
            // a relative icon url is always resolved against manifest's url
            // TODO: to be standard conformant we should actually check if "Content-Security-Policy: img-src " HTTP header
            //  is specified for resolving relative icon urls, see https://w3c.github.io/manifest/#content-security-policy
            creator.createFaviconFromSizesString(it.src, manifestAbsoluteUrl, type, it.type, it.sizes)
        }
    } catch (e: Throwable) {
        log.error("Could not read icons from web manifest of url '$manifestAbsoluteUrl'", e)
        emptyList()
    }

}