package com.homeworkshop.newsappmvvm.api

import com.homeworkshop.newsappmvvm.models.NewsResponse
import com.homeworkshop.newsappmvvm.util.Constants.Companion.NEWS_API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/top-headlines")
    suspend fun getBreadingNews(
            @Query("country")
            countryCode:String = "us",
            @Query("page")
            pageNumber :Int = 1,
            @Query("apiKey")
            apiKey:String = NEWS_API_KEY
    ) : Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchForNews(
            @Query("q")
            searchQuery:String,
            @Query("page")
            pageNumber :Int = 1,
            @Query("apiKey")
            apiKey:String = NEWS_API_KEY
    ) : Response<NewsResponse>
}