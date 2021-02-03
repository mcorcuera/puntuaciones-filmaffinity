package com.mikel.filmaffinity.provider.impl

import com.google.firebase.perf.metrics.AddTrace
import com.google.gson.JsonParser
import com.mikel.filmaffinity.FilmInformation
import com.mikel.filmaffinity.VideoLanguage
import com.mikel.filmaffinity.VideoType
import com.mikel.filmaffinity.provider.FilmProvider
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL


class FilminFilmProvider : FilmProvider("filmin.es") {

    override fun prepareConnection(url: String): Connection {

        val connection = Jsoup.connect(getApiUrl(url))
        connection.header("Accept", " application/json, text/plain, */*")
        connection.header("X-Requested-With", "XMLHttpRequest")
        connection.header("Accept-Language", "es")
        connection.ignoreContentType(true)

        return connection
    }

    fun getApiUrl(url: String): String {
        var (filmType, filmId) = url.split("/").takeLast(2)

        if (filmType == "pelicula") {
            filmType = "film"
        }

        return "https://www.filmin.es/wapi/medias/$filmType/$filmId"
    }

    @AddTrace(name = "find_video")
    override fun parseResults(doc: Document, url: String): FilmInformation? {

        val json = JsonParser.parseString(doc.body().text()).asJsonObject
        val data = json["data"].asJsonObject
        val title = data["title"].asString
        val year = data["year"].asInt
        val dataUrl = data["url"].asString
        val type = if ("film" == data["type"].asString) VideoType.movie else VideoType.show

        return FilmInformation(
            title = title,
            year = year,
            type = type,
            url = URL(dataUrl),
            language = VideoLanguage.es,
            provider = "filmin"
        )
    }
}