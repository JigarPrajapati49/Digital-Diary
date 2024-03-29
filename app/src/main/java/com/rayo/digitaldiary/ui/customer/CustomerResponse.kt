package com.rayo.digitaldiary.ui.customer

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.rayo.digitaldiary.BR
import com.rayo.digitaldiary.api.CommonResponse
import com.rayo.digitaldiary.database.TimestampConverter
import com.rayo.digitaldiary.ui.settings.NotificationDataDetails

/**
 * Created by Mittal Varsani on 18/04/23.
 *
 * @author Mittal Varsani
 */
class AddCustomerResponse : CommonResponse() {

    @SerializedName("data")
    val customerData: CustomerData? = null
}

class CustomerListData {

    @SerializedName("current_page")
    val currentPage: Int = 0

    @SerializedName("total_page")
    val totalPage: Int = 0

    @SerializedName("total_record_per_page")
    val totalRecordPerPage: Int = 0

    @SerializedName("list")
    val customerList: List<CustomerData> = ArrayList()

}

@Entity(tableName = "Customer")
class CustomerData() : Parcelable, BaseObservable() {

    /* Created new object because of two way dataBinding changing value
          in listing without submitting data */
    fun copy(customerData: CustomerData): CustomerData {
        return CustomerData().apply {
            id = customerData.id
            name = customerData.name
            address = customerData.address
            contactNumber = customerData.contactNumber
            createdById = customerData.createdById
            createdByRole = customerData.createdByRole
            active = customerData.active
            isCustomerSelected = customerData.isCustomerSelected
            createdAt = customerData.createdAt
            updatedAt = customerData.updatedAt
            dueAmount = customerData.dueAmount
        }
    }

    fun update(oldCustomerData: CustomerData, newCustomerData: CustomerData): CustomerData {
        return oldCustomerData.apply {
            id = newCustomerData.id
            name = newCustomerData.name
            address = newCustomerData.address
            contactNumber = newCustomerData.contactNumber
            createdById = newCustomerData.createdById
            createdByRole = newCustomerData.createdByRole
            active = newCustomerData.active
            isCustomerSelected = newCustomerData.isCustomerSelected
            createdAt = newCustomerData.createdAt
            updatedAt = newCustomerData.updatedAt
            dueAmount = newCustomerData.dueAmount
        }
    }

    @PrimaryKey
    @SerializedName("_id")
    var id: String = ""

    @get:Bindable
    var name: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }

    @get:Bindable
    var address: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.address)
        }

    @get:Bindable
    @SerializedName("contact_number")
    var contactNumber: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.contactNumber)
        }

    @SerializedName("created_by_id")
    var createdById: String = ""

    @SerializedName("created_by_role")
    var createdByRole: String = ""

    @SerializedName("is_active")
    var active: Int = 1

    @Ignore
    var isCustomerSelected: Int = 0

    @TypeConverters(TimestampConverter::class)
    var createdAt: String = ""

    var updatedAt: String = ""

    @Ignore
    var dueAmount: Float = 0f // make sure you have calculated dueAmount and assigned a data

    @Ignore
    @SerializedName("notification_preferences")
    val notificationData: NotificationDataDetails? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString().toString()
        name = parcel.readString().toString()
        address = parcel.readString().toString()
        contactNumber = parcel.readString().toString()
        createdById = parcel.readString().toString()
        createdByRole = parcel.readString().toString()
        active = parcel.readInt()
        isCustomerSelected = parcel.readInt()
        createdAt = parcel.readString().toString()
        updatedAt = parcel.readString().toString()
        dueAmount = parcel.readFloat()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(address)
        parcel.writeString(contactNumber)
        parcel.writeString(createdById)
        parcel.writeInt(active)
        parcel.writeInt(isCustomerSelected)
        parcel.writeString(createdByRole)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeFloat(dueAmount)
    }

    companion object CREATOR : Parcelable.Creator<CustomerData> {
        override fun createFromParcel(parcel: Parcel): CustomerData {
            return CustomerData(parcel)
        }

        override fun newArray(size: Int): Array<CustomerData?> {
            return arrayOfNulls(size)
        }
    }
}

class CustomerProfileResponce : CommonResponse() {

    @SerializedName("data")
    val customerData: List<CustomerData> = ArrayList()
}