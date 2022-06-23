package com.haallo.ui.call

import android.app.Service
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Binder
import android.os.IBinder

class AudioService : Service() {

    companion object {
        private val TAG = AudioService::class.simpleName!!
    }

    // Binder given to clients
    private val mBinder = LocalBinder()

    private var ringtone: Ringtone? = null

    override fun onCreate() {
        super.onCreate()
        ringtone = RingtoneManager.getRingtone(
            this,
            Uri.parse("android.resource://$packageName/raw/ringtone_original")
        )
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        BundleUtils.printIntentData(TAG, intent)
        return START_NOT_STICKY
    }

    private fun playRingtone() {
        ringtone?.play()
    }

    private fun stopRingtone() {
        ringtone?.stop()
    }

    override fun onBind(intent: Intent): IBinder {
        playRingtone()
        return mBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stopRingtone()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        stopRingtone()
    }

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): AudioService = this@AudioService
    }
}