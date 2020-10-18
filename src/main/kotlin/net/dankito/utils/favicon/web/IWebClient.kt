package net.dankito.utils.favicon.web


interface IWebClient {

    fun get(url: String): WebResponse

}