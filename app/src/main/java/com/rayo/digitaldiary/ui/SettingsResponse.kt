package com.rayo.digitaldiary.ui

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.rayo.digitaldiary.api.CommonResponse

/**
 * Created by Mittal Varsani on 08/05/23.
 *
 * @author Mittal Varsani
 */
class SettingsResponse : CommonResponse() {
    @SerializedName("data")
    val settingsData: SettingsData? = null
}

class SettingsData {

    @SerializedName("product_units")
    val productUnitsList: List<String> = ArrayList()

    @SerializedName("language")
    var languageList: List<LanguageData> = ArrayList()

    @SerializedName("currency")
    var currencyList: List<CurrencyData> = ArrayList()
}

@Entity(tableName = "Languages")
class LanguageData {

    @PrimaryKey
    @SerializedName("_id")
    var id: String = ""

    @SerializedName("language_name")
    var languageName: String = ""

    @SerializedName("language_translation_name")
    var languageTranslationName: String = ""

    @SerializedName("language_code")
    var languageCode: String = ""

    @Ignore
    var radioCheck: Boolean = false

    @SerializedName("is_active")
    var active: Int = 0
}

class CurrencyData {
    val currency: String = ""

    val symbol: String = ""

    val countryName: String = ""

    var isSelected: Boolean = false
}

