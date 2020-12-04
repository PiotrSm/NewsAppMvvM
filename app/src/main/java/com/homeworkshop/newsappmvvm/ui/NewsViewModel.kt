package com.homeworkshop.newsappmvvm.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homeworkshop.newsappmvvm.NewsApplication
import com.homeworkshop.newsappmvvm.models.Article
import com.homeworkshop.newsappmvvm.models.NewsResponse
import com.homeworkshop.newsappmvvm.repository.NewsRepository
import com.homeworkshop.newsappmvvm.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
        app: Application,
        val repository: NewsRepository
) : AndroidViewModel(app) {
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
    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val newArticles = resultResponse.articles
                    breakingNewsResponse?.articles?.addAll(newArticles)
                }
                Log.d(TAG, "breakingNewsResponseList : ${breakingNewsResponse?.articles?.size}")
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun getSearchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                } else {
                    val newArticles = resultResponse.articles
                    searchNewsResponse?.articles?.addAll(newArticles)
                }
                Log.d(TAG, "searchNewsResponseList : ${searchNewsResponse?.articles?.size}")
                return Resource.Success(searchNewsResponse ?: resultResponse)
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

    /**
     * metoda wywołująca bezpiecznie zapytanie do retrofita
     */
    private suspend fun safeBreakingNewsCall(countryCode: String) {
        brekingNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
                val response = repository.getBreakingNews(countryCode, breakingNewsPage)
                brekingNews.postValue(handleBreakingNewsResponse(response))
            }else{
                brekingNews.postValue(Resource.Error("No internet connection"))
            }

        } catch (t: Throwable) {
            when(t){
                is IOException -> brekingNews.postValue(Resource.Error("Network Failure"))
                else -> brekingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    /**
     * metoda wywołująca bezpiecznie zapytanie do retrofita
     */
    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
                val response = repository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            }else{
                searchNews.postValue(Resource.Error("No internet connection"))
            }

        } catch (t: Throwable) {
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    /*
    funkcja sprawdzająca czy user jest podłączony do internetu
     */
    private fun hasInternetConnection(): Boolean {
        //zmienna pobierająca informacje o połączeniach
        val conectivityManager = getApplication<NewsApplication>().getSystemService(
                Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = conectivityManager.activeNetwork ?: return false
            val capabilities = conectivityManager.getNetworkCapabilities(activeNetwork)
                    ?: return false
            // przy po mocy capabilities mamy dostęp do róznego typu połączeń
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }

        } else {
            conectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}