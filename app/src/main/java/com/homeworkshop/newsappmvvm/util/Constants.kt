package com.homeworkshop.newsappmvvm.util

import com.homeworkshop.newsappmvvm.BuildConfig

class Constants {
    companion object {
        const val NEWS_API_KEY: String = BuildConfig.ApiKey
        const val BASE_URL = "https://newsapi.org"
        const val SEARCH_NEWS_TIME_DELAY = 500L
        const val QUERY_PAGE_SIZE = 20
    }
}