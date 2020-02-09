package com.mikel.filmaffinity.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

interface OnHomePressedListener {
    fun onShouldClose()
}

class CloseWatcher(private val context: Context) {
    private val intentFilter: IntentFilter = IntentFilter()
    private var listener: OnHomePressedListener? = null
    private var receiver: InnerReceiver? = null
    private val receiverLock = Any()

    fun setOnHomePressedListener(listener: OnHomePressedListener?) {
        this.listener = listener
    }

    fun startWatch() {
        synchronized(receiverLock) {
            if (receiver != null) {
                stopWatch()
            }
            receiver = InnerReceiver()
            context.registerReceiver(receiver, intentFilter)
        }
    }

    fun stopWatch() {
        synchronized(receiverLock) {
            if (receiver != null) {
                context.unregisterReceiver(receiver)
                receiver = null
            }
        }
    }

    internal inner class InnerReceiver : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            if (listener != null) {
                listener!!.onShouldClose()
            }
        }
    }

    init {
        intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
        intentFilter.addAction(Intent.ACTION_ALL_APPS)
        intentFilter.addAction(Intent.ACTION_CALL)
        intentFilter.addAction(Intent.ACTION_CONFIGURATION_CHANGED)
    }
}