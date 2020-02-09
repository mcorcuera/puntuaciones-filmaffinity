package com.mikel.filmaffinity.provider.impl

import com.mikel.filmaffinity.FilmInformation
import com.mikel.filmaffinity.VideoLanguage
import com.mikel.filmaffinity.VideoType
import org.junit.Test
import java.net.URL

class FilminFilmProviderTest : AbstractFilmProviderTest() {
    override val underTest = FilminFilmProvider()

    @Test
    fun `parse series`() {
        assertResults(
            "/html/filmin/series.html", "https://www.filmin.es/serie/adult-material", FilmInformation(
                title = "Adult Material",
                year = 2020,
                type = VideoType.show,
                language = VideoLanguage.es,
                url = URL("https://www.filmin.es/serie/adult-material"),
                provider = "filmin"
            )
        )
    }

    @Test
    fun `parse movies`() {
        assertResults(
            "/html/filmin/movie.html", "https://www.filmin.es/pelicula/persepolis", FilmInformation(
                title = "Persépolis",
                year = 2007,
                type = VideoType.movie,
                language = VideoLanguage.es,
                url = URL("https://www.filmin.es/pelicula/persepolis"),
                provider = "filmin"
            )
        )
    }
}