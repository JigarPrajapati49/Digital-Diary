package com.rayo.digitaldiary.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rayo.digitaldiary.ui.LanguageData
import com.rayo.digitaldiary.ui.customer.CustomerData
import com.rayo.digitaldiary.ui.languageTranslation.LanguageTranslationData
import com.rayo.digitaldiary.ui.login.ScanQRData
import com.rayo.digitaldiary.ui.order.OrderData
import com.rayo.digitaldiary.ui.product.Product
import com.rayo.digitaldiary.ui.staff.StaffData
import com.rayo.digitaldiary.ui.payment.PaymentData

/**
 * Created by Mittal Varsani on 31/08/22.
 *
 * @author Mittal Varsani
 */
@Database(
    entities = [Product::class, CustomerData::class, StaffData::class, OrderData::class, ScanQRData::class, LanguageTranslationData::class, LanguageData::class, PaymentData::class],
    version = 1,
    exportSchema = false
)
abstract class DigitalDiaryDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun staffDao(): StaffDao
    abstract fun customerDao(): CustomerDao
    abstract fun orderDao(): OrderDao
    abstract fun customerLoginDao(): CustomerLoginDao
    abstract fun languageTranslationDao(): LanguageTranslationDao
    abstract fun languagesDao(): LanguagesDao
    abstract fun paymentDao(): PaymentDao
}