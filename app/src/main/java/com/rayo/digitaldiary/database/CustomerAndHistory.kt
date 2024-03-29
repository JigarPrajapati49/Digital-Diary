package com.rayo.digitaldiary.database

import androidx.room.Embedded
import androidx.room.Relation
import com.rayo.digitaldiary.ui.customer.CustomerData
import com.rayo.digitaldiary.ui.order.OrderData
import com.rayo.digitaldiary.ui.payment.PaymentData
import com.rayo.digitaldiary.ui.staff.StaffData

data class CustomerWithHistory(
    @Embedded val orderData: OrderData,

    @Relation(parentColumn = "customerId", entityColumn = "id")
    val customerData: CustomerData?,

    @Relation(parentColumn = "createdById", entityColumn = "id")
    val staffData: StaffData? = null,

    @Relation(parentColumn = "cancelledById", entityColumn = "id")
    var staffDatas: StaffData? = null,
)

data class StaffWithPayment(
    @Embedded val paymentData: PaymentData,
    @Relation(parentColumn = "customerId", entityColumn = "id")
    val customerData: CustomerData? = null,
    @Relation(parentColumn = "addByUserId", entityColumn = "id")
    val staffData: StaffData? = null,
)