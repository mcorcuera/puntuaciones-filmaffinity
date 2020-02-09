package com.mikel.filmaffinity.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class UrlKtTest {

    @Test
    fun extractUrlTest() {
        val text1 =
            """Hey I’m watching The Challenge: ETA - Season 1. Check it out now on Prime Video!
        https://app.primevideo.com/detail?gti=amzn1.dv.gti.c0ba27d5-f99c-c286-bfd2-d09f501ef6bf&ref_=atv_dp_share_seas&r=web"""

        assertEquals(
            "https://app.primevideo.com/detail?gti=amzn1.dv.gti.c0ba27d5-f99c-c286-bfd2-d09f501ef6bf&ref_=atv_dp_share_seas&r=web",
            extractUrl(text1)
        )

        val text2 = """Viendo https://filmin.es/serie/adult-material en @filmin"""

        assertEquals("https://filmin.es/serie/adult-material", extractUrl(text2))

    }
}