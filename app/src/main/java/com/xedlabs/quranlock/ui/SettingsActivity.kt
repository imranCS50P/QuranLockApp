package com.xedlabs.quranlock.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.xedlabs.quranlock.R
import com.xedlabs.quranlock.databinding.ActivitySettingsBinding
import com.xedlabs.quranlock.preferences.PreferenceManager
import com.xedlabs.quranlock.repository.WordRepository

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefManager: PreferenceManager
    private lateinit var repository: WordRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PreferenceManager(this)
        repository = WordRepository(this)

        setupToolbar()
        setupPermissionButtons()
        setupPreferencesUI()
    }

    override fun onResume() {
        super.onResume()
        updatePermissionButtonsState()
    }

    private fun setupToolbar() {
        binding.toolbarSettings.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupPermissionButtons() {
        binding.btnGrantOverlay.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            } else {
                Toast.makeText(this, "Permission already active on this OS version", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnGrantBattery.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                startActivity(intent)
            }
        }
    }

    private fun updatePermissionButtonsState() {
        // Overlay Permission
        val hasOverlay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
        if (hasOverlay) {
            binding.btnGrantOverlay.text = getString(R.string.granted)
            binding.btnGrantOverlay.isEnabled = false
        } else {
            binding.btnGrantOverlay.text = getString(R.string.grant_permission)
            binding.btnGrantOverlay.isEnabled = true
        }

        // Battery Optimization
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            val isIgnoring = powerManager.isIgnoringBatteryOptimizations(packageName)
            if (isIgnoring) {
                binding.btnGrantBattery.text = getString(R.string.granted)
                binding.btnGrantBattery.isEnabled = false
            } else {
                binding.btnGrantBattery.text = getString(R.string.grant_permission)
                binding.btnGrantBattery.isEnabled = true
            }
        }
    }

    private fun setupPreferencesUI() {
        // Display Mode
        if (prefManager.displayMode == PreferenceManager.MODE_NOTIFICATION) {
            binding.rbNotification.isChecked = true
        } else {
            binding.rbOverlay.isChecked = true
        }

        binding.rgDisplayMode.setOnCheckedChangeListener { _, checkedId ->
            prefManager.displayMode = if (checkedId == R.id.rbNotification) {
                PreferenceManager.MODE_NOTIFICATION
            } else {
                PreferenceManager.MODE_OVERLAY
            }
        }

        // Auto Close Timer
        when (prefManager.autoCloseSeconds) {
            5 -> binding.rbClose5.isChecked = true
            15 -> binding.rbClose15.isChecked = true
            0 -> binding.rbCloseManual.isChecked = true
            else -> binding.rbClose8.isChecked = true
        }

        binding.rgAutoClose.setOnCheckedChangeListener { _, checkedId ->
            prefManager.autoCloseSeconds = when (checkedId) {
                R.id.rbClose5 -> 5
                R.id.rbClose15 -> 15
                R.id.rbCloseManual -> 0
                else -> 8
            }
        }

        // Reset Progress
        binding.btnResetProgress.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Reset Learning Progress?")
                .setMessage("This will unstar all memorized words and reset your word index to #1.")
                .setPositiveButton("Reset") { _, _ ->
                    prefManager.resetProgress()
                    Toast.makeText(this, "Progress reset successfully", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
