package com.rayo.digitaldiary.api

import com.rayo.digitaldiary.LanguageListResponce
import com.rayo.digitaldiary.ui.SettingsResponse
import com.rayo.digitaldiary.ui.customer.AddCustomerResponse
import com.rayo.digitaldiary.ui.login.GoogleLoginResponse
import com.rayo.digitaldiary.ui.login.RegisterResponse
import com.rayo.digitaldiary.ui.login.ScanQRResponse
import com.rayo.digitaldiary.ui.order.SyncOrderResponse
import com.rayo.digitaldiary.ui.payment.SyncPaymentResponse
import com.rayo.digitaldiary.ui.product.AddProductResponse
import com.rayo.digitaldiary.ui.profile.ProfileResponce
import com.rayo.digitaldiary.ui.profile.SessionDataResponse
import com.rayo.digitaldiary.ui.settings.ChangePasswordResponce
import com.rayo.digitaldiary.ui.settings.NotificationResponce
import com.rayo.digitaldiary.ui.staff.AddStaffResponse
import com.rayo.digitaldiary.ui.staff.GenerateQRCodeResponse
import com.rayo.digitaldiary.ui.sync.SyncDataResponse
import com.rayo.digitaldiary.ui.sync.SyncInitResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by Mittal Varsani on 28/04/22.
 *
 * @author Mittal Varsani
 */
interface APIInterface {

    @FormUrlEncoded
    @POST(APIEndPoints.REGISTER)
    suspend fun register(
        @Header("language_code") languageCode: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("login_type") loginType: String,
        @Field("contact_number") contactNumber: String,
        @Field("country_code") countryCode: String,
        @Field("iso_code") isoCode: String,
        @Field("business_name") businessName: String,
        @Field("device_id") deviceId: String,
        @Field("device_name") deviceName: String,
        @Field("subscription_id") subscriptionId: String,
    ): Response<RegisterResponse>

    @FormUrlEncoded
    @POST(APIEndPoints.LOGIN)
    suspend fun login(
        @Header("language_code") languageCode: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("login_type") loginType: String,
        @Field("device_id") deviceId: String,
        @Field("device_name") deviceName: String,
        @Field("subscription_id") subscriptionId: String,
    ): Response<RegisterResponse>

    @FormUrlEncoded
    @POST(APIEndPoints.GOOGLE_LOGIN)
    suspend fun loginWithGoogle(
        @Header("language_code") languageCode: String,
        @Field("google_id") googleId: String,
        @Field("email") password: String,
        @Field("login_type") loginType: String,
        @Field("device_id") deviceId: String,
        @Field("device_name") deviceName: String,
        @Field("subscription_id") subscriptionId: String,
    ): Response<GoogleLoginResponse>

    @FormUrlEncoded
    @POST(APIEndPoints.COMPLETE_GOOGLE_LOGIN)
    suspend fun completeLoginWithGoogle(
        @Header("token") token: String,
        @Header("language_code") languageCode: String,
        @Field("business_name") businessName: String,
        @Field("contact_number") contactNumber: String,
        @Field("country_code") countryCode: String,
        @Field("iso_code") isoCode: String,
        @Field("device_id") deviceId: String,
        @Field("device_name") deviceName: String,
        @Field("subscription_id") subscriptionId: String,
    ): Response<GoogleLoginResponse>

    @FormUrlEncoded
    @POST(APIEndPoints.FORGOT_PASSWORD)
    suspend fun forgotPassword(
        @Header("language_code") languageCode: String,
        @Field("email") email: String,
    ): Response<CommonResponse>

    @FormUrlEncoded
    @POST(APIEndPoints.VERIFY_OTP)
    suspend fun verifyOtp(
        @Header("language_code") languageCode: String,
        @Field("email") email: String,
        @Field("otp") otp: String,
    ): Response<CommonResponse>

    @FormUrlEncoded
    @POST(APIEndPoints.RESET_PASSWORD)
    suspend fun resetPassword(
        @Header("language_code") languageCode: String,
        @Field("email") email: String,
        @Field("new_password") newPassword: String,
    ): Response<CommonResponse>

