package com.mikel.filmaffinity.provider.impl

import com.google.firebase.perf.metrics.AddTrace
import com.mikel.filmaffinity.FilmInformation
import com.mikel.filmaffinity.VideoLanguage
import com.mikel.filmaffinity.VideoType
import com.mikel.filmaffinity.provider.FilmProvider
import org.jsoup.Connection
import org.jsoup.nodes.Document
import java.lang.Integer.parseInt
import java.net.URL


class NetflixFilmProvider : FilmProvider("netflix.com") {


    override fun prepareConnection(url: String): Connection {
        return super.prepareConnection(url).header("Accept-Language", "en")
    }

    @AddTrace(name = "find_video")
    override fun parseResults(doc: Document, url: String): FilmInformation? {

        val title = doc.getElementsByClass("title-title")[0].text()
        val year = parseInt(doc.getElementsByClass("item-year")[0].text())
        val type = if (doc.getElementById("section-seasons-and-episodes") != null)
            VideoType.show else VideoType.movie

        return FilmInformation(
            title = title,
            year = year,
            type = type,
            url = URL(url),
            language = VideoLanguage.en,
            provider = "netflix"
        )
    }
}