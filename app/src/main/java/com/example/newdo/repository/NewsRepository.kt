package com.example.newdo.repository

import com.example.newdo.api.RetrofitInstance
import com.example.newdo.database.ArticleDatabase

class NewsRepository(
    val db: ArticleDatabase
) {

    //get breaking news from my api
    suspend fun getBreakingNews(country: String, currentPage: Int) =
        RetrofitInstance.api.getBreakingNews(country, currentPage)
}