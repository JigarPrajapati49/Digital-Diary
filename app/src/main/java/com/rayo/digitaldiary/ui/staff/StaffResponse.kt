package com.rayo.digitaldiary.ui.staff

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
import java.util.*

/**
 * Created by Mittal Varsani on 10/04/23.
 *
 * @author Mittal Varsani
 */
class AddStaffResponse : CommonResponse() {

    @SerializedName("data")
    val staffData: StaffData? = null
}

@Entity(tableName = "Staff")
class StaffData() : Parcelable, BaseObservable() {

    /* Created new object because of two way dataBinding changing value
      in listing without submitting data */
    fun copy(staffData: StaffData): StaffData {
        return StaffData().apply {
            id = staffData.id
            name = staffData.name
            email = staffData.email
            active = staffData.active
            contactNumber = staffData.contactNumber
            createdAt = staffData.createdAt
            updatedAt = staffData.updatedAt
        }
    }

    fun update(oldStaffData: StaffData, newStaffData: StaffData): StaffData {
        return oldStaffData.apply {
            id = newStaffData.id
            name = newStaffData.name
            email = newStaffData.email
            active = newStaffData.active
            contactNumber = newStaffData.contactNumber
            createdAt = newStaffData.createdAt
            updatedAt = newStaffData.updatedAt
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
    var email: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.email)
        }

    @SerializedName("is_active")
    var active: Int = 1

    @get:Bindable
    @SerializedName("contact_number")
    var contactNumber: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.contactNumber)
        }

    @TypeConverters(TimestampConverter::class)
    var createdAt: String = ""

    var updatedAt: String = ""

    @Ignore
    @SerializedName("notification_preferences")
    val notificationData: NotificationDataDetails? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString().toString()
        name = parcel.readString().toString()
        email = parcel.readString().toString()
        active = parcel.readInt()
        contactNumber = parcel.readString().toString()
        createdAt = parcel.readString().toString()
        updatedAt = parcel.readString().toString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeInt(active)
        parcel.writeString(contactNumber)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
    }

    companion object CREATOR : Parcelable.Creator<StaffData> {
        override fun createFromParcel(parcel: Parcel): StaffData {
            return StaffData(parcel)
        }

        override fun newArray(size: Int): Array<StaffData?> {
            return arrayOfNulls(size)
        }
    }
}