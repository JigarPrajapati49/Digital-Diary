package com.rayo.digitaldiary.database

import androidx.room.*
import com.rayo.digitaldiary.ui.login.ScanQRData

@Dao
interface CustomerLoginDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomerLoginDetails(scanQRDataList: ScanQRData)

    @Query("SELECT * FROM CustomerLoginDetail")
    suspend fun getCustomerLoginDetails(): List<ScanQRData>

    @Query("SELECT * FROM CustomerLoginDetail WHERE userId =:userId")
    suspend fun getCurrentCustomerData(userId: String): ScanQRData?

    @Query("SELECT * FROM CustomerLoginDetail WHERE userId =:userId")
    suspend fun getCustomerId(userId: String): ScanQRData

    @Query("UPDATE CustomerLoginDetail SET orderHistoryLastSyncTime =:lastSyncTime WHERE userId =:userId")
    suspend fun setCustomerHistoryLastSyncTime(userId: String, lastSyncTime: String)

    @Query("UPDATE CustomerLoginDetail SET paymentHistoryLastSyncTime =:lastSyncTime WHERE userId =:userId")
    suspend fun setCustomerPaymentHistoryLastSyncTime(userId: String, lastSyncTime: String)

    @Query("UPDATE CustomerLoginDetail SET staffLastSyncTime =:lastSyncTime WHERE userId =:userId")
    suspend fun setCustomerStaffLastSyncTime(userId: String, lastSyncTime: String)

    @Query("UPDATE CustomerLoginDetail SET customerName = :customerName,contactNumber = :contactNumber,address = :address WHERE userId = :userId")
    suspend fun updateCustomerProfileData(
        customerName: String,
        contactNumber: String,
        address: String,
        userId: String
    )

    @Query("UPDATE CustomerLoginDetail SET currencyCountryName = :currencyCountryName, countryCode = :countryCode,currencySymbol = :currencySymbol WHERE userId = :userId")
    suspend fun updateCustomerCurrencyData(
        currencyCountryName: String,
        currencySymbol: String,
        countryCode: String,
        userId: String
    )

    @Query("DELETE FROM CustomerLoginDetail WHERE userId = :userId")
    suspend fun deleteById(userId: String)

    @Query("DELETE FROM CustomerLoginDetail")
    suspend fun deleteTable()
}