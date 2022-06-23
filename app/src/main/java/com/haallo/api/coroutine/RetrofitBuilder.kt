package com.haallo.api.coroutine

import com.haallo.BuildConfig
import com.haallo.constant.NetworkConstants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBuilder {

    private var okHttpClient : OkHttpClient
    private var gsonConverterFactory : GsonConverterFactory

    init {
        val loggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        }

        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .followRedirects(false)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60,TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        gsonConverterFactory = GsonConverterFactory.create()
    }

    private fun getRetrofitClient() : Retrofit {
        return  Retrofit.Builder().baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    val GATEWAY_SERVICE : APIService = getRetrofitClient()
        .create(APIService::class.java)
}