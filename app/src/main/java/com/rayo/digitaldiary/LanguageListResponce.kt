package com.rayo.digitaldiary

import com.google.gson.annotations.SerializedName
import com.rayo.digitaldiary.api.CommonResponse
import com.rayo.digitaldiary.ui.LanguageData

class LanguageListResponce : CommonResponse() {
    @SerializedName("data")
    val data: LanguageDataTest? = null
}

class LanguageDataTest {

    @SerializedName("language")
    var languageList: List<LanguageData> = ArrayList()
}