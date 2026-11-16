package com.xedlabs.quranlock.model

import com.google.gson.annotations.SerializedName

data class QuranWord(
    @SerializedName("id")
    val id: Int,

    @SerializedName("arabic")
    val arabic: String,

    @SerializedName("transliteration")
    val transliteration: String,

    @SerializedName("urdu_meaning")
    val urduMeaning: String,

    @SerializedName("english_meaning")
    val englishMeaning: String,

    @SerializedName("frequency")
    val frequency: Int,

    @SerializedName("reference")
    val reference: String,

    @SerializedName("grammar_type")
    val grammarType: String,

    @SerializedName("is_memorized")
    var isMemorized: Boolean = false
)
