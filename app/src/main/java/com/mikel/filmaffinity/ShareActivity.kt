package com.mikel.filmaffinity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.mikel.filmaffinity.service.FindRatingService
import com.mikel.filmaffinity.utils.extractUrl

class ShareActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent?.action == Intent.ACTION_SEND && intent?.type == "text/plain") {
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                val url = extractUrl(it)
                if (url != null && FilmFinder.instance.supports(url)) {
                    val findIntent = Intent(this, FindRatingService::class.java)
                    findIntent.putExtra("url", url)
                    startService(findIntent)

                }
            }
        }
        finish()
    }
}
