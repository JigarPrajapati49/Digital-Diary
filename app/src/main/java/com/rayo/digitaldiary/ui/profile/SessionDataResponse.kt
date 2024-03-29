package com.rayo.digitaldiary.ui.profile

import com.google.gson.annotations.SerializedName
import com.rayo.digitaldiary.api.CommonResponse

class SessionDataResponse : CommonResponse() {
    @SerializedName("data")
    val sessionData: List<SessionData> = ArrayList()
}

open class SessionData {
    @SerializedName("user_id")
    var userId: String = ""

    @SerializedName("device_id")
    var deviceId: String = ""

    @SerializedName("device_name")
    var deviceName: String = ""

    @SerializedName("last_access_time")
    var lastUsed: String = ""
}
