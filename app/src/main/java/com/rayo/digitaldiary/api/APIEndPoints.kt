package com.rayo.digitaldiary.api

object APIEndPoints {

    const val STATIC_TOKEN = "c0640735-1dfe-2fd1-51c4-c60d28963f68"

    /* Authentication */
    const val LOGIN = "v1/users/login"
    const val REGISTER = "v1/users/register"
    const val GOOGLE_LOGIN = "v1/users/google"
    const val COMPLETE_GOOGLE_LOGIN = "v1/users/complete-google-sign-in"
    const val FORGOT_PASSWORD = "v1/users/forgot-password"
    const val VERIFY_OTP = "v1/users/verify-otp"
    const val RESET_PASSWORD = "v1/users/reset-password"
    const val CHANGE_PASSWORD = "v1/users/change-password"

    /* Profile */
    const val PROFILE = "v1/users/user"

    /* Business Owner */
    const val BUSINESS_OWNER_UPDATE_PROFILE = "v1/users/update-profile"

    /*Product*/
    const val CREATE_OR_UPDATE_PRODUCT = "v1/product/create-or-update"

    /* setting */
    const val APP_SETTINGS = "v1/app/app-setting"

    /* notification */
    const val NOTIFICATION = "v1/notification/{id}"
    const val UPDATE_NOTIFICATION = "v1/notification/update"

    /* Staff */
    const val CREATE_OR_UPDATE_STAFF = "v1/staff/create-or-update"
    const val UPDATE_PROFILE = "v1/staff/update-profile"

    /* Customer */
    const val CREATE_OR_UPDATE_CUSTOMER = "v1/customer/create-or-update"

    /* Sync */
    const val SYNC_INIT = "v1/sync/init"
    const val SYNC_DATA = "v1/sync/data"
    const val SYNC_ORDER = "v1/sync/order"
    const val SYNC_PAYMENT = "v1/sync/payment"
    const val SYNC_TRANSLATION_INIT = "v1/sync/translation-init"
    const val SYNC_TRANSLATION_DATA = "v1/sync/translation-data"

    /* Generate QR Code*/
    const val GENERATE_QR_CODE = "v1/generate/generate-qr/{id}"
    const val SCAN_QR = "v1/generate/scan-qr"

    /*Session */
    const val SESSION = "v1/users/sessions"
    const val LOGOUT_ALL_DEVICE = "v1/users/logout-from-all-device"

    const val LOGOUT = "v1/users/logout"

    /* LanguageList */
    const val LANGUAGE_LIST = "/v1/app/languages"
}