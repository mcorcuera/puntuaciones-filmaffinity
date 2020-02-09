package com.mikel.filmaffinity.provider.impl

import com.google.firebase.perf.metrics.AddTrace
import com.mikel.filmaffinity.FilmInformation
import com.mikel.filmaffinity.VideoLanguage
import com.mikel.filmaffinity.VideoType
import com.mikel.filmaffinity.provider.FilmProvider
import org.jsoup.nodes.Document
import java.lang.Integer.parseInt
import java.net.URL


class FilminFilmProvider : FilmProvider("filmin.es") {

    @AddTrace(name = "find_video")
    override fun parseResults(doc: Document, url: String): FilmInformation? {

        val title = doc.select(".display-1[itemprop=\"name\"]")[0].text().trim()
        val year = parseInt(doc.select("[itemprop=\"dateCreated\"]")[0].text())
        val type = if (url.contains("/serie/")) VideoType.show else VideoType.movie

        return FilmInformation(
            title = title,
            year = year,
            type = type,
            url = URL(url),
            language = VideoLanguage.es,
            provider = "filmin"
        )
    }
}