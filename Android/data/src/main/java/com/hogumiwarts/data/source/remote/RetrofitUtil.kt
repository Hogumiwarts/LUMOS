package com.hogumiwarts.data.source.remote

import com.hogumiwarts.data.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object
RetrofitUtil {
    private val smartThingsClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    val smartThingsRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.AUTH_BASE_URL)
            .client(smartThingsClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val smartThingsApiService: SmartThingsApi by lazy {
        smartThingsRetrofit.create(SmartThingsApi::class.java)
    }
}
