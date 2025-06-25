package net.dankito.utils.favicon

import com.fasterxml.jackson.annotation.JsonIgnore


open class Favicon(
    open val url : String,
    open val iconType : FaviconType,
    open var size : Size? = null,
    open val imageMimeType : String? = null
) {

    constructor() : this("", FaviconType.Icon, null, null) // for object deserializers


    /**
     * Temporary value to indicate if tried to determine image size so that we don't
     * fetch image data multiple times.
     */
    @JsonIgnore
    open var triedToRetrieveSize: Boolean = false


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