package com.homeworkshop.newsappmvvm.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homeworkshop.newsappmvvm.models.Article
import com.homeworkshop.newsappmvvm.models.NewsResponse
import com.homeworkshop.newsappmvvm.repository.NewsRepository
import com.homeworkshop.newsappmvvm.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
        val repository: NewsRepository
) : ViewModel() {
    val brekingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

    val TAG = "NewsViewModel"

    init {
        getBreakingNews("us")
    }

    //funkcja wyłouje funkcję wątkową dlatego też musi działać w wątku
    fun getBreakingNews(countryCode:String) = viewModelScope.launch {
        brekingNews.postValue(Resource.Loading())
        val response = repository.getBreakingNews(countryCode, breakingNewsPage)
        brekingNews.postValue(handleBreakingNewsResponse(response))
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if(breakingNewsResponse==null){
                    breakingNewsResponse = resultResponse
                }else{
                    val newArticles = resultResponse.articles
                    breakingNewsResponse?.articles?.addAll(newArticles)
                }
                Log.d(TAG,"breakingNewsResponseList : ${breakingNewsResponse?.articles?.size}")
                return Resource.Success(breakingNewsResponse ?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun getSearchNews(searchQuery:String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        val response = repository.searchNews(searchQuery, searchNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response))
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if(searchNewsResponse==null){
                    searchNewsResponse = resultResponse
                }else{
                    val newArticles = resultResponse.articles
                    searchNewsResponse?.articles?.addAll(newArticles)
                }
                Log.d(TAG,"searchNewsResponseList : ${searchNewsResponse?.articles?.size}")
                return Resource.Success(searchNewsResponse ?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        repository.upsert(article)
    }

    fun getSavedNews() = repository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        repository.deleteArticle(article)
    }
}