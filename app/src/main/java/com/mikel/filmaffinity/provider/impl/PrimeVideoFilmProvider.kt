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


class PrimeVideoFilmProvider : FilmProvider("primevideo.com") {

    override fun prepareConnection(connection: Connection): Connection {

        val connectionResponse = connection.execute()
        val sessionCookie = connectionResponse.cookie("session-id")

        return connection
            .cookie("lc-main-av", "en_US")
            .cookie("session-id", sessionCookie)
    }

    @AddTrace(name = "find_video")
    override fun parseResults(doc: Document, url: String): FilmInformation? {

        val title = doc.select("[data-automation-id=\"title\"]")[0].text().trim()
        val year = parseReleaseYear(doc)
            ?: parseInt(doc.select("[data-automation-id=\"release-year-badge\"]")[0].text())
        val type =
            if (doc.getElementById("av-ep-episodes-0") != null) VideoType.show else VideoType.movie

        parseReleaseYear(doc)
        return FilmInformation(
            title = title,
            year = year,
            type = type,
            url = URL(url),
            language = VideoLanguage.en,
            provider = "primevideo"
        )
    }

    private fun parseReleaseYear(doc: Document): Int? {
        "".toRegex(setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE))
        val result = """"releaseYear"\s*:\s*(\d{4})""".toRegex().find(doc.toString())

        return result?.groups?.get(1)?.value?.let { parseInt(it) }

    }
}