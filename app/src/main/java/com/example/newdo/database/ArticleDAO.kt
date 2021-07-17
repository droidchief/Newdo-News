package com.example.newdo.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.newdo.database.model.Article

@Dao
interface ArticleDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(article: Article): Long

    @Query("SELECT * FROM articles")
    fun getAllFavouriteArticle(): LiveData<List<Article>>

    @Delete
    fun deleteAllFavouriteArticle(article: Article)
}