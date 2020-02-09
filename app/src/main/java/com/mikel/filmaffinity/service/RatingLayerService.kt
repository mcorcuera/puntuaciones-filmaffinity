package com.mikel.filmaffinity.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.mikel.filmaffinity.FilmaffinityRatting
import com.mikel.filmaffinity.PermissionChecker
import com.mikel.filmaffinity.overlay.RatingLayer


class RatingLayerService : Service() {

    private var ratingLayer: RatingLayer? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        if (PermissionChecker(this).isRequiredPermissionGranted) {
            ratingLayer = RatingLayer(this)
        } else {
            //TODO: Show notification to add permission
        }
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        ratingLayer?.let {
            if (intent != null && intent.extras != null) {
                val loading = intent.getBooleanExtra("loading", false)
                val stopLoading = intent.getBooleanExtra("stopLoading", false)
                val rating = intent.getParcelableExtra<FilmaffinityRatting>("rating")

                if (loading) {
                    it.startLoading()
                }

                if (stopLoading) {
                    it.hide()
                }

                if (rating != null) {
                    it.setRating(rating)
                }
            }
        }

        return START_STICKY
    }


    override fun onDestroy() {
        destroyLayer()
        super.onDestroy()
    }

    private fun destroyLayer() {
        ratingLayer?.let {
            if (!it.destroyed) {
                it.destroy()
            }
        }

    }
}

