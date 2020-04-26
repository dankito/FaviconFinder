package net.dankito.utils.favicon

import net.dankito.utils.Size


data class Favicon(
        val url : String,
        val iconType : FaviconType,
        var size : Size? = null,
        val type : String? = null) {


    override fun toString(): String {
        return "$iconType $size $url"
    }

}