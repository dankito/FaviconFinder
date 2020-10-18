package net.dankito.utils.favicon.web

import org.slf4j.LoggerFactory
import java.net.HttpURLConnection
import java.net.URL


open class UrlConnectionWebClient : IWebClient {

    companion object {
        private val log = LoggerFactory.getLogger(UrlConnectionWebClient::class.java)
    }


    override fun get(url: String): WebResponse {
        try {
            val connection = createConnection(url, "GET")

            val inputStream = connection.getInputStream().buffered()
            val receivedData = inputStream.readBytes()

            return closeConnectionAndMapResponse(connection, receivedData)
        } catch (e: Exception) {
            return logAndMapError(url, e)
        }
    }


    override fun head(url: String): WebResponse {
        try {
            val connection = createConnection(url, "HEAD")

            return closeConnectionAndMapResponse(connection, null)
        } catch (e: Exception) {
            return logAndMapError(url, e)
        }
    }

    private fun createConnection(url: String, method: String): HttpURLConnection {
        val connection = URL(url).openConnection() as HttpURLConnection

        connection.requestMethod = method
        connection.instanceFollowRedirects = true

        connection.connect()

        return connection
    }


    protected open fun closeConnectionAndMapResponse(connection: HttpURLConnection, receivedData: ByteArray?): WebResponse {
        val status = connection.responseCode

        connection.inputStream.close()

        return WebResponse(status in 200..299, status, receivedData)
    }

    protected open fun logAndMapError(url: String, e: Exception): WebResponse {
        log.error("Could not request $url", e)

        return WebResponse(false, error = e)
    }

}