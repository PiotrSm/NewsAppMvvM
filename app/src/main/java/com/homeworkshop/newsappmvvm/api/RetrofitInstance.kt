package com.homeworkshop.newsappmvvm.api

import com.homeworkshop.newsappmvvm.util.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


//singleton aby mieć dostęp z dowolnego miejsca w aplikacji
class RetrofitInstance {
    companion object{
        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build()
            Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
        }
        //To jest właściwy obiekt api który będziemy używać do retrofita
        val api by lazy {
            retrofit.create(NewsApi::class.java)
        }
    }
}