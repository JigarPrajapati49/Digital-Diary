package com.rayo.digitaldiary.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import com.rayo.digitaldiary.BuildConfig
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient {
    companion object {
        fun webservice(networkConnectionInterceptor: NetworkConnectionInterceptor): APIInterface {
            val logging = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG)
                logging.level = HttpLoggingInterceptor.Level.BODY
            else
                logging.level = HttpLoggingInterceptor.Level.NONE

            val okClient = OkHttpClient.Builder().apply {
                connectTimeout(15, TimeUnit.SECONDS)
                readTimeout(15, TimeUnit.SECONDS)
                writeTimeout(15, TimeUnit.SECONDS)
                addInterceptor(networkConnectionInterceptor)
                addInterceptor(logging)
            }

            return Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .client(okClient.build())
                .build()
                .create(APIInterface::class.java)
        }
    }
}