package com.rayo.digitaldiary.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rayo.digitaldiary.ui.staff.StaffData

/**
 * Created by Mittal Varsani on 28/04/23.
 *
 * @author Mittal Varsani
 */

@Dao
interface StaffDao {

    @Query("SELECT * FROM Staff ORDER BY createdAt DESC")
    suspend fun getStaff(): List<StaffData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStaff(staffList: List<StaffData>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaff(staffData: StaffData)

    @Query("SELECT COUNT(id) FROM Staff WHERE active = 1")
    suspend fun getStaffActiveCount(): Int

    @Query("DELETE FROM Staff")
    suspend fun deleteTable()
}