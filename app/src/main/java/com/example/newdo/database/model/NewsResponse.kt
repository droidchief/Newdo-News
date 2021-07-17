package com.example.newdo.database.model

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)