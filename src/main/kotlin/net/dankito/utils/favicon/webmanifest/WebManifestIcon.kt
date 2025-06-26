package net.dankito.utils.favicon.webmanifest

import kotlinx.serialization.Serializable

@Serializable
data class WebManifestIcon(
    val src: String,
    val sizes: String? = null,
    val type: String? = null,
    val purpose: String? = null,
)