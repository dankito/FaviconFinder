package net.dankito.utils.favicon.web


open class WebResponse(
    open val successful: Boolean,
    open val status: Int = -1,
    open val contentType: String? = null,
    open val contentLength: Int? = null,
    open val headers: Map<String, List<String>>? = null,
    open val receivedData: ByteArray? = null,
    open val error: Exception? = null
) {


    open val body: String?
        get() = receivedData?.let {
                String(it)
            }


    override fun toString(): String {
        if (successful) {
            return "Succees $status"
        }
        else {
            return "Error $status: $error"
        }
    }

}