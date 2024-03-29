package com.rayo.digitaldiary.api

import android.util.Log
import com.bumptech.glide.util.Util
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import org.json.JSONObject
import retrofit2.Response
import java.net.SocketTimeoutException

/**
 * Created by Mittal Varsani on 26/5/20.
 * @author Mittal Varsani
 */
abstract class APIRequest {

    suspend fun <T : Any> apiRequest(call: suspend () -> Response<T>): T {
        try {
            val response = call.invoke()
            if (response.code() == Constants.STATUS_200 && response.isSuccessful && response.body() != null) {
                return response.body()!!
            } else if (response.code() == Constants.STATUS_UNAUTHORIZED) {
                val jsonObject = response.errorBody()?.string()?.let { JSONObject(it) }
                val message = jsonObject?.getString("message")
                throw UnauthorizedUserException(Utils.getTranslatedValue(message.toString()))
            }else {
                val jsonObject = response.errorBody()?.string()?.let { JSONObject(it) }
                val message = jsonObject?.getString("message")
                throw ApiException(Utils.getTranslatedValue(message.toString()))
            }
        } catch (e: SocketTimeoutException) {
            e.printStackTrace()
            throw ApiException("Socket timeOut Exception")
        } catch (e: NoInternetException) {
            e.printStackTrace()
            throw NoInternetException(Utils.getTranslatedValue(e.message.toString()))
        } catch (e: UnauthorizedUserException) {
            e.printStackTrace()
            Log.e("UnauthorizedUser", "apiRequest: UnauthorizedUserException" + e.message)
            throw UnauthorizedUserException(Utils.getTranslatedValue(e.message.toString()))
        } catch (e: ApiException) {
            e.printStackTrace()
            throw ApiException(Utils.getTranslatedValue(e.message.toString()))
        } catch (e: Exception) {
            e.printStackTrace()
            throw ApiException(Utils.getTranslatedValue("Something went wrong"))
        }
    }
}