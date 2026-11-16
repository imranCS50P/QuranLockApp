package com.xedlabs.quranlock.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xedlabs.quranlock.R
import com.xedlabs.quranlock.databinding.ActivityMainBinding
import com.xedlabs.quranlock.preferences.PreferenceManager
import com.xedlabs.quranlock.repository.WordRepository
import com.xedlabs.quranlock.service.QuranForegroundService
import com.xedlabs.quranlock.service.WordOverlayService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: WordRepository
    private lateinit var prefManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = WordRepository(this)
        prefManager = PreferenceManager(this)

        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        updateDashboardUI()
    }

    private fun setupListeners() {
        binding.switchUnlockService.setOnCheckedChangeListener { _, isChecked ->
            prefManager.isServiceEnabled = isChecked
            if (isChecked) {
                QuranForegroundService.startService(this)
                Toast.makeText(this, "Screen unlock learning activated!", Toast.LENGTH_SHORT).show()
            } else {
                QuranForegroundService.stopService(this)
                Toast.makeText(this, "Screen unlock learning paused.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBrowseWords.setOnClickListener {
            startActivity(Intent(this, WordsListActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.btnTestOverlay.setOnClickListener {
            testOverlayFeature()
        }

        binding.cardCurrentWord.setOnClickListener {
            val currentWord = repository.getCurrentWord()
            if (currentWord != null) {
                WordOverlayService.showWordOverlay(this, currentWord.id)
            }
        }
    }

    private fun updateDashboardUI() {
        // Update Switch State
        binding.switchUnlockService.isChecked = prefManager.isServiceEnabled
        if (prefManager.isServiceEnabled) {
            QuranForegroundService.startService(this)
        }

        // Update Progress
        val memorized = repository.getMemorizedCount()
        val total = repository.getTotalCount()
        val percentage = if (total > 0) (memorized * 100) / total else 0

        binding.tvProgressText.text = "$memorized / $total ($percentage%)"
        binding.progressMastered.progress = percentage

        // Update Current Word Card
        val word = repository.getCurrentWord()
        if (word != null) {
            binding.tvMainWordNumber.text = "Word ${word.id} / $total"
            binding.tvMainFrequency.text = "${word.frequency} times"
            binding.tvMainArabic.text = word.arabic
            binding.tvMainTransliteration.text = word.transliteration
            binding.tvMainUrdu.text = word.urduMeaning
        }
    }

    private fun testOverlayFeature() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Toast.makeText(
                this,
                "Please grant 'Draw over other apps' permission in Settings first",
                Toast.LENGTH_LONG
            ).show()
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                android.net.Uri.parse("package:$packageName")
            )
            startActivity(intent)
        } else {
            val word = repository.getCurrentWord()
            if (word != null) {
                WordOverlayService.showWordOverlay(this, word.id)
            }
        }
    }
}
