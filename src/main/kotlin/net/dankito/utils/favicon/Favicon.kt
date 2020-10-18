package net.dankito.utils.favicon

import net.dankito.utils.Size


open class Favicon(
    open val url : String,
    open val iconType : FaviconType,
    open var size : Size? = null,
    open val type : String? = null
) {

    constructor() : this("", FaviconType.Icon, null, null) // for object deserializers


    override fun toString(): String {
        return "$iconType $size $url"
    }

}