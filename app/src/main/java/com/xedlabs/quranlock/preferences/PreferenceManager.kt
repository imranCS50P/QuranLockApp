package com.xedlabs.quranlock.preferences

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var isServiceEnabled: Boolean
        get() = prefs.getBoolean(KEY_SERVICE_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SERVICE_ENABLED, value).apply()

    var currentWordIndex: Int
        get() = prefs.getInt(KEY_CURRENT_WORD_INDEX, 0)
        set(value) = prefs.edit().putInt(KEY_CURRENT_WORD_INDEX, value).apply()

    var displayMode: String
        get() = prefs.getString(KEY_DISPLAY_MODE, MODE_OVERLAY) ?: MODE_OVERLAY
        set(value) = prefs.edit().putString(KEY_DISPLAY_MODE, value).apply()

    var autoCloseSeconds: Int
        get() = prefs.getInt(KEY_AUTO_CLOSE_SEC, 8)
        set(value) = prefs.edit().putInt(KEY_AUTO_CLOSE_SEC, value).apply()

    var intervalMinutes: Int
        get() = prefs.getInt(KEY_INTERVAL_MINUTES, 0) // 0 means every unlock
        set(value) = prefs.edit().putInt(KEY_INTERVAL_MINUTES, value).apply()

    var lastUnlockTimestamp: Long
        get() = prefs.getLong(KEY_LAST_UNLOCK_TIME, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_UNLOCK_TIME, value).apply()

    fun getMemorizedWordIds(): MutableSet<String> {
        return prefs.getStringSet(KEY_MEMORIZED_IDS, HashSet())?.toMutableSet() ?: mutableSetOf()
    }

    fun setWordMemorized(wordId: Int, memorized: Boolean) {
        val currentSet = getMemorizedWordIds()
        val idStr = wordId.toString()
        if (memorized) {
            currentSet.add(idStr)
        } else {
            currentSet.remove(idStr)
        }
        prefs.edit().putStringSet(KEY_MEMORIZED_IDS, currentSet).apply()
    }

    fun isWordMemorized(wordId: Int): Boolean {
        return getMemorizedWordIds().contains(wordId.toString())
    }

    fun resetProgress() {
        prefs.edit()
            .remove(KEY_MEMORIZED_IDS)
            .putInt(KEY_CURRENT_WORD_INDEX, 0)
            .apply()
    }

    companion object {
        private const val PREF_NAME = "quran_lock_preferences"
        private const val KEY_SERVICE_ENABLED = "key_service_enabled"
        private const val KEY_CURRENT_WORD_INDEX = "key_current_word_index"
        private const val KEY_MEMORIZED_IDS = "key_memorized_ids"
        private const val KEY_DISPLAY_MODE = "key_display_mode"
        private const val KEY_AUTO_CLOSE_SEC = "key_auto_close_sec"
        private const val KEY_INTERVAL_MINUTES = "key_interval_minutes"
        private const val KEY_LAST_UNLOCK_TIME = "key_last_unlock_time"

        const val MODE_OVERLAY = "OVERLAY"
        const val MODE_NOTIFICATION = "NOTIFICATION"
    }
}
