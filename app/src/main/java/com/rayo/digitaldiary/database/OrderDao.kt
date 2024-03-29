package com.rayo.digitaldiary.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rayo.digitaldiary.ui.order.OrderData


/**
 * Created by Mittal Varsani on 28/04/23.
 *
 * @author Mittal Varsani
 */

@Dao
interface OrderDao {

    // Order define in '' because Order keyword is used by room database also
    @Transaction
    @Query("SELECT * FROM 'OrderHistory' WHERE orderDate BETWEEN :createdAtStart AND :createdAtEnd ORDER BY createdAt DESC")
    suspend fun getOrders(createdAtStart: String, createdAtEnd: String): List<CustomerWithHistory>

    // Order define in '' because Order keyword is used by room database also
    @Transaction
    @Query("SELECT * FROM 'OrderHistory' WHERE cancelled = 0 AND orderDate BETWEEN :createdAtStart AND :createdAtEnd")
    suspend fun getCompleteOrders(
        createdAtStart: String,
        createdAtEnd: String,
    ): List<CustomerWithHistory>

    @Query("SELECT * FROM 'OrderHistory' WHERE cancelled = 0")
    suspend fun getCompletedOrders(): List<OrderData>

    @Query("SELECT * FROM 'orderHistory' WHERE customerId =:customerId AND cancelled = 0 ORDER BY orderDate ASC")
    suspend fun getDataMonthYearWise(customerId: String): List<OrderData>

    @Query("SELECT * FROM 'orderHistory' WHERE customerId =:customerId AND cancelled = 0")
    suspend fun getCustomerAllOrder(customerId: String): List<OrderData>

    @Transaction
    @Query("SELECT * FROM 'OrderHistory' WHERE orderDate BETWEEN :createdAtStart AND :createdAtEnd AND createdById =:createdById ORDER BY orderDate DESC")
    suspend fun getStaffOrderHistory(
        createdAtStart: String,
        createdAtEnd: String,
        createdById: String,
    ): List<CustomerWithHistory>

    @Transaction
    @Query("SELECT * FROM 'OrderHistory' WHERE customerId = :customerId AND strftime('%Y',orderDate) IN(:year) AND strftime('%m',orderDate) IN(:month) ORDER BY orderDate ASC")
    suspend fun getMonthDetailsHistory(
        customerId: String,
        month: String,
        year: String,
    ): List<CustomerWithHistory>

    @Transaction
    @Query("SELECT * FROM 'OrderHistory' WHERE localOrderId =:localOrderId")
    suspend fun getOwnerOrStaffOrderHistory(localOrderId: String): CustomerWithHistory

    @Query("SELECT * FROM 'OrderHistory' WHERE sync = 0")
    suspend fun getNotSyncOrder(): List<OrderData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllOrder(orderList: List<OrderData>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOder(orderData: OrderData)

    @Query("delete from 'OrderHistory' where localOrderId in (:idList)")
    suspend fun delete(idList: List<String>)

    @Query("DELETE FROM 'OrderHistory'")
    suspend fun deleteTable()

    @Query("SELECT COUNT(sync) from 'OrderHistory' WHERE sync=0")
    suspend fun getOrderSyncCount(): Int

    @Query("SELECT * from 'OrderHistory' ")
    suspend fun getTotalRecord(): List<OrderData>

    @Transaction
    @Query("SELECT * FROM 'OrderHistory' WHERE  createdById =:userId")
    suspend fun getTotalRecordOfStaff(userId: String): List<CustomerWithHistory>

    @Transaction
    @Query("SELECT * FROM 'OrderHistory' WHERE  createdById =:userId AND sync=0")
    suspend fun getTotalRecordOfStaffUnSync(userId: String): List<CustomerWithHistory>
}