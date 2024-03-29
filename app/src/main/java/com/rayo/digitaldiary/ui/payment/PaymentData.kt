package com.rayo.digitaldiary.ui.payment

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.room.*
import com.google.gson.annotations.SerializedName
import com.rayo.digitaldiary.api.CommonResponse
import com.rayo.digitaldiary.database.TimestampConverter
import com.rayo.digitaldiary.helper.Constants


class SyncPaymentResponse : CommonResponse() {

    @SerializedName("data")
    val paymentList: List<PaymentData> = ArrayList()
}

@Entity(tableName = "PaymentHistory")
class PaymentData() : BaseObservable(), Parcelable {
    @PrimaryKey
    @SerializedName("local_payment_id")
    var localPaymentId: String = ""

    @SerializedName("_id")
    var id: String = ""

    @SerializedName("customer_id")
    var customerId: String = ""

    @SerializedName("add_by_user_id")
    var addByUserId: String = ""

    @SerializedName("mode_of_payment")
    var modeOfPayment: String = Constants.CASH

    @get:Bindable
    @SerializedName("amount")
    var amount: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.amount)
        }

    @get:Bindable
    var note: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.note)
        }

    @TypeConverters(TimestampConverter::class)
    @SerializedName("created_at_app")
    var createdAt = ""

    @SerializedName("updated_at_app")
    var updatedAt = ""

    @Transient
    @ColumnInfo("sync")
    var sync: Int = 0


    constructor(parcel: Parcel) : this() {
        localPaymentId = parcel.readString().toString()
        id = parcel.readString().toString()
        customerId = parcel.readString().toString()
        addByUserId = parcel.readString().toString()
        modeOfPayment = parcel.readString().toString()
        amount = parcel.readString().toString()
        note = parcel.readString().toString()
        createdAt = parcel.readString().toString()
        updatedAt = parcel.readString().toString()
        sync = parcel.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeString(localPaymentId)
        parcel.writeString(id)
        parcel.writeString(customerId)
        parcel.writeString(addByUserId)
        parcel.writeString(modeOfPayment)
        parcel.writeString(amount)
        parcel.writeString(note)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeInt(sync)
    }

    companion object CREATOR : Parcelable.Creator<PaymentData> {
        override fun createFromParcel(parcel: Parcel): PaymentData {
            return PaymentData(parcel)
        }

        override fun newArray(size: Int): Array<PaymentData?> {
            return arrayOfNulls(size)
        }
    }
}

class DuePaymentData {
    var monthYear :String= ""

    var dueAmount :Float = 0f
}