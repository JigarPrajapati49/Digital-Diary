package com.rayo.digitaldiary.ui.login

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.room.*
import com.google.gson.annotations.SerializedName
import com.rayo.digitaldiary.api.CommonResponse

/**
 * Created by Mittal Varsani on 05/06/23.
 *
 * @author Mittal Varsani
 */
class ScanQRResponse : CommonResponse() {

    @SerializedName("data")
    val scanQRData: ScanQRData? = null
}

@Entity(tableName = "CustomerLoginDetail")
class ScanQRData() : Parcelable, BaseObservable() {

    @PrimaryKey
    @SerializedName("_id")
    var userId: String = ""

    var token: String = ""

    @SerializedName("business_name")
    var businessName: String = ""

    @get:Bindable
    @ColumnInfo("customerName")
    var name: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }

    @get:Bindable
    @SerializedName("contact_number")
    var contactNumber: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.contactNumber)
        }

    @get:Bindable
    var address: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.address)
        }

    @Ignore
    var email: String = ""

    @Ignore
    @SerializedName("user_type")
    var userType: String = ""

    @SerializedName("is_active")
    var active: Int = 0

    @SerializedName("order_history_last_sync_time")
    var orderHistoryLastSyncTime: String = ""

    @SerializedName("payment_history_last_sync_time")
    var paymentHistoryLastSyncTime: String = ""

    @SerializedName("staff_last_sync_time")
    var staffLastSyncTime: String = ""

    @Ignore
    @SerializedName("device_id")
    var deviceId: String = ""

    @Ignore
    @SerializedName("device_name")
    var deviceName: String = ""

    @Transient
    @ColumnInfo("countryCode")
    var countryCode: String = ""

    @Ignore
    @SerializedName("iso_code")
    var isoCode: String = ""

    @SerializedName("currency_symbol")
    var currencySymbol: String = ""

    @SerializedName("currency_country_name")
    var currencyCountryName: String = ""

    @SerializedName("owner_email")
    var ownerEmail: String = ""

    @SerializedName("owner_contact_number")
    var ownerContactNumber: String = ""

    @Ignore
    var isSelected = false

    constructor(parcel: Parcel) : this() {
        businessName = parcel.readString().toString()
        userId = parcel.readString().toString()
        email = parcel.readString().toString()
        name = parcel.readString().toString()
        contactNumber = parcel.readString().toString()
        address = parcel.readString().toString()
        userType = parcel.readString().toString()
        active = parcel.readInt()
        deviceId = parcel.readString().toString()
        deviceName = parcel.readString().toString()
        token = parcel.readString().toString()
        countryCode = parcel.readString().toString()
        ownerEmail = parcel.readString().toString()
        ownerContactNumber = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(businessName)
        parcel.writeString(userId)
        parcel.writeString(email)
        parcel.writeString(name)
        parcel.writeString(contactNumber)
        parcel.writeString(address)
        parcel.writeString(userType)
        parcel.writeInt(active)
        parcel.writeString(deviceId)
        parcel.writeString(deviceName)
        parcel.writeString(token)
        parcel.writeString(countryCode)
        parcel.writeString(ownerEmail)
        parcel.writeString(ownerContactNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScanQRData> {
        override fun createFromParcel(parcel: Parcel): ScanQRData {
            return ScanQRData(parcel)
        }

        override fun newArray(size: Int): Array<ScanQRData?> {
            return arrayOfNulls(size)
        }
    }

}