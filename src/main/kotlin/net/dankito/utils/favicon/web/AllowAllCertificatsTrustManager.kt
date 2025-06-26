package net.dankito.utils.favicon.web

import net.codinux.log.logger
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

class AllowAllCertificatsTrustManager : X509TrustManager {

  override fun checkClientTrusted(x509Certificates: Array<X509Certificate>, s: String) {

  }

  override fun checkServerTrusted(x509Certificates: Array<X509Certificate>, s: String) {

  }

  fun isClientTrusted(chain: Array<X509Certificate?>?): Boolean {
    return true
  }

  fun isServerTrusted(chain: Array<X509Certificate?>?): Boolean {
    return true
  }

  override fun getAcceptedIssuers(): Array<X509Certificate> {
    return Companion.acceptedIssuers
  }

  companion object {
    private val trustManagers = arrayOf(AllowAllCertificatsTrustManager())

    private val acceptedIssuers = arrayOf<X509Certificate>()

    private val log by logger()

    fun allowAllCertificates() {
      HttpsURLConnection.setDefaultHostnameVerifier { arg0, arg1 -> true }

      try {
        val context = SSLContext.getInstance("TLS")
        context.init(null, trustManagers, SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(context.socketFactory)
      } catch (e: Exception) {
        log.error(e) { "Could not allow all SSL connections" }
      }
    }
  }
}