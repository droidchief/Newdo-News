package com.example.newdo.model

import com.example.newdo.model.Article

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)