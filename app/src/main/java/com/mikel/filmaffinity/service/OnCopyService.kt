package com.mikel.filmaffinity.service

import android.app.*
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.mikel.filmaffinity.FilmFinder
import com.mikel.filmaffinity.MainActivity
import com.mikel.filmaffinity.R
import com.mikel.filmaffinity.utils.extractUrl


private const val ONGOING_NOTIFICATION_ID = 1234

class OnCopyService : Service(), ClipboardManager.OnPrimaryClipChangedListener {

    private lateinit var clipBoardManager: ClipboardManager

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        Log.i("OnCopyService", "onCreate")
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("fa_netflix_permanent", "Filmaffinity service")
            } else {
                ""
            }

        clipBoardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }
        val notification: Notification =
            NotificationCompat.Builder(this, channelId)
                .setContentTitle("Puntuaciones de Filmaffinity")
                .setContentText("Esperando a que selecciones una serie o pelicula")
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentIntent(pendingIntent)
                .setTicker("Puntuaciones de Filmaffinity")
                .build()

        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initCopyListener()
        return START_STICKY
    }

    override fun onPrimaryClipChanged() {
        val primaryData = clipBoardManager.primaryClip;
        if (primaryData != null && primaryData.itemCount > 0 && primaryData.getItemAt(0).text != null) {
            Log.i("OnCopyService", "onPrimaryClipChanged")
            val copiedText = primaryData.getItemAt(0).text.toString()
            val validUrl = extractUrl(copiedText)?.let { FilmFinder.instance.supports(it) } ?: false
            if (validUrl) {
                val findIntent = Intent(this, FindRatingService::class.java)
                findIntent.putExtra("url", copiedText)
                startService(findIntent)
            }
        }
    }

    private fun initCopyListener() {
        clipBoardManager.removePrimaryClipChangedListener(this)
        clipBoardManager.addPrimaryClipChangedListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_MIN
        )
        chan.lockscreenVisibility = Notification.VISIBILITY_SECRET
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }
}