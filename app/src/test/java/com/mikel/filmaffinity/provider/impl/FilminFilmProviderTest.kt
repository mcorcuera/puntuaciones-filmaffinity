package com.mikel.filmaffinity.provider.impl

import com.mikel.filmaffinity.FilmInformation
import com.mikel.filmaffinity.VideoLanguage
import com.mikel.filmaffinity.VideoType
import junit.framework.Assert.assertEquals
import org.junit.Assert
import org.junit.Test
import java.net.URL

class FilminFilmProviderTest : AbstractFilmProviderTest() {
    override val underTest = FilminFilmProvider()

    @Test
    fun `url replacing`() {
        val seriesApiUrl = underTest.getApiUrl("https://www.filmin.es/serie/exit")
        assertEquals("https://www.filmin.es/wapi/medias/serie/exit", seriesApiUrl)

        val movieApiUrl = underTest.getApiUrl("https://www.filmin.es/film/el-regalo")
        assertEquals("https://www.filmin.es/wapi/medias/film/el-regalo", movieApiUrl)

        val movieApiUrl2 = underTest.getApiUrl("https://www.filmin.es/pelicula/el-regalo")
        assertEquals("https://www.filmin.es/wapi/medias/film/el-regalo", movieApiUrl2)
    }

    @Test
    fun `parse series`() {
        assertResults(
            "/html/filmin/series.json", "https://www.filmin.es/serie/exit", FilmInformation(
                title = "Exit",
                year = 2019,
                type = VideoType.show,
                language = VideoLanguage.es,
                url = URL("https://www.filmin.es/serie/exit"),
                provider = "filmin"
            )
        )
    }

    @Test
    fun `parse movies`() {
        assertResults(
            "/html/filmin/movie.json", "https://www.filmin.es/pelicula/el-regalo", FilmInformation(
                title = "El regalo",
                year = 2015,
                type = VideoType.movie,
                language = VideoLanguage.es,
                url = URL("https://www.filmin.es/pelicula/el-regalo"),
                provider = "filmin"
            )
        )
    }
}