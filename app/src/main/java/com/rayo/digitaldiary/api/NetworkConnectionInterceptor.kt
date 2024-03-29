package com.rayo.digitaldiary.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.rayo.digitaldiary.R
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import java.lang.Exception
import java.net.ConnectException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConnectionInterceptor @Inject constructor(@ApplicationContext context: Context) :
    Interceptor {
    private val applicationContext = context.applicationContext
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isInternetAvailable()) {
            throw NoInternetException(applicationContext.getString(R.string.no_internet))
        }
        try {
            return chain.proceed(chain.request())
        } catch (e: ConnectException) {
            throw NoInternetException("Failed to connect to server")
        } catch (e: Exception) {
            throw NoInternetException(applicationContext.getString(R.string.no_internet))
        }
    }

    fun isInternetAvailable(): Boolean {
        var result = false
        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        connectivityManager?.let {
            it.getNetworkCapabilities(connectivityManager.activeNetwork)?.apply {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                    else -> false
                }
            }
        }
        return result
    }
}