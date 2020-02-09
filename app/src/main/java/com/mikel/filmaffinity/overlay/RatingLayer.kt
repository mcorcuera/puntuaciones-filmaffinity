package com.mikel.filmaffinity.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.*
import android.webkit.WebView
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.github.guilhe.views.CircularProgressView
import com.mikel.filmaffinity.FilmaffinityRatting
import com.mikel.filmaffinity.R
import com.mikel.filmaffinity.service.CloseWatcher
import com.mikel.filmaffinity.service.OnHomePressedListener
import java.text.NumberFormat
import java.util.*
import kotlin.math.PI

private val OVERLAY_TYPE =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    } else {
        WindowManager.LayoutParams.TYPE_PHONE
    }

class RatingLayer(
    context: Context
) : View(context), OnHomePressedListener {

    var destroyed: Boolean = false
    var hidden: Boolean = true

    private val windowManager: WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val layoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val ratingLayout: FrameLayout = FrameLayout(context)
    private val detailsLayout: FrameLayout = object : FrameLayout(context) {
        override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
            if (event?.keyCode == KeyEvent.KEYCODE_BACK) {
                closeDetails()
                return true
            }
            return super.dispatchKeyEvent(event)
        }
    }
    private val closeWatcher = CloseWatcher(context)
    private var closeTimer: Timer? = null

    init {
        Log.i("RatingLayer", "Start layouting")
        addDetailsToWindowManager()
        addRatingToWindowManager()
        Log.i("RatingLayer", "Finish layouting")
        closeWatcher.setOnHomePressedListener(this)
    }

    fun setRating(rating: FilmaffinityRatting) {
        hide {
            hidden = false
            closeWatcher.startWatch()
            startCloseTimer()
            setRatingText(rating)
            setRatingUrl(rating)
            setProgress(rating)
            setLinkListener(rating)
            showRatingLayout()
            hideLoading()

            ratingLayout.setOnClickListener {
                toggleDetails()
            }

        }
    }

    fun startLoading() {
        hide {
            hidden = false
            showRatingLayout()
            showLoading()
        }
    }

    fun hide(function: (() -> Unit)? = null) {
        closeWatcher.stopWatch()
        stopCloseTimer()
        if (!hidden) {
            hidden = true
            ratingLayout.setOnClickListener(null)
            ratingLayout.animate().scaleYBy(0.5f).scaleXBy(0.5f).alpha(0f).setDuration(200)
                .withEndAction {
                    ratingLayout.visibility = GONE
                    ratingLayout.scaleY = 1.0f
                    ratingLayout.scaleX = 1.0f
                    ratingLayout.alpha = 1.0f
                    if (function != null) {
                        function()
                    }
                }
            detailsLayout.animate().scaleXBy(0.5f).scaleYBy(0.5f).alpha(0f).setDuration(200)
                .withEndAction {
                    detailsLayout.visibility = GONE
                    detailsLayout.scaleY = 1.0f
                    detailsLayout.scaleX = 1.0f
                    detailsLayout.alpha = 1.0f
                }
        } else {
            if (function != null) {
                function()
            }
        }
    }

    private fun showLoading() {
        val ratingContent = ratingLayout.findViewById<View>(R.id.ratingContent)
        ratingContent.alpha = 0.0f
        val progressBar = ratingLayout.findViewById<ProgressBar>(R.id.progressBar)
        progressBar.alpha = 1.0f
        progressBar.visibility = VISIBLE
    }

    private fun hideLoading() {
        val progressBar = ratingLayout.findViewById<ProgressBar>(R.id.progressBar)
        val ratingContent = ratingLayout.findViewById<View>(R.id.ratingContent)

        ratingContent.alpha = 0f
        ratingContent.rotation = -45f

        ratingContent.animate().rotation(0f).alpha(1f).scaleX(1f)
            .scaleY(1f).setDuration(200)

        progressBar.animate().alpha(0.0f).setDuration(200).withEndAction {
            progressBar.visibility = GONE
        }
    }

    private fun showRatingLayout() {
        if (ratingLayout.visibility != VISIBLE) {
            ratingLayout.visibility = VISIBLE
            ratingLayout.alpha = 0f
            ratingLayout.scaleX = 0f
            ratingLayout.scaleY = 0f
            ratingLayout.animate().alpha(1f).scaleX(1f)
                .scaleY(1f).setDuration(200)
        }
    }


    private fun addRatingToWindowManager() {
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            OVERLAY_TYPE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.END or Gravity.TOP
        windowManager.addView(ratingLayout, params)

        layoutInflater.inflate(R.layout.filmaffinity_rating, ratingLayout)

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun addDetailsToWindowManager() {
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            OVERLAY_TYPE,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    or WindowManager.LayoutParams.FLAG_FULLSCREEN
                    or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.END or Gravity.TOP
        windowManager.addView(detailsLayout, params)

        detailsLayout.visibility = GONE

        layoutInflater.inflate(R.layout.filmaffinity_details, detailsLayout)
        val webView = detailsLayout.findViewById<WebView>(R.id.web_view)

        webView.settings.javaScriptEnabled = true

        val closeButton = detailsLayout.findViewById<Button>(R.id.close_button)

        closeButton.setOnClickListener {
            hide()
        }
    }



    private fun setRatingUrl(rating: FilmaffinityRatting) {
        val webView = detailsLayout.findViewById<WebView>(R.id.web_view)
        webView.loadUrl(rating.url.toString())
    }

    private fun setLinkListener(rating: FilmaffinityRatting) {
        val linkButton = detailsLayout.findViewById<Button>(R.id.link_button)
        linkButton.setOnClickListener {
            openLink(rating)
            hide()
        }
    }

    private fun setRatingText(rating: FilmaffinityRatting) {
        val ratingValue = rating.rating
        val textView = ratingLayout.findViewById<TextView>(R.id.ratingText)

        if (ratingValue != null) {
            val format = NumberFormat.getNumberInstance()
            textView.text = format.format(ratingValue)
        } else {
            textView.text = "-"
        }
    }

    private fun setProgress(rating: FilmaffinityRatting) {
        val ratings = rating.ratingOverview;
        val progressView = ratingLayout.findViewById<CircularProgressView>(R.id.progress_circle)

        if (ratings == null) {
            progressView.setProgress(100.0f, false)
            return
        }

        val items: ArrayList<Pair<Float, Int>> = arrayListOf()

        val positive = ratings.positive.percentage
        val neutral = ratings.neutral.percentage
        val negative = ratings.negative.percentage

        if (positive > 0) {
            items.add(Pair(positive, ContextCompat.getColor(context,
                R.color.colorPositive
            )))
        }

        if (neutral > 0) {
            items.add(Pair(neutral, ContextCompat.getColor(context,
                R.color.colorNeutral
            )))
        }

        if (negative > 0) {
            items.add(Pair(negative, ContextCompat.getColor(context,
                R.color.colorNegative
            )))
        }

        val d = 74.0
        val c = d * PI
        val spacing: Float = (9.0 / c * 100.0).toFloat()
        val total: Float = 100 - (if (items.size > 1) items.size else 0) * spacing

        progressView.setProgress(
            items.flatMap { listOf(it.first * total, spacing) }.take(items.size * 2 - 1).toList(),
            items.flatMap { listOf(it.second, Color.TRANSPARENT) }.take(items.size * 2 - 1).toList()
        )
    }


    private fun startCloseTimer() {
        closeTimer = Timer()
        closeTimer!!.schedule(object : TimerTask() {
            override fun run() {
                if (!isDetailsOpen()) {
                    hide()
                    cancel()
                }
            }
        }, 10000)
    }

    private fun stopCloseTimer() {
        if (closeTimer != null) {
            closeTimer!!.cancel()
            closeTimer = null
        }
    }

    private fun isDetailsOpen(): Boolean {
        return detailsLayout.visibility != GONE
    }

    private fun openLink(rating: FilmaffinityRatting) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(rating.url.toString()))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }

    private fun toggleDetails() {
        if (!isDetailsOpen()) {
            openDetails()
        } else {
            closeDetails()
        }
    }

    private fun closeDetails() {
        detailsLayout.animate().setDuration(200).alpha(0.0f).withEndAction {
            detailsLayout.visibility = GONE
        }
        startCloseTimer()
    }

    private fun openDetails() {
        detailsLayout.requestFocus()
        detailsLayout.alpha = 0.0f
        detailsLayout.visibility = VISIBLE
        detailsLayout.animate().setDuration(200).alpha(1.0f)
        stopCloseTimer()
    }

    fun destroy() {
        if (!destroyed) {
            hide()
        }
    }

    override fun onShouldClose() {
        hide()
    }
}