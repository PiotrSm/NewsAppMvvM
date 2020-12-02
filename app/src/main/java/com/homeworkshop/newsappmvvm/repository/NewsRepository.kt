package com.homeworkshop.newsappmvvm.repository

import androidx.lifecycle.ViewModel
import com.homeworkshop.newsappmvvm.api.RetrofitInstance
import com.homeworkshop.newsappmvvm.db.ArticleDatabase
import retrofit2.Retrofit

class NewsRepository(
        val db: ArticleDatabase
) {
    /**
     * Metoda pobiera responce z retrofit api
     */
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
            RetrofitInstance.api.getBreadingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery:String, pageNumber: Int)=
            RetrofitInstance.api.searchForNews(searchQuery,pageNumber)
}