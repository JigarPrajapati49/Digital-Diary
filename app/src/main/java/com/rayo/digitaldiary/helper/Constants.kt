package com.rayo.digitaldiary.helper


object Constants {

    const val ONESIGNAL_APP_ID = "bf8451cc-25d5-4573-9ddb-dce4d43458f3"

    /*API response code */
    const val STATUS_SUCCESS = 1
    const val STATUS_UNAUTHORIZED = 401
    const val STATUS_200 = 200

    /* preference */
    const val prefToken = "token"
    const val prefUserId = "userId"
    const val prefEmail = "email"
    const val prefLoginType = "loginType"
    const val prefOwnerEmail = "ownerEmail"
    const val prefOwnerContactNumber = "ownerContactNumber"
    const val prefContactNumber = "contactNumber"
    const val prefCountryCode = "CountryCode"
    const val prefIsoCode = "IsoCode"
    const val prefBusinessName = "businessName"
    const val prefCurrencySymbol = "currencySymbol"
    const val prefCurrencyCountry = "currencyCountry"
    const val prefDeviceId = "deviceId"
    const val prefDeviceName = "deviceName"
    const val prefIsNewUser = "isNewUser"
    const val prefProductLastSyncTime = "productLastSyncTime"
    const val prefStaffLastSyncTime = "staffLastSyncTime"
    const val prefCustomerLastSyncTime = "customerLastSyncTime"
    const val prefOrderHistoryLastSyncTime = "orderHistoryLastSyncTime"
    const val prefPaymentHistoryLastSyncTime = "paymentHistoryLastSyncTime"
    const val prefLanguageTranslationLastSyncTime = "languageTranslationLastSyncTime"
    const val prefLanguage = "language"
    const val prefLanguageCode = "languageCode"
    const val prefProductUnits = "productUnits"
    const val prefName = "name"
    const val prefAddress = "address"
    const val prefIsActive = "isActive"
    const val prefUserType = "userType"

    /* JSON string */
    const val userId = "user_id"
    const val expireTime = "expire_time"

    /* user type key */
    const val USER_TYPE_CUSTOMER = "customer"
    const val USER_TYPE_STAFF = "staff_member"
    const val USER_TYPE_OWNER = "business_owner"

    const val languageDefault = "English"
    const val languageCodeDefault = "en"

    const val UTCTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val orderDisplayDateFormat = "dd-MM-yyyy"
    const val yearFormat = "yyyy-MM-dd"
    const val dateTimeFormat = "dd-MM-yyyy h:mm a"
    const val dateFormat = "LLLL dd, yyyy"
    const val monthYearFormat = "MMMM yyyy"
    const val onlyYearFormat = "yyyy"
    const val dateTimeSecondFormat = "dd-MM-yyyy h:mm a"
    const val orderTimeFormat = "hh:mm a"

    /* Intent */
    const val email = "email"
    const val notificationType = "notificationType"
    const val staffDetail = "staffDetail"
    const val customerDetail = "customerDetail"
    const val customerData = "customerData"
    const val createdById = "createById"
    const val customerId = "customerId"
    const val staffId = "staffId"
    const val historyId = "historyId"
    const val orderData = "orderData"
    const val historyType = "historyType"
    const val businessName = "businessName"
    const val localOrderId = "orderID"
    const val customerName = "customerName"
    const val historySummaryData = "historySummaryData"
    const val isFromNotification = "isFromNotification"
    const val customerWithHistory = "customerWithHistory"
    const val paymentData = "paymentData"
    const val addByUserName = "addByUserName"
    const val createdByStaffData = "createdByStaffData"
    const val cancelledByStaffData = "cancelledByStaffData"
    var localPaymentId = "localPaymentId"
    const val requestPaymentId = "requestPaymentId"

    /* Dashboard */
    const val DASHBOARD_STAFF_ID = 1
    const val DASHBOARD_CUSTOMER_ID = 2
    const val DASHBOARD_ADD_PRODUCT_ID = 3
    const val DASHBOARD_ADD_ORDER_ID = 4
    const val DASHBOARD_ORDER_HISTORY = 5
    const val DASHBOARD_OWNER_DETAIL = 6
    const val DASHBOARD_PROFILE = 7
    const val DASHBOARD_ADD_PAYMENT = 8
    const val DASHBOARD_REPORT = 9

    /* Add Product*/
    const val STATES_ALL = 2
    const val STATES_ACTIVE = 1
    const val STATES_INACTIVE = 0

    /* Bottom Navigation Index */
    const val INDEX_DASHBOARD = 0
    const val INDEX_PRODUCT = 1
    const val INDEX_CUSTOMER = 2
    const val INDEX_SETTINGS = 3

    /* Paging count limit */
    /* TODO verify page count */
    const val SYNC_ORDER_COUNT = 100
    const val SYNC_PAYMENT_COUNT = 100
    const val PAYMENT_RECORD_PER_PAGE = 100

    /* Collections Type */
    const val PRODUCT = "product"
    const val STAFF = "staff"
    const val CUSTOMER = "customer"
    const val LANGUAGE_TRANSLATION = "language_translation"
    const val ORDER_HISTORY = "order_history" // get all order from backend to show offline history
    const val PAYMENT_HISTORY = "payment_history" // get all payments from backend to show offline history
    const val ORDER = "order" // to sync createdOrder in backend
    const val PAYMENT = "payment" // to sync payments in backend
    const val NONE = "none"

    /* Order Status */
    const val COMPLETED = "completed"
    const val CANCELLED = "cancelled"
    const val ALL = "all"
    const val STATUS_ALL = 2

    /* Customer Filter */
    const val CREATED_AT = "createdAt"
    const val DUE_AMOUNT_FIRST = "dueAmountFirst"
    const val DUE_AMOUNT_LAST = "dueAmountLast"
    const val A_TO_Z = "aToz"
    const val Z_TO_A = "zToa"

    /* Date filter */
    const val DEFAULT = "default"
    const val ASCENDING = "ascending"
    const val DESCENDING = "descending"

    /* Payment Mode */
    const val ONLINE = "ONLINE"
    const val CASH = "CASH"

    /* Fetch order type */
    const val DAILY = 0
    const val MONTHLY = 1
    const val YEARLY = 2

    /* Login type */
    const val EMAIL = "EMAIL"
    const val GOOGLE = "GOOGLE"

    /* Notification type */
    const val NOTIFICATION_TYPE = "notification_type"

    /* Delete Account Url */
    const val DELETE_ACCOUNT = "https://admin.digital-diary.rayoinnovations.com/user/delete-account"
}