    @FormUrlEncoded
    @POST(APIEndPoints.CREATE_OR_UPDATE_STAFF)
    suspend fun createOrUpdateStaff(
        @Header("token") token: String,
        @Header("language_code") languageCode: String,
        @Field("staff_member_id") staffMemberId: String,
        @Field("name") name: String,
        @Field("contact_number") contactNumber: String,
        @Field("email") email: String,
        @Field("is_active") isActive: Int,
    ): Response<AddStaffResponse>

    @FormUrlEncoded
    @POST(APIEndPoints.CREATE_OR_UPDATE_CUSTOMER)
    suspend fun createOrUpdateCustomer(
        @Header("token") token: String,
        @Header("language_code") languageCode: String,
        @Field("customer_id") customerId: String,
        @Field("name") name: String,
        @Field("address") address: String,
        @Field("contact_number") contactNumber: String,
        @Field("is_active") isActive: Int,
    ): Response<AddCustomerResponse>

    @FormUrlEncoded
    @POST(APIEndPoints.LOGOUT)
    suspend fun logout(
        @Header("token") token: String,
        @Header("language_code") languageCode: String,
        @Field("device_id") deviceId: String,
        @Field("user_id") userId: String,
        @Field("is_multiple_business_logout") isMultipleDeviceLogout: Boolean,
    ): Response<CommonResponse>

    @FormUrlEncoded
    @POST(APIEndPoints.BUSINESS_OWNER_UPDATE_PROFILE)
    suspend fun updateOwnerProfile(
        @Header("token") token: String,
        @Header("language_code") languageCode: String,
        @Field("business_owner_id") businessOwnerId: String,
        @Field("business_name") businessName: String,
        @Field("contact_number") contactNumber: String,
        @Field("country_code") countryCode: String,
        @Field("iso_code") isoCode: String,
        @Field("currency_symbol") currencySymbol: String,
        @Field("currency_country_name") currencyCountryName: String,
        @Field("device_id") deviceId: String,
        @Field("device_name") deviceName: String,
        @Field("subscription_id") subscriptionId: String,
    ): Response<RegisterResponse>

    @FormUrlEncoded
    @POST(APIEndPoints.SYNC_INIT)
    suspend fun syncInit(
        @Header("token") token: String,
        @Header("language_code") languageCode: String,
        @Field("last_sync_time") lastSyncTime: String,
        @Field("collection_name") collectionName: String,
    ): Response<SyncInitResponse>

    @FormUrlEncoded
    @POST(APIEndPoints.SYNC_TRANSLATION_INIT)
    suspend fun syncTranslationInit(
        @Header("token") token: String,
        @Header("language_code") languageCode: String,
        @Field("last_sync_time") lastSyncTime: String,
        @Field("collection_name") collectionName: String,
    ): Response<SyncInitResponse>

    @FormUrlEncoded
    @POST(APIEndPoints.SYNC_DATA)
    suspend fun syncData(
        @Header("token") token: String,
        @Header("language_code") languageCode: String,
        @Field("start_sync_time") syncStartTime: String,
        @Field("current_page") currentPage: Int,
        @Field("collection_name") collectionName: String,
        @Field("last_sync_time") lastSyncTime: String,
    ): Response<SyncDataResponse>

    @FormUrlEncoded
    @POST(APIEndPoints.SYNC_TRANSLATION_DATA)
    suspend fun syncTranslationData(
        @Header("token") token: String,
        @Header("language_code") languageCode: String,
        @Field("start_sync_time") syncStartTime: String,
        @Field("current_page") currentPage: Int,
        @Field("collection_name") collectionName: String,
        @Field("last_sync_time") lastSyncTime: String,
    ): Response<SyncDataResponse>

