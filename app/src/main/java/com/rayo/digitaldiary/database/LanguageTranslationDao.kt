package com.rayo.digitaldiary.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rayo.digitaldiary.ui.languageTranslation.LanguageTranslationData

@Dao
interface LanguageTranslationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLanguageTranslation(languageTranslationList: List<LanguageTranslationData>)

    @Query("SELECT * FROM LanguageTranslation WHERE abbreviation =:abbreviation")
    suspend fun getLanguageTranslation(abbreviation: String): List<LanguageTranslationData>
}