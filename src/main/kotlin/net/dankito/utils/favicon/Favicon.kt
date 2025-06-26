package net.dankito.utils.favicon

import com.fasterxml.jackson.annotation.JsonIgnore


open class Favicon(
    open val url : String,
    open val iconType : FaviconType,
    // TODO: the icon actually may has multiple sizes like for .ico files (a container format) or like in this web manifest example:
    /*
        {
          "src": "icon/hd_hi.ico",
          "sizes": "72x72 96x96 128x128 256x256"
        }
     */
    open var size : Size? = null,
    imageMimeType : String? = null,
    /**
     * If set to `true`, than [imageMimeType] has been derived from image's file name.
     * Otherwise it has been explicitly stated in HTML or Web Manifest
     */
    open val isMimeTypeDerivedFromFilename: Boolean = false,

    /**
     * Only relevant for [FaviconType.MsTileImage] and [FaviconType.SafariMaskIcon]:
     * The background color for the live tile (MsTileImage) or
     * color of the Safari mask icon if pinned tab is selected.
     */
    open val color: String? = null,
) {

    constructor() : this("", FaviconType.Icon, null, null) // for object deserializers


    open var imageMimeType : String? = imageMimeType
        internal set

    /**
     * Temporary value to indicate if tried to determine image size so that we don't
     * fetch image data multiple times.
     */
    @JsonIgnore
    open var triedToRetrieveSize: Boolean = false

    /**
     * Temporary value that holds image's data if we retrieved it e.g. to determine its size.
     */
    @JsonIgnore
    open var imageBytes: ByteArray? = null


    override fun toString(): String {
        return "$iconType $size $url"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Favicon) return false

        if (url != other.url) return false
        if (iconType != other.iconType) return false
        if (size != other.size) return false
        if (imageMimeType != other.imageMimeType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + iconType.hashCode()
        result = 31 * result + (size?.hashCode() ?: 0)
        result = 31 * result + (imageMimeType?.hashCode() ?: 0)
        return result
    }

}