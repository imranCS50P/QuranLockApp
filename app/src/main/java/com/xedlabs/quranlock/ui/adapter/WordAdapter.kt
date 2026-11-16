package com.xedlabs.quranlock.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.xedlabs.quranlock.R
import com.xedlabs.quranlock.model.QuranWord

class WordAdapter(
    private var words: List<QuranWord>,
    private val onStarClick: (QuranWord) -> Unit,
    private val onItemClick: (QuranWord) -> Unit
) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNumber: TextView = itemView.findViewById(R.id.tvItemNumber)
        val tvGrammar: TextView = itemView.findViewById(R.id.tvItemGrammar)
        val tvFrequency: TextView = itemView.findViewById(R.id.tvItemFrequency)
        val tvTranslit: TextView = itemView.findViewById(R.id.tvItemTranslit)
        val tvEnglish: TextView = itemView.findViewById(R.id.tvItemEnglish)
        val tvArabic: TextView = itemView.findViewById(R.id.tvItemArabic)
        val tvUrdu: TextView = itemView.findViewById(R.id.tvItemUrdu)
        val btnStar: ImageButton = itemView.findViewById(R.id.btnItemStar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_word, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = words[position]
        holder.tvNumber.text = "#${word.id}"
        holder.tvGrammar.text = word.grammarType
        holder.tvFrequency.text = "${word.frequency}"
        holder.tvTranslit.text = word.transliteration
        holder.tvEnglish.text = word.englishMeaning
        holder.tvArabic.text = word.arabic
        holder.tvUrdu.text = word.urduMeaning

        if (word.isMemorized) {
            holder.btnStar.setImageResource(android.R.drawable.btn_star_big_on)
            holder.btnStar.setColorFilter(
                ContextCompat.getColor(holder.itemView.context, R.color.accent_star)
            )
        } else {
            holder.btnStar.setImageResource(android.R.drawable.btn_star_big_off)
            holder.btnStar.setColorFilter(
                ContextCompat.getColor(holder.itemView.context, R.color.text_muted)
            )
        }

        holder.btnStar.setOnClickListener {
            onStarClick(word)
            notifyItemChanged(position)
        }

        holder.itemView.setOnClickListener {
            onItemClick(word)
        }
    }

    override fun getItemCount(): Int = words.size

    fun updateData(newWords: List<QuranWord>) {
        this.words = newWords
        notifyDataSetChanged()
    }
}
