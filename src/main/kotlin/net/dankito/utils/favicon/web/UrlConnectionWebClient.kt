package net.dankito.utils.favicon.web

import org.slf4j.LoggerFactory
import java.io.FileNotFoundException
import java.net.HttpURLConnection
import java.net.URL


open class UrlConnectionWebClient : IWebClient {

    companion object {
        val Default = UrlConnectionWebClient()
    }


    private val log = LoggerFactory.getLogger(UrlConnectionWebClient::class.java)


    override fun get(url: String, requestDesktopWebsite: Boolean): WebResponse {
        try {
            val connection = createConnection(url, "GET", requestDesktopWebsite)

            val inputStream = connection.getInputStream().buffered()
            val receivedData = inputStream.readBytes()

            return closeConnectionAndMapResponse(connection, receivedData) { redirectUrl ->
                get(redirectUrl, requestDesktopWebsite)
            }
        } catch (e: Exception) {
            return logAndMapError(url, e)
        }
    }


    override fun head(url: String, requestDesktopWebsite: Boolean): WebResponse {
        try {
            val connection = createConnection(url, "HEAD", requestDesktopWebsite)

            try {
                return closeConnectionAndMapResponse(connection, null) { redirectUrl ->
                    head(redirectUrl, requestDesktopWebsite)
                }
            } catch (e: FileNotFoundException) { // couldn't believe it, HEAD throws FileNotFoundException if url doesn't exist
                return WebResponse(false, 404)
            }
        } catch (e: Exception) {
            return logAndMapError(url, e)
        }
    }

    private fun createConnection(url: String, method: String, requestDesktopWebsite: Boolean): HttpURLConnection {
        val connection = URL(url).openConnection() as HttpURLConnection

        connection.requestMethod = method
        connection.instanceFollowRedirects = true

        connection.connectTimeout = 5 * 1000

        if (requestDesktopWebsite) {
            connection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
        }

        connection.connect()

        return connection
    }


    protected open fun closeConnectionAndMapResponse(connection: HttpURLConnection, receivedData: ByteArray?, redirectUrlRetrieved: ((String) -> WebResponse)? = null): WebResponse {
        val status = connection.responseCode
        val contentType = connection.contentType
        val contentLength = connection.contentLength
        val headers = connection.headerFields

        connection.inputStream.close()

        if (status in 300..399 && headers.containsKey("Location") && redirectUrlRetrieved != null) {
            headers["Location"]?.firstOrNull()?.let { redirectUrl ->
                return redirectUrlRetrieved(redirectUrl)
            }
        }

        return WebResponse(status in 200..299, status, contentType, contentLength, headers, receivedData)
    }

    protected open fun logAndMapError(url: String, e: Exception): WebResponse {
        log.error("Could not request $url", e)

        return WebResponse(false, error = e)
    }

}