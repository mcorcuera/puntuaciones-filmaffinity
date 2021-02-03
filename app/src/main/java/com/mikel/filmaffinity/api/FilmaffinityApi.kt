package com.mikel.filmaffinity.api

import com.google.firebase.perf.metrics.AddTrace
import com.mikel.filmaffinity.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.lang.Float.parseFloat
import java.lang.Integer.parseInt
import java.net.URL

class FilmaffinityApi {

    @AddTrace(name = "find_rating")
    fun findRating(video: FilmInformation): FilmaffinityRatting? {

        val result = findItem(video) ?: return null

        return getFilmaffinityItem(result)
    }

    private fun getFilmaffinityItem(url: URL): FilmaffinityRatting {
        val doc = Jsoup.connect(url.toString()).get()
        val ratingElement = doc.getElementById("movie-rat-avg")
        val titleElement = doc.getElementById("main-title")
        val summaryElement = doc.selectFirst(".legend-wrapper")

        val rating = if(ratingElement != null) parseFloat(ratingElement.text().replace(",", ".")) else null
        val title = titleElement.text().trim()

        return FilmaffinityRatting(
            title = title,
            ratingOverview = parseRatingSummary(summaryElement),
            rating = rating,
            url = url,
            id = getFilmId(url)
        )

    }

    private fun parseRatingSummary(summaryElement: Element?): FilmaffinityRatingOverview? {
        if (summaryElement == null) {
            return null
        }
        var total = 0
        var positive = 0
        var neutral = 0
        var negative = 0

        val ratingElements = summaryElement.select(".leg")

        for (ratingElement in ratingElements) {
            val count = parseInt(ratingElement.text().trim())

            if (ratingElement.selectFirst(".pos") != null) {
                positive += count
            }

            if (ratingElement.selectFirst(".neu") != null) {
                neutral += count
            }

            if (ratingElement.selectFirst(".neg") != null) {
                negative += count
            }

            total += count
        }

        return FilmaffinityRatingOverview(
            positive = FilmaffinityOverviewItem(
                count = positive,
                percentage = positive.toFloat() / total
            ),
            neutral = FilmaffinityOverviewItem(
                count = neutral,
                percentage = neutral.toFloat() / total
            ),
            negative = FilmaffinityOverviewItem(
                count = negative,
                percentage = negative.toFloat() / total
            )
        )
    }

    private fun findItem(video: FilmInformation): URL? {
        val search = searchVideos(video)

        if (search.redirectURL != null) {
            return search.redirectURL
        }
        val results = search.results.filter { it.type == video.type && it.year == video.year }

        if (results.isEmpty()) return null

        val exactMatch = results.find { it.title.equals(video.title, ignoreCase = true) }

        if (exactMatch != null) {
            return exactMatch.url
        }

        return results[0].url
    }

    private fun searchVideos(film: FilmInformation): FilmaffinitySearch {
        val searchUrl = "https://www.filmaffinity.com/${film.language.name}/search.php?stype=title&stext=${film.title}"
        val doc = Jsoup.connect(searchUrl).get()
        if (!doc.location().contains("/search.php")) {
            return FilmaffinitySearch(
                redirectURL = URL(
                    doc.location().replace("/en/", "/es/")
                )
            )
        }
        val results = doc.select(".z-search .se-it").map {
            val titleElement = it.selectFirst(".mc-title > a")
            var title = titleElement.attr("title").trim()
            val url = URL(titleElement.attr("href").replace("/en/", "/es/"))
            val year = parseInt(it.selectFirst(".ye-w").text().trim())
            var type = VideoType.movie

            val seriesRegex =  """\(.*TV.*\)""".toRegex()
            if (title.contains(seriesRegex)) {
                title = title.replace(seriesRegex, "".trim())
                type = VideoType.show
            }

            FilmaffinitySearchResult(
                title = title,
                type = type,
                url = url,
                year = year,
                id = getFilmId(url)
            )
        }

        return FilmaffinitySearch(results)
    }

    private fun getFilmId(url: URL): String {
        return url.path.split("/").last().split(".").first().replace("film", "");
    }
}