package com.example.newdo.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newdo.database.model.Article
import com.example.newdo.database.model.NewsResponse
import com.example.newdo.repository.NewsRepository
import com.example.newdo.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
    val newsRepository:  NewsRepository
) : ViewModel() {

    val breakingNews: MutableLiveData<Resource<NewsResponse >> = MutableLiveData()
    var breakingNewsCurrentPage = 1

    val searchNews: MutableLiveData<Resource<NewsResponse >> = MutableLiveData()
    var searchNewsCurrentPage = 1

    init {
        getBreakingNews("us")
    }

    //call the function that gets breaking news from the repository
    fun getBreakingNews(country: String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        val response = newsRepository.getBreakingNews(country, breakingNewsCurrentPage )

        breakingNews.postValue(handleBreakingNewsResponse(response))

    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        val response = newsRepository.searchNews(searchQuery, searchNewsCurrentPage)

        searchNews.postValue(handleSearchNewsResponse(response))
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }

        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }

        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews() = newsRepository.getSavedArticle()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

}