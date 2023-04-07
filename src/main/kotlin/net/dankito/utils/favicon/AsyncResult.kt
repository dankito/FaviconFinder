package net.dankito.utils.favicon


open class AsyncResult<T>(val successful : Boolean, val error : Exception? = null, val result : T? = null)