package com.mikel.filmaffinity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.mikel.filmaffinity.service.OnCopyService


class Autostart : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return
        }
        Log.i("Autostart", "Autostart")
        val checker = PermissionChecker(context)
        if (checker.isRequiredPermissionGranted) {
            val copyIntent = Intent(context, OnCopyService::class.java)
            copyIntent.putExtra("autostart", true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(copyIntent)
            } else {
                context.startService(copyIntent)
            }
        }
    }
}

