package com.mikel.filmaffinity.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.metrics.AddTrace
import com.mikel.filmaffinity.FilmFinder
import com.mikel.filmaffinity.FilmaffinityRatting
import com.mikel.filmaffinity.R
import com.mikel.filmaffinity.api.FilmaffinityApi


class FindRatingService : Service() {
    private val filmaffinityApi: FilmaffinityApi =
        FilmaffinityApi()
    private val uiHandler: Handler = Handler(Looper.getMainLooper())


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            return START_NOT_STICKY
        }


        val url = intent.getStringExtra("url");

        if (url != null) {
            startLoadingIntent()
            try {
                Thread {
                    Log.i("FindRatingService", "Start finding $url")
                    try {
                        val rating = findRating(url)
                        if (rating != null) {
                            Log.i("FindRatingService", "Found rating $rating")
                            startViewIntent(rating)
                        } else {
                            startStopLoadingIntent()
                            showErrorMessage(R.string.rating_not_found)
                        }
                    } catch (e: Exception) {
                        startStopLoadingIntent()
                        showErrorMessage(R.string.find_rating_error_toast)
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                }.start()
            } catch (e: Exception) {
                startStopLoadingIntent()
                showErrorMessage(R.string.find_rating_error_toast)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }


        return START_NOT_STICKY
    }

    private fun showErrorMessage(resource: Int) {
        uiHandler.post {
            Toast.makeText(this, resource, LENGTH_SHORT)
                .show()
        }
    }

    private fun startViewIntent(rating: FilmaffinityRatting?) {
        val viewIntent =
            Intent(this, RatingLayerService::class.java)
        viewIntent.putExtra("rating", rating)
        startService(viewIntent)
    }

    private fun startLoadingIntent() {
        val viewIntent =
            Intent(this, RatingLayerService::class.java)
        viewIntent.putExtra("loading", true)
        startService(viewIntent)
    }

    private fun startStopLoadingIntent() {
        val viewIntent =
            Intent(this, RatingLayerService::class.java)
        viewIntent.putExtra("stopLoading", true)
        startService(viewIntent)
    }

    @AddTrace(name = "find_video_and_rating")
    private fun findRating(url: String): FilmaffinityRatting? {
        val video = FilmFinder.instance.getFilmInformation(url)

        if (video != null) {
            Log.i("FindRatingService", "Found video $video")

            FirebaseAnalytics.getInstance(this).logEvent("find_rating", bundleOf(
                Pair("provider", video.provider),
                Pair("video_type", video.type.name)
            ))

            val rating = filmaffinityApi.findRating(video)

            if (rating != null) {
                Log.i("FindRatingService", "Found rating $rating")
                return rating
            }
        }

        return null
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}