package com.rayo.digitaldiary.ui.settings

import com.google.gson.annotations.SerializedName
import com.rayo.digitaldiary.api.CommonResponse

class ChangePasswordResponce : CommonResponse() {
    @SerializedName("data")
    val data: ChangePasswordData? = null
}

class ChangePasswordData {

    @SerializedName("old_password")
    val oldPassword: String? = null

    @SerializedName("new_password")
    val newPassword: String? = null
}