package com.rayo.digitaldiary.ui.languageTranslation

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "LanguageTranslation")
class LanguageTranslationData {

    @PrimaryKey
    @SerializedName("_id")
    var id: String = ""

    @SerializedName("translated_value")
    var translatedValue: String = ""

    @SerializedName("english_translation")
    var englishTranslation: String = ""

    @SerializedName("term")
    var term: String = ""

    @SerializedName("language_name")
    var languageName: String = ""

    @SerializedName("abbreviation")
    var abbreviation: String = ""

    @SerializedName("createdAt")
    var createdAt: String = ""

    @SerializedName("updatedAt")
    var updatedAt: String = ""

    @SerializedName("is_active")
    var active: Int = 0
}