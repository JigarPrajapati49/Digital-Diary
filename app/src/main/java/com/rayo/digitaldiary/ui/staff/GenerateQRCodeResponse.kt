package com.rayo.digitaldiary.ui.staff

import com.google.gson.annotations.SerializedName
import com.rayo.digitaldiary.api.CommonResponse

class GenerateQRCodeResponse : CommonResponse() {

    @SerializedName("data")
    val generateQRData: GenerateQRData? = null
}

class GenerateQRData {

    @SerializedName("qr_code")
    val qrCode: String = ""
}