package com.xedlabs.quranlock.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xedlabs.quranlock.model.QuranWord
import com.xedlabs.quranlock.preferences.PreferenceManager
import java.io.InputStreamReader

class WordRepository(private val context: Context) {

    private val prefManager = PreferenceManager(context)
    private var cachedWords: List<QuranWord>? = null

    fun getAllWords(): List<QuranWord> {
        if (cachedWords == null) {
            cachedWords = loadWordsFromAssets()
        }
        val memorizedIds = prefManager.getMemorizedWordIds()
        cachedWords?.forEach { word ->
            word.isMemorized = memorizedIds.contains(word.id.toString())
        }
        return cachedWords ?: emptyList()
    }

    private fun loadWordsFromAssets(): List<QuranWord> {
        return try {
            val inputStream = context.assets.open("quran_words_500.json")
            val reader = InputStreamReader(inputStream, Charsets.UTF_8)
            val type = object : TypeToken<List<QuranWord>>() {}.type
            Gson().fromJson(reader, type)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun getNextWord(): QuranWord? {
        val words = getAllWords()
        if (words.isEmpty()) return null

        var currentIndex = prefManager.currentWordIndex
        if (currentIndex >= words.size) {
            currentIndex = 0
        }

        val word = words[currentIndex]
        // Advance index for the next unlock
        prefManager.currentWordIndex = (currentIndex + 1) % words.size
        return word
    }

    fun getCurrentWord(): QuranWord? {
        val words = getAllWords()
        if (words.isEmpty()) return null
        val currentIndex = prefManager.currentWordIndex.coerceIn(0, words.size - 1)
        return words[currentIndex]
    }

    fun toggleMemorized(word: QuranWord): Boolean {
        val newState = !word.isMemorized
        word.isMemorized = newState
        prefManager.setWordMemorized(word.id, newState)
        return newState
    }

    fun getMemorizedCount(): Int {
        return prefManager.getMemorizedWordIds().size
    }

    fun getTotalCount(): Int {
        return getAllWords().size
    }
}
