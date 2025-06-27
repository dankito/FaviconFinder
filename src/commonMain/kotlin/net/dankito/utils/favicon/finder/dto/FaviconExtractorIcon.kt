package net.dankito.utils.favicon.finder.dto

import kotlinx.serialization.Serializable

@Serializable
data class FaviconExtractorIcon(
    val href: String,
    val sizes: String? = null,
)