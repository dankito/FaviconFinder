package net.dankito.utils.favicon.finder.dto

import kotlinx.serialization.Serializable

@Serializable
data class FaviconExtractorResponse(
    val url: String,
    val host: String,
    val status: Int,
    val statusText: String,
    val duration: String? = null,
    val icons: List<FaviconExtractorIcon> = emptyList(),
)