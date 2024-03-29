package com.rayo.digitaldiary.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rayo.digitaldiary.ui.customer.CustomerData

@Dao
interface CustomerDao {

    @Query("SELECT * FROM Customer ORDER BY createdAt DESC")
    suspend fun getCustomers(): List<CustomerData>

    @Query("SELECT * FROM Customer ORDER BY createdAt DESC")
    suspend fun getAllCustomers(): List<CustomerData>

    @Query("SELECT * FROM Customer WHERE active = 1 ORDER BY createdAt DESC")
    suspend fun getActiveCustomers(): List<CustomerData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCustomer(customerList: List<CustomerData>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customerData: CustomerData)

    @Query("SELECT COUNT(id) FROM Customer WHERE active = 1")
    suspend fun getCustomerActiveCount(): Int

    @Query("DELETE FROM Customer")
    suspend fun deleteTable()
}




