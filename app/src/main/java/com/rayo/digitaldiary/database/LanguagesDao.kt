package com.rayo.digitaldiary.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rayo.digitaldiary.ui.LanguageData


@Dao
interface LanguagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLanguagesListInDB(languagesList: List<LanguageData>)

    @Query("SELECT * FROM Languages WHERE active =:active")
    suspend fun getLanguagesListFromDB(active : Int): List<LanguageData>
}