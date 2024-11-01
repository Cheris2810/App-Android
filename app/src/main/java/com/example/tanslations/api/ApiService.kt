package com.example.translations.api

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("get_tuvung.php")
    fun getVocabulary(): Call<List<Vocabulary>>
}
