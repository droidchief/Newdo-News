package com.example.newdo.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.newdo.model.Article

@Dao
interface ArticleDAO  {

     @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun upsert(article: Article) : Long

     @Query("SELECT * FROM articles")
     fun getAllSavedArticles(): LiveData<List<Article>>

     @Delete
     suspend fun deleteArticle(article: Article)

}