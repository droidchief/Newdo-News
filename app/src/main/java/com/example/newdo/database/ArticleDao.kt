package com.example.newdo.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.newdo.database.model.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun upsert(article: Article): Long

    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>

    @Delete
     fun deleteArticle(article: Article)
}
