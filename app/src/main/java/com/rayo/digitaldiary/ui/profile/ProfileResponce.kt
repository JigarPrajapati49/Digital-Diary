package com.rayo.digitaldiary.ui.profile

import com.google.gson.annotations.SerializedName
import com.rayo.digitaldiary.api.CommonResponse
import com.rayo.digitaldiary.ui.customer.CustomerData
import com.rayo.digitaldiary.ui.staff.StaffData

class ProfileResponce : CommonResponse() {
    @SerializedName("data")
    val data: ProfileData? = null
}

class ProfileData : CommonResponse() {
    @SerializedName("businessOwner")
    val businessOwnerData: BusinessOwnerData? = null

    @SerializedName("staff")
    val staffData: StaffData? = null

    @SerializedName("customer")
    val customerData: CustomerData? = null
}

class BusinessOwnerData : CommonResponse() {
    @SerializedName("_id")
    val id: String = ""

    @SerializedName("email")
    val email: String = ""

    @SerializedName("business_name")
    val businessName: String = ""

    @SerializedName("contact_number")
    val contactNumber: String = ""

    @SerializedName("country_code")
    val countryCode: String = ""

    @SerializedName("iso_code")
    val isoCode: String = ""

    @SerializedName("currency_symbol")
    val currencySymbol: String = ""

    @SerializedName("currency_country_name")
    val currencyCountryName: String = ""
}
