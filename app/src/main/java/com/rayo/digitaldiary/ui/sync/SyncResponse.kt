package com.rayo.digitaldiary.ui.sync

import com.google.gson.annotations.SerializedName
import com.rayo.digitaldiary.api.CommonResponse
import com.rayo.digitaldiary.ui.customer.CustomerData
import com.rayo.digitaldiary.ui.languageTranslation.LanguageTranslationData
import com.rayo.digitaldiary.ui.order.OrderData
import com.rayo.digitaldiary.ui.product.Product
import com.rayo.digitaldiary.ui.staff.StaffData
import com.rayo.digitaldiary.ui.payment.PaymentData

class SyncInitResponse : CommonResponse() {

    @SerializedName("data")
    val syncInitData: SyncInitData? = null
}

class SyncInitData {
    @SerializedName("start_sync_time")
    val syncStartTime: String = ""

    @SerializedName("total_records")
    val totalRecords: Int = 0

    @SerializedName("total_pages")
    var totalPages: Int = 0
}

class SyncDataResponse : CommonResponse() {

    @SerializedName("data")
    val syncData: SyncData? = null
}

class SyncData {

    @SerializedName("start_sync_time")
    val syncStartTime: String = ""

    @SerializedName("current_page")
    val currentPage: Int = 0

    @SerializedName("product")
    val productList: List<Product> = ArrayList()

    @SerializedName("customer")
    val customerList: List<CustomerData> = ArrayList()

    @SerializedName("staff")
    val staffList: List<StaffData> = ArrayList()

    @SerializedName("language_translation")
    val languageTranslationList: List<LanguageTranslationData> = ArrayList()

    @SerializedName("order_history")
    val orderHistoryList: List<OrderData> = ArrayList()

    @SerializedName("payment_history")
    val paymentHistoryList: List<PaymentData> = ArrayList()
}