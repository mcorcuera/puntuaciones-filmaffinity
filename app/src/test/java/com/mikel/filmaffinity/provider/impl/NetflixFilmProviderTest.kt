package com.mikel.filmaffinity.provider.impl

import com.mikel.filmaffinity.FilmInformation
import com.mikel.filmaffinity.VideoLanguage
import com.mikel.filmaffinity.VideoType
import org.junit.Test
import java.net.URL

class NetflixFilmProviderTest : AbstractFilmProviderTest() {
    override val underTest = NetflixFilmProvider()

    @Test
    fun `parse series`() {
        assertResults(
            "/html/netflix/series.html", "https://www.netflix.com/", FilmInformation(
                title = "The Crown",
                year = 2016,
                type = VideoType.show,
                language = VideoLanguage.en,
                url = URL("https://www.netflix.com/"),
                provider = "netflix"
            )
        )
    }

    @Test
    fun `parse movies`() {
        assertResults(
            "/html/netflix/movie.html", "https://www.netflix.com/", FilmInformation(
                title = "Titanic",
                year = 1997,
                type = VideoType.movie,
                language = VideoLanguage.en,
                url = URL("https://www.netflix.com/"),
                provider = "netflix"
            )
        )
    }
}