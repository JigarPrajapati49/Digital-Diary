package com.rayo.digitaldiary.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rayo.digitaldiary.ui.payment.PaymentData

@Dao
interface PaymentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insetPayment(paymentData: PaymentData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPayments(transactionList: List<PaymentData>)

    @Query("SELECT * FROM `PaymentHistory` WHERE sync = 0")
    suspend fun getNotSyncPaymentOrder(): List<PaymentData>

    @Query("SELECT * FROM `PaymentHistory`")
    suspend fun getAllPayment(): List<PaymentData>

    @Transaction
    @Query("SELECT * FROM `PaymentHistory` WHERE customerId = :customerId ORDER BY createdAt DESC")
    suspend fun getCustomerPayment(customerId: String): List<StaffWithPayment>

    @Transaction
    @Query("SELECT * FROM `PaymentHistory` WHERE addByUserId = :staffId ORDER BY createdAt DESC")
    suspend fun getStaffCollectedPayment(staffId: String): List<StaffWithPayment>

    @Transaction
    @Query("SELECT * FROM `PaymentHistory` ORDER BY createdAt DESC")
    suspend fun getAllPaymentByDescending(): List<StaffWithPayment>

    @Query("SELECT * FROM `PaymentHistory` WHERE customerId = :customerId")
    suspend fun getPaymentByCustomerId(customerId: String): List<PaymentData>

    @Transaction
    @Query("SELECT * FROM 'PaymentHistory' WHERE createdAt BETWEEN :createdAtStart AND :createdAtEnd")
    suspend fun getPayments(createdAtStart: String, createdAtEnd: String): List<PaymentData>

    @Query("DELETE FROM `PaymentHistory`")
    fun deleteTable()

    @Query("SELECT COUNT(sync) from 'PaymentHistory' WHERE sync=0")
    suspend fun getPaymentSyncCount(): Int

    @Query("SELECT * from 'paymenthistory' ")
    suspend fun getTotalRecord(): List<PaymentData>

    @Transaction
    @Query("SELECT * FROM 'paymenthistory' WHERE  addByUserId = :userId")
    suspend fun getTotalRecordOfStaff(userId: String): List<PaymentData>

    @Transaction
    @Query("SELECT * FROM 'paymenthistory' WHERE  addByUserId =:userId AND sync=0")
    suspend fun getTotalUnsyncRecordOfStaff(userId: String): List<PaymentData>
}