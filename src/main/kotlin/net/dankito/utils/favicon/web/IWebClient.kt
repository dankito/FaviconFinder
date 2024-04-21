package net.dankito.utils.favicon.web


interface IWebClient {

    fun get(url: String, requestDesktopWebsite: Boolean = false): WebResponse

    fun head(url: String, requestDesktopWebsite: Boolean = false): WebResponse

}