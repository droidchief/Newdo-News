package com.example.newdo.repository

import com.example.newdo.api.RetrofitInstance
import com.example.newdo.database.ArticleDatabase
import com.example.newdo.database.model.Article

class NewsRepository(val database: ArticleDatabase) {
    //get breaking news from my api
    suspend fun getBreakingNews(country: String, currentPage: Int) =
        RetrofitInstance.api.getBreakingNews(country, currentPage)

    suspend fun searchNews(searchQuery: String, currentPage: Int) =
        RetrofitInstance.api.searchNews(searchQuery, currentPage)


    suspend fun upsert(article: Article) =  database.getArticleDao().upsert(article)

    fun getSavedArticle() = database.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = database.getArticleDao().deleteArticle(article)
}