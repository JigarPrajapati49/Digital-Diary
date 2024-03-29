package com.rayo.digitaldiary.api

import com.rayo.digitaldiary.LanguageListResponce
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.ui.SettingsResponse
import com.rayo.digitaldiary.ui.customer.AddCustomerResponse
import com.rayo.digitaldiary.ui.customer.CustomerData
import com.rayo.digitaldiary.ui.login.GoogleLoginResponse
import com.rayo.digitaldiary.ui.login.RegisterResponse
import com.rayo.digitaldiary.ui.login.ScanQRResponse
import com.rayo.digitaldiary.ui.order.SyncOrderResponse
import com.rayo.digitaldiary.ui.payment.SyncPaymentResponse
import com.rayo.digitaldiary.ui.product.AddProductResponse
import com.rayo.digitaldiary.ui.product.Product
import com.rayo.digitaldiary.ui.profile.ProfileResponce
import com.rayo.digitaldiary.ui.profile.SessionDataResponse
import com.rayo.digitaldiary.ui.settings.ChangePasswordResponce
import com.rayo.digitaldiary.ui.settings.NotificationResponce
import com.rayo.digitaldiary.ui.staff.AddStaffResponse
import com.rayo.digitaldiary.ui.staff.GenerateQRCodeResponse
import com.rayo.digitaldiary.ui.staff.StaffData
import com.rayo.digitaldiary.ui.sync.SyncDataResponse
import com.rayo.digitaldiary.ui.sync.SyncInitResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class APIRepository @Inject constructor(private var client: APIInterface) : APIRequest() {

    suspend fun register(
        languageCode: String,
        email: String,
        password: String,
        contactNumber: String,
        countryCode: String,
        isoCode: String,
        businessName: String,
        deviceId: String,
        deviceName: String,
        subscriptionId: String,
    ): RegisterResponse = apiRequest {
        client.register(
            languageCode,
            email,
            password,
            Constants.EMAIL,
            contactNumber,
            countryCode,
            isoCode,
            businessName,
            deviceId,
            deviceName, subscriptionId
        )
    }

    suspend fun login(
        languageCode: String,
        email: String,
        password: String,
        deviceId: String,
        deviceName: String,
        subscriptionId: String,
    ): RegisterResponse =
        apiRequest {
            client.login(
                languageCode,
                email,
                password,
                Constants.EMAIL,
                deviceId,
                deviceName, subscriptionId
            )
        }

    suspend fun loginWithGoogle(
        languageCode: String,
        googleId: String,
        email: String,
        deviceId: String,
        deviceName: String,
        subscriptionId: String
    ): GoogleLoginResponse =
        apiRequest {
            client.loginWithGoogle(
                languageCode,
                googleId,
                email,
                Constants.GOOGLE,
                deviceId,
                deviceName,
                subscriptionId
            )
        }

    suspend fun completeLoginWithGoogle(
        token: String,
        languageCode: String,
        businessName: String,
        contactNumber: String,
        countryCode: String,
        isoCode: String,
        deviceId: String,
        deviceName: String,
        subscriptionId: String,
    ): GoogleLoginResponse = apiRequest {
        client.completeLoginWithGoogle(
            token,
            languageCode,
            businessName,
            contactNumber,
            countryCode,
            isoCode,
            deviceId,
            deviceName, subscriptionId
        )
    }

    suspend fun forgotPassword(languageCode: String, email: String): CommonResponse =
        apiRequest { client.forgotPassword(languageCode, email) }

    suspend fun verifyOtp(languageCode: String, email: String, otp: String): CommonResponse =
        apiRequest { client.verifyOtp(languageCode, email, otp) }

    suspend fun resetPassword(
        languageCode: String,
        email: String,
        newPassword: String,
    ): CommonResponse = apiRequest { client.resetPassword(languageCode, email, newPassword) }

    suspend fun createOrUpdateStaff(
        token: String,
        languageCode: String,
        staffData: StaffData,
    ): AddStaffResponse = apiRequest {
        client.createOrUpdateStaff(
            token,
            languageCode,
            staffData.id,
            staffData.name,
            staffData.contactNumber,
            staffData.email,
            staffData.active,
        )
    }

    suspend fun createOrUpdateCustomer(
        token: String,
        languageCode: String,
        customerData: CustomerData,
    ): AddCustomerResponse = apiRequest {
        client.createOrUpdateCustomer(
            token,
            languageCode,
            customerData.id,
            customerData.name,
            customerData.address,
            customerData.contactNumber,
            customerData.active
        )
    }

    suspend fun logout(
        token: String,
        languageCode: String,
        deviceId: String,
        userId: String,
        isMultipleDeviceLogout: Boolean = false
    ): CommonResponse =
        apiRequest { client.logout(token, languageCode, deviceId, userId, isMultipleDeviceLogout) }

    suspend fun updateOwnerProfile(
        token: String,
        languageCode: String,
        businessOwnerId: String,
        businessName: String,
        contactNumber: String,
        countryCode: String,
        isoCode: String,
        currencySymbol: String,
        currencyCountryName: String,
        deviceId: String,
        deviceName: String,
        subscriptionId: String
    ): RegisterResponse = apiRequest {
        client.updateOwnerProfile(
            token,
            languageCode,
            businessOwnerId,
            businessName,
            contactNumber,
            countryCode,
            isoCode,
            currencySymbol,
            currencyCountryName,
            deviceId,
            deviceName,subscriptionId
        )
    }

    suspend fun syncInit(
        token: String,
        languageCode: String,
        lastSyncTime: String,
        collectionName: String,
    ): SyncInitResponse =
        apiRequest { client.syncInit(token, languageCode, lastSyncTime, collectionName) }

    suspend fun syncTranslationInit(
        languageCode: String,
        lastSyncTime: String,
        collectionTime: String,
    ): SyncInitResponse = apiRequest {
        client.syncTranslationInit(
            APIEndPoints.STATIC_TOKEN, languageCode, lastSyncTime, collectionTime
        )
    }

    suspend fun syncData(
        token: String,
        languageCode: String,
        syncStartTime: String,
        currentPage: Int,
        collectionName: String,
        lastSyncTime: String,
    ): SyncDataResponse = apiRequest {
        client.syncData(
            token, languageCode, syncStartTime, currentPage, collectionName, lastSyncTime
        )
    }

    suspend fun syncTranslationData(
        languageCode: String,
        syncStartTime: String,
        currentPage: Int,
        collectionName: String,
        lastSyncTime: String,
    ): SyncDataResponse = apiRequest {
        client.syncTranslationData(
            APIEndPoints.STATIC_TOKEN,
            languageCode,
            syncStartTime,
            currentPage,
            collectionName,
            lastSyncTime
        )
    }

    suspend fun syncOrder(token: String, languageCode: String, orders: String): SyncOrderResponse =
        apiRequest {
            client.syncOrder(token, languageCode, orders)
        }

    suspend fun syncPayment(
        token: String,
        languageCode: String,
        payments: String,
    ): SyncPaymentResponse =
        apiRequest {
            client.syncPayments(token, languageCode, payments)
        }

    suspend fun generateQRCode(
        token: String,
        languageCode: String,
        id: String,
    ): GenerateQRCodeResponse = apiRequest { client.generateQRCode(token, languageCode, id) }

    suspend fun scanQRCode(
        languageCode: String,
        userId: String,
        expireTime: String,
        deviceId: String,
        deviceName: String,
        subscriptionId: String,
    ): ScanQRResponse =
        apiRequest {
            client.scanQRCode(
                languageCode,
                userId,
                expireTime,
                deviceId,
                deviceName,
                subscriptionId
            )
        }

    suspend fun createOrUpdateProduct(
        token: String,
        languageCode: String,
        product: Product,
    ): AddProductResponse = apiRequest {
        client.createOrUpdateProduct(
            token,
            languageCode,
            product.id,
            product.name,
            product.price.toFloat(),
            product.unit,
            product.weight,
            product.active
        )
    }

    suspend fun getAppSettings(
        languageCode: String,
        userId: String,
        deviceId: String,
    ): SettingsResponse =
        apiRequest {
            client.getAppSettings(
                languageCode,
                APIEndPoints.STATIC_TOKEN,
                userId,
                deviceId
            )
        }

    suspend fun changePassword(
        token: String,
        languageCode: String,
        oldPassword: String,
        newPassword: String,
    ): ChangePasswordResponce =
        apiRequest { client.changePassword(token, languageCode, oldPassword, newPassword) }

    suspend fun getNotification(
        token: String,
        languageCode: String,
        id: String,
    ): NotificationResponce = apiRequest { client.getNotification(token, languageCode, id) }

    suspend fun updateNotification(
        token: String,
        languageCode: String,
        id: String,
        order: Boolean,
        payments: Boolean,

        ): NotificationResponce =
        apiRequest { client.updateNotification(token, languageCode, id, order, payments) }

    suspend fun getSessionDetail(
        token: String,
        languageCode: String,
        userId: String,
    ): SessionDataResponse = apiRequest { client.getSessionDetails(token, languageCode, userId) }

    suspend fun logoutAllDevice(
        token: String,
        languageCode: String,
        userId: String,
    ): CommonResponse = apiRequest { client.logoutAllDevice(token, languageCode, userId) }

    suspend fun getProfile(
        token: String,
        languageCode: String,
        userId: String,
        userType: String,
    ): ProfileResponce = apiRequest { client.getProfile(token, languageCode, userId, userType) }

    suspend fun getLanguageList(
        token: String,
        userId: String,
        deviceId: String
    ): LanguageListResponce = apiRequest { client.getLanguageList(token,userId,deviceId) }
}

