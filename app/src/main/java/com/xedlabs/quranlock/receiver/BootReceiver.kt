package com.xedlabs.quranlock.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.xedlabs.quranlock.preferences.PreferenceManager
import com.xedlabs.quranlock.service.QuranForegroundService

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            val prefManager = PreferenceManager(context)
            if (prefManager.isServiceEnabled) {
                QuranForegroundService.startService(context)
            }
        }
    }
}
