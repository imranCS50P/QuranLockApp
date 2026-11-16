package com.xedlabs.quranlock.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.xedlabs.quranlock.QuranLockApp
import com.xedlabs.quranlock.R
import com.xedlabs.quranlock.preferences.PreferenceManager
import com.xedlabs.quranlock.repository.WordRepository
import com.xedlabs.quranlock.service.WordOverlayService
import com.xedlabs.quranlock.ui.MainActivity

class ScreenUnlockReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_USER_PRESENT) {
            handleScreenUnlocked(context)
        }
    }

    private fun handleScreenUnlocked(context: Context) {
        val prefManager = PreferenceManager(context)
        if (!prefManager.isServiceEnabled) return

        // Check interval limit if configured
        val now = System.currentTimeMillis()
        val intervalMs = prefManager.intervalMinutes * 60 * 1000L
        if (intervalMs > 0 && (now - prefManager.lastUnlockTimestamp) < intervalMs) {
            return
        }
        prefManager.lastUnlockTimestamp = now

        val repository = WordRepository(context)
        val word = repository.getNextWord() ?: return

        // Decide display mode
        if (prefManager.displayMode == PreferenceManager.MODE_OVERLAY && canDrawOverlays(context)) {
            // Trigger Floating Card Overlay
            WordOverlayService.showWordOverlay(context, word.id)
        } else {
            // Display High-Priority Heads-Up Notification
            showWordNotification(context, word.arabic, word.transliteration, word.urduMeaning)
        }
    }

    private fun canDrawOverlays(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    private fun showWordNotification(
        context: Context,
        arabic: String,
        transliteration: String,
        urduMeaning: String
    ) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            2002,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, QuranLockApp.CHANNEL_WORDS_ID)
            .setContentTitle("$arabic ($transliteration)")
            .setContentText(urduMeaning)
            .setSmallIcon(android.R.drawable.ic_menu_agenda)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_WORD_ID, notification)
    }

    companion object {
        private const val NOTIFICATION_WORD_ID = 2001
    }
}
