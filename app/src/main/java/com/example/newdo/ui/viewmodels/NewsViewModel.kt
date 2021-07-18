package com.example.newdo.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.newdo.database.model.NewsResponse
import com.example.newdo.repository.NewsRepository

class NewsViewModel(
    val newsRepository:  NewsRepository
) : ViewModel() {
}