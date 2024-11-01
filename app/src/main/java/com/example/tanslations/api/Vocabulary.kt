package com.example.translations.api

data class Vocabulary(
    val voc_id: Int,
    val voc_word: String,
    val voc_definition: String,
    val voc_pronunciation: String,
    val voc_translation: String,
    val voc_example: String,
    val voc_example_pinyin: String,
    val voc_video: String // Thêm thuộc tính cho nét viết
)
