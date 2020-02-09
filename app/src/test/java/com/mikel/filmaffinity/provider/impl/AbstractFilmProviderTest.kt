package com.mikel.filmaffinity.provider.impl

import com.mikel.filmaffinity.FilmInformation
import com.mikel.filmaffinity.provider.FilmProvider
import org.jsoup.Jsoup
import org.junit.Assert.*

abstract class AbstractFilmProviderTest {

    abstract val underTest: FilmProvider

    fun assertResults(file: String, url: String, result: FilmInformation) {
        val htmlFile =
            AbstractFilmProviderTest::class.java.getResource(file)?.readText()
        val doc = Jsoup.parse(htmlFile)
        val information = underTest.parseResults(doc, url)!!

        assertEquals(result, information)

    }
}