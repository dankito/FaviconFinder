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
            val connection = URL(url).openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.instanceFollowRedirects = true

            connection.connect()

            val inputStream = connection.getInputStream().buffered()
            val downloadedBytes = inputStream.readBytes()

            inputStream.close()

            val status = connection.responseCode

            return WebResponse(status in 200..299, status, downloadedBytes)
        } catch (e: Exception) {
            log.error("Could not request $url", e)

            return WebResponse(false, error = e)
        }
    }

}