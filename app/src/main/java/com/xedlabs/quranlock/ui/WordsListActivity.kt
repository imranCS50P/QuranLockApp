package com.xedlabs.quranlock.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.xedlabs.quranlock.databinding.ActivityWordsListBinding
import com.xedlabs.quranlock.model.QuranWord
import com.xedlabs.quranlock.repository.WordRepository
import com.xedlabs.quranlock.service.WordOverlayService
import com.xedlabs.quranlock.ui.adapter.WordAdapter

class WordsListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWordsListBinding
    private lateinit var repository: WordRepository
    private lateinit var adapter: WordAdapter
    private var allWords: List<QuranWord> = emptyList()
    private var currentFilterMode = FILTER_ALL
    private var currentSearchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWordsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = WordRepository(this)
        allWords = repository.getAllWords()

        setupToolbar()
        setupRecyclerView()
        setupSearch()
        setupFilterChips()
    }

    private fun setupToolbar() {
        binding.toolbarWords.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = WordAdapter(
            words = allWords,
            onStarClick = { word ->
                repository.toggleMemorized(word)
            },
            onItemClick = { word ->
                WordOverlayService.showWordOverlay(this, word.id)
            }
        )
        binding.rvWords.layoutManager = LinearLayoutManager(this)
        binding.rvWords.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearchWord.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s?.toString()?.trim() ?: ""
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupFilterChips() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            currentFilterMode = when {
                checkedIds.contains(binding.chipMemorized.id) -> FILTER_MEMORIZED
                checkedIds.contains(binding.chipPending.id) -> FILTER_PENDING
                else -> FILTER_ALL
            }
            applyFilters()
        }
    }

    private fun applyFilters() {
        var filtered = when (currentFilterMode) {
            FILTER_MEMORIZED -> allWords.filter { it.isMemorized }
            FILTER_PENDING -> allWords.filter { !it.isMemorized }
            else -> allWords
        }

        if (currentSearchQuery.isNotEmpty()) {
            val query = currentSearchQuery.lowercase()
            filtered = filtered.filter {
                it.transliteration.lowercase().contains(query) ||
                        it.englishMeaning.lowercase().contains(query) ||
                        it.urduMeaning.contains(query) ||
                        it.arabic.contains(query) ||
                        it.id.toString() == query
            }
        }

        adapter.updateData(filtered)
    }

    companion object {
        private const val FILTER_ALL = 0
        private const val FILTER_MEMORIZED = 1
        private const val FILTER_PENDING = 2
    }
}