    @FormUrlEncoded
    @POST(APIEndPoints.SYNC_ORDER)
    suspend fun syncOrder(
        @Header("token") token: String,
        @Header("language_code") languageCode: String,
        @Field("orders") orders: String,
    ): Response<SyncOrderResponse>

    @FormUrlEncoded
    @POST(APIEndPoints.SYNC_PAYMENT)
    suspend fun syncPayments(
        @Header("token") token: String,
        @Header("language_code") languageCode: String,
        @Field("payments") payments: String,
    ): Response<SyncPaymentResponse>

    @GET(APIEndPoints.GENERATE_QR_CODE)
    suspend fun generateQRCode(
        @Header("token") token: String,
        @Header("language_code") languageCode: String,
        @Path("id") id: String,
    ): Response<GenerateQRCodeResponse>

    @FormUrlEncoded
    @POST(APIEndPoints.SCAN_QR)
    suspend fun scanQRCode(
        @Header("language_code") languageCode: String,
        @Field("user_id") userId: String,
        @Field("expire_time") expireTime: String,
        @Field("device_id") deviceId: String,
        @Field("device_name") deviceName: String,
        @Field("subscription_id") subscriptionId: String,
    ): Response<ScanQRResponse>

    @FormUrlEncoded
    @POST(APIEndPoints.CREATE_OR_UPDATE_PRODUCT)
    suspend fun createOrUpdateProduct(
        @Header("token") token: String,
        @Header("language_code") languageCode: String,
        @Field("product_id") productId: String,
        @Field("name") name: String,
        @Field("price") price: Float,
        @Field("unit") unit: String,
        @Field("weight") weight: String,
        @Field("is_active") isActive: Int,
    ): Response<AddProductResponse>

    @FormUrlEncoded
    @POST(APIEndPoints.APP_SETTINGS)
    suspend fun getAppSettings(
        @Header("language_code") languageCode: String,
        @Header("token") token: String,
        @Field("user_id") userId: String,
        @Field("device_id") deviceId: String,
    ): Response<SettingsResponse>

    @FormUrlEncoded
    @POST(APIEndPoints.CHANGE_PASSWORD)
    suspend fun changePassword(
        @Header("token") token: String,
        @Header("language_code") languageCode: String,
        @Field("old_password") oldPassword: String,
        @Field("new_password") newPassword: String,
    ): Response<ChangePasswordResponce>

    @GET(APIEndPoints.NOTIFICATION)
    suspend fun getNotification(
        @Header("token") token: String,
        @Header("language_code") languageCode: String,
        @Path("id") id: String,
    ): Response<NotificationResponce>

    @FormUrlEncoded
    @POST(APIEndPoints.UPDATE_NOTIFICATION)
    suspend fun updateNotification(
        @Header("token") token: String,
        @Header("language_code") languageCode: String,
        @Field("user_id") userId: String,
        @Field("order") order: Boolean,
        @Field("payment") payment: Boolean,
    ): Response<NotificationResponce>

    @FormUrlEncoded
    @POST(APIEndPoints.SESSION)
    suspend fun getSessionDetails(
        @Header("token") token: String,
        @Header("language_code") languageCode: String,
        @Field("user_id") userId: String,
    ): Response<SessionDataResponse>

    @FormUrlEncoded
    @POST(APIEndPoints.LOGOUT_ALL_DEVICE)
    suspend fun logoutAllDevice(
        @Header("token") token: String,
        @Header("language_code") languageCode: String,
        @Field("user_id") userId: String,
    ): Response<CommonResponse>

    @FormUrlEncoded
    @POST(APIEndPoints.PROFILE)
    suspend fun getProfile(
        @Header("token") token: String,
        @Header("language_code") languageCode: String,
        @Field("id") userId: String,
        @Field("type") userType: String,
    ): Response<ProfileResponce>


    @FormUrlEncoded
    @POST(APIEndPoints.LANGUAGE_LIST)
    suspend fun getLanguageList(
        @Header("token") token: String,
        @Field("user_id") userId: String,
        @Field("device_id") deviceId: String,
    ): Response<LanguageListResponce>
}