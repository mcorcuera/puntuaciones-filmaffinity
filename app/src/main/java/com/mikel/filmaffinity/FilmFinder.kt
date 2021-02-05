package com.mikel.filmaffinity

import com.mikel.filmaffinity.provider.FilmProvider
import com.mikel.filmaffinity.provider.impl.FilminFilmProvider
import com.mikel.filmaffinity.provider.impl.NetflixFilmProvider
import com.mikel.filmaffinity.provider.impl.PrimeVideoFilmProvider

class FilmFinder {
    companion object {
        val instance = FilmFinder()
    }

    private val providers: List<FilmProvider> = listOf(
        NetflixFilmProvider(),
        FilminFilmProvider(),
        PrimeVideoFilmProvider()
    )

    fun supports(url: String): Boolean {
        return getProvider(url) != null
    }

    fun getFilmInformation(url: String): FilmInformation? {
        val provider = getProvider(url) ?: return null

        return provider.getInformation(url)
    }

    private fun getProvider(url: String): FilmProvider? {
        return providers.firstOrNull { it.supports(url) }
    }

}