package com.rayo.digitaldiary.api

import com.google.gson.annotations.SerializedName

open class CommonResponse {
    val success: Int = 0
    val message: String? = null
    @SerializedName("status_code")
    val status: Int = 0
}