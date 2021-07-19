package com.example.newdo.database.model

data class  NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)