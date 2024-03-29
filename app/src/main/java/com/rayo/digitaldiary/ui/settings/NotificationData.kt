package com.rayo.digitaldiary.ui.settings

import com.google.gson.annotations.SerializedName
import com.rayo.digitaldiary.api.CommonResponse
/**
 * Created by Mittal Varsani on 01/08/23.
 *
 * @author Mittal Varsani
 */
class NotificationData {

    var name: String = ""

    var enable: Boolean = true
}

class NotificationResponce : CommonResponse() {

    val data: NotificationDataDetails? = null
}

open class NotificationDataDetails {

    @SerializedName("user_id")
    var id: String = ""

    @SerializedName("order")
    var order: Boolean = true

    @SerializedName("payment")
    var payment: Boolean = true
}