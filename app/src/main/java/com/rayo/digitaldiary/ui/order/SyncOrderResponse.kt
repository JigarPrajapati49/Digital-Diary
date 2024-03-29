package com.rayo.digitaldiary.ui.order

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.SerializedName
import com.rayo.digitaldiary.api.CommonResponse
import com.rayo.digitaldiary.database.OrderDateTimeStampConverter
import com.rayo.digitaldiary.database.TimestampConverter
import com.rayo.digitaldiary.ui.product.Product
import java.util.*

/**
 * Created by Mittal Varsani on 24/05/23.
 *
 * @author Mittal Varsani
 */
class SyncOrderResponse : CommonResponse() {

    @SerializedName("data")
    val orderList: List<OrderData> = ArrayList()
}

@Entity(tableName = "OrderHistory")
class OrderData() : Parcelable {

    @PrimaryKey
    @SerializedName("local_order_id")
    var localOrderId: String = ""

    @SerializedName("_id")
    var id: String = ""

    @SerializedName("created_by_id")
    var createdById: String = ""

    @SerializedName("order_date")
    @TypeConverters(OrderDateTimeStampConverter::class)
    var orderDate: String = ""

    @SerializedName("customer_id")
    var customerId: String = ""

    // Transient use to ignore field while creating json array
    // Because we used transient and we want these field in table we have to use columnInfo
    @Transient
    @ColumnInfo("products")
    var products: String = ""

    @Ignore
    @SerializedName("products")
    var product: MutableList<Product> = ArrayList()

    @SerializedName("order_amount")
    var orderAmount: String = ""

    @SerializedName("created_at_app")
    @TypeConverters(TimestampConverter::class)
    var createdAt: String = ""

    @SerializedName("is_cancelled")
    var cancelled: Int = 0

    @SerializedName("cancelled_date")
    var cancelledDate: String = ""

    @SerializedName("cancelled_by_id")
    var cancelledById: String = ""

    @SerializedName("note")
    var note: String = ""

    @SerializedName("updated_at_app")
    var updatedAt: String = ""

    @Transient
    @ColumnInfo("sync")
    var sync: Int = 0

    @SerializedName("cancel_reason")
    var cancelReason: String = ""

    constructor(parcel: Parcel) : this() {
        localOrderId = parcel.readString().toString()
        id = parcel.readString().toString()
        createdById = parcel.readString().toString()
        orderDate = parcel.readString().toString()
        customerId = parcel.readString().toString()
        orderAmount = parcel.readString().toString()
        createdAt = parcel.readString().toString()
        cancelled = parcel.readInt()
        cancelledDate = parcel.readString().toString()
        cancelledById = parcel.readString().toString()
        note = parcel.readString().toString()
        updatedAt = parcel.readString().toString()
        sync = parcel.readInt()
        cancelReason = parcel.readString().toString()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            parcel.readList(product, Product::class.java.classLoader, Product::class.java)
        } else {
            @Suppress("DEPRECATION") parcel.readList(product, Product::class.java.classLoader)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeString(localOrderId)
        parcel.writeString(id)
        parcel.writeString(createdById)
        parcel.writeString(orderDate)
        parcel.writeString(customerId)
        parcel.writeString(orderAmount)
        parcel.writeString(createdAt)
        parcel.writeInt(cancelled)
        parcel.writeString(cancelledDate)
        parcel.writeString(cancelledById)
        parcel.writeString(note)
        parcel.writeString(updatedAt)
        parcel.writeInt(sync)
        parcel.writeString(cancelReason)
        parcel.writeList(product)
    }

    companion object CREATOR : Parcelable.Creator<OrderData> {
        override fun createFromParcel(parcel: Parcel): OrderData {
            return OrderData(parcel)
        }

        override fun newArray(size: Int): Array<OrderData?> {
            return arrayOfNulls(size)
        }
    }
}