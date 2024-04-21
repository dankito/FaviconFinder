package net.dankito.utils.favicon.webmanifest

// for definitions see e.g. https://developer.mozilla.org/en-US/docs/Web/Manifest , https://w3c.github.io/manifest/
data class WebManifest(
    val icons: List<WebManifestIcon>
    // the web manifest of course has much more attributes, but we're only interested in the icons
)