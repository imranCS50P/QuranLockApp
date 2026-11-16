package com.xedlabs.quranlock.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.xedlabs.quranlock.R
import com.xedlabs.quranlock.model.QuranWord
import com.xedlabs.quranlock.preferences.PreferenceManager
import com.xedlabs.quranlock.repository.WordRepository

class WordOverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var repository: WordRepository? = null
    private var prefManager: PreferenceManager? = null
    private var autoDismissHandler: Handler? = null
    private var autoDismissRunnable: Runnable? = null
    private var currentWord: QuranWord? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        repository = WordRepository(this)
        prefManager = PreferenceManager(this)
        autoDismissHandler = Handler(Looper.getMainLooper())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val wordId = intent?.getIntExtra(EXTRA_WORD_ID, -1) ?: -1
        loadAndDisplayWord(wordId)
        return START_NOT_STICKY
    }

    private fun loadAndDisplayWord(wordId: Int) {
        val words = repository?.getAllWords() ?: emptyList()
        currentWord = if (wordId > 0) {
            words.find { it.id == wordId } ?: repository?.getCurrentWord()
        } else {
            repository?.getCurrentWord()
        }

        if (currentWord == null) {
            stopSelf()
            return
        }

        if (overlayView == null) {
            createOverlayView()
        }
        updateOverlayUI(currentWord!!)
        scheduleAutoDismiss()
    }

    private fun createOverlayView() {
        val layoutInflater = LayoutInflater.from(this)
        overlayView = layoutInflater.inflate(R.layout.overlay_word_card, null)

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        setupOverlayClickListeners(overlayView!!)
        windowManager?.addView(overlayView, layoutParams)
    }

    private fun setupOverlayClickListeners(view: View) {
        val btnClose = view.findViewById<ImageButton>(R.id.btnOverlayClose)
        val btnNext = view.findViewById<MaterialButton>(R.id.btnOverlayNext)
        val btnMemorized = view.findViewById<MaterialButton>(R.id.btnOverlayMemorize)

        btnClose.setOnClickListener {
            removeOverlayAndStop()
        }

        btnNext.setOnClickListener {
            val next = repository?.getNextWord()
            if (next != null) {
                currentWord = next
                updateOverlayUI(next)
                scheduleAutoDismiss()
            }
        }

        btnMemorized.setOnClickListener {
            currentWord?.let { word ->
                val newState = repository?.toggleMemorized(word) ?: false
                btnMemorized.text = if (newState) {
                    getString(R.string.memorized)
                } else {
                    getString(R.string.mark_memorized)
                }
            }
        }
    }

    private fun updateOverlayUI(word: QuranWord) {
        overlayView?.let { view ->
            val tvWordNum = view.findViewById<TextView>(R.id.tvOverlayWordNumber)
            val tvFrequency = view.findViewById<TextView>(R.id.tvOverlayFrequency)
            val tvArabic = view.findViewById<TextView>(R.id.tvOverlayArabic)
            val tvTranslit = view.findViewById<TextView>(R.id.tvOverlayTransliteration)
            val tvGrammar = view.findViewById<TextView>(R.id.tvOverlayGrammar)
            val tvUrdu = view.findViewById<TextView>(R.id.tvOverlayUrdu)
            val tvEnglish = view.findViewById<TextView>(R.id.tvOverlayEnglish)
            val tvAyahRef = view.findViewById<TextView>(R.id.tvOverlayReference)
            val btnMemorized = view.findViewById<MaterialButton>(R.id.btnOverlayMemorize)

            tvWordNum.text = "Word ${word.id} / 500"
            tvFrequency.text = "${word.frequency} times in Quran"
            tvArabic.text = word.arabic
            tvTranslit.text = word.transliteration
            tvGrammar.text = word.grammarType
            tvUrdu.text = word.urduMeaning
            tvEnglish.text = word.englishMeaning
            tvAyahRef.text = "Quran Reference: ${word.reference}"

            btnMemorized.text = if (word.isMemorized) {
                getString(R.string.memorized)
            } else {
                getString(R.string.mark_memorized)
            }
        }
    }

    private fun scheduleAutoDismiss() {
        cancelAutoDismiss()
        val seconds = prefManager?.autoCloseSeconds ?: 8
        if (seconds > 0) {
            autoDismissRunnable = Runnable {
                removeOverlayAndStop()
            }
            autoDismissHandler?.postDelayed(autoDismissRunnable!, seconds * 1000L)
        }
    }

    private fun cancelAutoDismiss() {
        autoDismissRunnable?.let { autoDismissHandler?.removeCallbacks(it) }
        autoDismissRunnable = null
    }

    private fun removeOverlayAndStop() {
        cancelAutoDismiss()
        overlayView?.let {
            try {
                windowManager?.removeView(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        overlayView = null
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeOverlayAndStop()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val EXTRA_WORD_ID = "extra_word_id"

        fun showWordOverlay(context: Context, wordId: Int) {
            val intent = Intent(context, WordOverlayService::class.java).apply {
                putExtra(EXTRA_WORD_ID, wordId)
            }
            context.startService(intent)
        }
    }
}
