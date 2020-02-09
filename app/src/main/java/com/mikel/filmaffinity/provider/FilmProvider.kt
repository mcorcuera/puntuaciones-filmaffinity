package com.mikel.filmaffinity.provider

import com.google.firebase.perf.metrics.AddTrace
import com.mikel.filmaffinity.FilmInformation
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URL

abstract class FilmProvider(private val host: String) {

    fun supports(url: String): Boolean {
        try {
            val parsedUrl = URL(url)

            if (parsedUrl.host.endsWith(host)) {
                return true
            }

        } catch (e: MalformedURLException) {
            return false
        }

        return false
    }


    @AddTrace(name = "find_video")
    fun getInformation(url: String): FilmInformation? {
        if (!supports(url)) return null

        val doc = prepareConnection(Jsoup.connect(url))
            .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36").get()
        return parseResults(doc, url)
    }

    abstract fun parseResults(doc: Document, url: String): FilmInformation?

    open fun prepareConnection(connection: Connection): Connection {
        return connection
    }

}