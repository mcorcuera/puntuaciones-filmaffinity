package com.mikel.filmaffinity.provider.impl

import com.mikel.filmaffinity.FilmInformation
import com.mikel.filmaffinity.VideoLanguage
import com.mikel.filmaffinity.VideoType
import org.junit.Test
import java.net.URL

class PrimeVideoFilmProviderTest : AbstractFilmProviderTest() {
    override val underTest = PrimeVideoFilmProvider()

    @Test
    fun `parse series`() {
        assertResults(
            "/html/prime-video/series.html", "https://app.primevideo.com/detail?gti=amzn1.dv.gti.c0ba27d5-f99c-c286-bfd2-d09f501ef6bf&ref_=atv_dp_share_seas&r=web", FilmInformation(
                title = "The Challenge: ETA",
                year = 2020,
                type = VideoType.show,
                language = VideoLanguage.en,
                provider = "primevideo",
                url = URL("https://app.primevideo.com/detail?gti=amzn1.dv.gti.c0ba27d5-f99c-c286-bfd2-d09f501ef6bf&ref_=atv_dp_share_seas&r=web")
            )
        )
    }

    @Test
    fun `parse series multiple seasons`() {
        assertResults(
            "/html/prime-video/series_multiple_seasons.html", "https://app.primevideo.com/detail?gti=amzn1.dv.gti.22b029a5-db0a-5ee4-90a8-eb727cba082f&ref_=atv_dp_share_seas&r=web", FilmInformation(
                title = "The Office",
                year = 2005,
                type = VideoType.show,
                language = VideoLanguage.en,
                provider = "primevideo",
                url = URL("https://app.primevideo.com/detail?gti=amzn1.dv.gti.22b029a5-db0a-5ee4-90a8-eb727cba082f&ref_=atv_dp_share_seas&r=web")
            )
        )
    }

    @Test
    fun `parse movies`() {
        assertResults(
            "/html/prime-video/movie.html", "https://app.primevideo.com/detail?gti=amzn1.dv.gti.c0ba27d5-f99c-c286-bfd2-d09f501ef6bf&ref_=atv_dp_share_seas&r=web", FilmInformation(
                title = "Shrek",
                year = 2001,
                type = VideoType.movie,
                language = VideoLanguage.en,
                provider = "primevideo",
                url = URL("https://app.primevideo.com/detail?gti=amzn1.dv.gti.c0ba27d5-f99c-c286-bfd2-d09f501ef6bf&ref_=atv_dp_share_seas&r=web")
            )
        )
    }
}