package com.rayo.digitaldiary.ui.settings

import com.google.gson.annotations.SerializedName

data class LanguageModel(
    @SerializedName("title")
    var title: String = "",
    var radioCheck: Boolean=false)


