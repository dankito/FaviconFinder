package net.dankito.utils.favicon.extensions

import org.jsoup.nodes.Node

fun Node.attrOrNull(attributeKey: String): String? =
    if (this.hasAttr(attributeKey)) {
        this.attr(attributeKey)
    } else {
        null
    }