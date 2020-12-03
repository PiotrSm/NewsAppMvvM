package com.homeworkshop.newsappmvvm.repository

import androidx.lifecycle.ViewModel
import com.homeworkshop.newsappmvvm.api.RetrofitInstance
import com.homeworkshop.newsappmvvm.db.ArticleDatabase
import com.homeworkshop.newsappmvvm.models.Article
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

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().delete(article)
}