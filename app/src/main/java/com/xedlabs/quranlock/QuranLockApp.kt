package com.xedlabs.quranlock

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class QuranLockApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_SERVICE_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_desc)
                setShowBadge(false)
            }

            val wordsChannel = NotificationChannel(
                CHANNEL_WORDS_ID,
                "Quran Word Cards",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Shows Quranic word upon phone unlock"
                enableVibration(true)
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(serviceChannel)
            notificationManager.createNotificationChannel(wordsChannel)
        }
    }

    companion object {
        const val CHANNEL_SERVICE_ID = "quran_lock_service_channel"
        const val CHANNEL_WORDS_ID = "quran_lock_words_channel"
    }
}
