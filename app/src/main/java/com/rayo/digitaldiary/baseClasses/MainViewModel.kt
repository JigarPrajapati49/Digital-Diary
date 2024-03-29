package com.rayo.digitaldiary.baseClasses

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.rayo.digitaldiary.DigitalDiaryApplication
import com.rayo.digitaldiary.LanguageListResponce
import com.rayo.digitaldiary.api.APIRepository
import com.rayo.digitaldiary.api.ApiException
import com.rayo.digitaldiary.api.NetworkConnectionInterceptor
import com.rayo.digitaldiary.api.NoInternetException
import com.rayo.digitaldiary.api.UnauthorizedUserException
import com.rayo.digitaldiary.database.CustomerDao
import com.rayo.digitaldiary.database.CustomerLoginDao
import com.rayo.digitaldiary.database.LanguageTranslationDao
import com.rayo.digitaldiary.database.LanguagesDao
import com.rayo.digitaldiary.database.OrderDao
import com.rayo.digitaldiary.database.PaymentDao
import com.rayo.digitaldiary.database.ProductDao
import com.rayo.digitaldiary.database.StaffDao
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.PreferenceManager
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.LanguageData
import com.rayo.digitaldiary.ui.SettingsResponse
import com.rayo.digitaldiary.ui.languageTranslation.LanguageTranslationData
import com.rayo.digitaldiary.ui.order.OrderData
import com.rayo.digitaldiary.ui.order.SyncOrderResponse
import com.rayo.digitaldiary.ui.payment.PaymentData
import com.rayo.digitaldiary.ui.payment.SyncPaymentResponse
import com.rayo.digitaldiary.ui.product.Product
import com.rayo.digitaldiary.ui.product.ProductJsonExclusion
import com.rayo.digitaldiary.ui.profile.BusinessOwnerData
import com.rayo.digitaldiary.ui.profile.ProfileResponce
import com.rayo.digitaldiary.ui.sync.SyncData
import com.rayo.digitaldiary.ui.sync.SyncDataResponse
import com.rayo.digitaldiary.ui.sync.SyncInitResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.reflect.Type
import javax.inject.Inject

class ProgressData(
    val collectionName: String,
    val syncCount: Int = 0,
    val totalSyncCollection: Int
)

@HiltViewModel
open class MainViewModel @Inject constructor(
    private val apiRepository: APIRepository,
    private val preferenceManager: PreferenceManager,
    private val customerDao: CustomerDao,
    private val staffDao: StaffDao,
    private val productDao: ProductDao,
    private val orderDao: OrderDao,
    private val paymentDao: PaymentDao,
    private val languageTranslationDao: LanguageTranslationDao,
    private val languagesDao: LanguagesDao,
    private val customerLoginDao: CustomerLoginDao,
    networkInterceptor: NetworkConnectionInterceptor
) : BaseViewModel() {

    val syncSuccess = MutableLiveData<String>()
    private val ordersList: MutableList<OrderData> = ArrayList()
    private val paymentList: MutableList<PaymentData> = ArrayList()
    val languagesList = MutableLiveData<MutableList<LanguageData>>()
    val translationData = MutableLiveData<String>()
    val translationSyncProgress = MutableLiveData<Int>()
    private var currentAPIPage = 1
    private var totalPages = 0
    private var startSyncTime = ""
    var isFreshUser = false
    var isSyncAPICallWhileLogout = false
    private val TAG = MainViewModel::class.java.simpleName
    val syncCollectionCount = MutableLiveData<ProgressData>()
    private var currentSyncCount = 0
    private var totalSyncCollection = 0
    private var mCurrentCustomerId = ""

    init {
        // TODO increase count accordingly if added new sync collection
        totalSyncCollection = when (preferenceManager.getPref(Constants.prefUserType, "")) {
            Constants.USER_TYPE_CUSTOMER -> {
                6
            }

            else -> {
                8
            }
        }
        if (networkInterceptor.isInternetAvailable() && preferenceManager.getPref(Constants.prefToken, "").isEmpty()) {
            getAppSettings()
        }
    }

    fun getAppSettings() {
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.getAppSettings(
                        preferenceManager.getPref(Constants.prefLanguageCode, Constants.languageCodeDefault),
                        preferenceManager.getPref(Constants.prefUserId, ""),
                        preferenceManager.getPref(Constants.prefDeviceId, "")
                    )
                )
            } catch (e: ApiException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: NoInternetException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: UnauthorizedUserException) {
                unauthorizedUserHandler.postValue(Event(e.message))
            }
        }, { it ->
            if (it != null && it is SettingsResponse) {
                preferenceManager.savePref(
                    Constants.prefProductUnits,
                    it.settingsData?.productUnitsList?.toTypedArray().contentToString().replace("[", "").replace("]", "")
                )
                callLanguageListApi()
            }
        })
    }

    fun syncInit(collectionName: String, currentCustomerId: String = "") {
        // No need to assign blank string value to mCurrentCustomerId
        if (currentCustomerId.isNotEmpty()) {
            mCurrentCustomerId = currentCustomerId
        }
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.syncInit(
                        preferenceManager.getPref(Constants.prefToken, ""),
                        preferenceManager.getPref(Constants.prefLanguageCode, Constants.languageCodeDefault),
                        getLastSyncTime(collectionName),
                        collectionName
                    )
                )
            } catch (e: ApiException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: NoInternetException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: UnauthorizedUserException) {
                unauthorizedUserHandler.postValue(Event(e.message))
            }
        }, {
            if (it != null && it is SyncInitResponse) {
                totalPages = it.syncInitData?.totalPages ?: 0
                startSyncTime = it.syncInitData?.syncStartTime ?: ""
                Log.d(TAG, "SyncInitResponse: totalPages $totalPages")
                if (totalPages == 0) {
                    syncNextData(collectionName)
                } else {
                    syncData(startSyncTime, currentAPIPage, collectionName)
                }
            }
        })
    }

    fun syncInitTranslation(collectionName: String) {
        Log.d(TAG, "syncInitTranslation: collectionName- $collectionName")
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.syncTranslationInit(
                        preferenceManager.getPref(Constants.prefLanguageCode, Constants.languageCodeDefault),
                        getLastSyncTime(collectionName),
                        collectionName
                    )
                )
            } catch (e: ApiException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: NoInternetException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: UnauthorizedUserException) {
                errorMessage.postValue(Event((e.message)))
            }
        }, {
            if (it != null && it is SyncInitResponse) {
                totalPages = it.syncInitData?.totalPages ?: 0
                startSyncTime = it.syncInitData?.syncStartTime ?: ""
                Log.d(TAG, "SyncInitResponse: totalPages $totalPages")

                if (totalPages == 0) {
                    syncNextData(collectionName)
                } else {
                    syncTranslationData(startSyncTime, currentAPIPage, collectionName)
                }
            }
        })
    }

    private fun syncData(startSyncTime: String, currentPage: Int, collectionName: String) {
        Log.d(TAG, "syncData: collectionName $collectionName currentPage $currentPage startSyncTime $startSyncTime")
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.syncData(
                        preferenceManager.getPref(Constants.prefToken, ""),
                        preferenceManager.getPref(Constants.prefLanguageCode, Constants.languageCodeDefault),
                        startSyncTime,
                        currentPage,
                        collectionName,
                        getLastSyncTime(collectionName)
                    )
                )
            } catch (e: ApiException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: NoInternetException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: UnauthorizedUserException) {
                unauthorizedUserHandler.postValue(Event(e.message))
            }
        }, {
            if (it != null && it is SyncDataResponse) {
                currentAPIPage++
                addSyncDataToLocalDB(collectionName, it.syncData)
            }
        })
    }

    private fun syncTranslationData(startSyncTime: String, currentPage: Int, collectionName: String) {
        Log.d(TAG, "syncData: collectionName $collectionName currentPage $currentPage startSyncTime $startSyncTime")
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.syncTranslationData(
                        preferenceManager.getPref(Constants.prefLanguageCode, Constants.languageCodeDefault),
                        startSyncTime,
                        currentPage,
                        collectionName,
                        getLastSyncTime(collectionName)
                    )
                )
            } catch (e: ApiException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: NoInternetException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: UnauthorizedUserException) {
                unauthorizedUserHandler.postValue(Event(e.message))
            }
        }, {
            if (it != null && it is SyncDataResponse) {
                currentAPIPage++
                addSyncDataToLocalDB(collectionName, it.syncData)
            }
        })
    }

    private fun addSyncDataToLocalDB(collectionName: String, syncData: SyncData?) {
        Coroutines.ioThenMain({
            when (collectionName) {
                Constants.PRODUCT -> {
                    syncData?.productList?.let { productDao.insertAllProducts(it) }
                }

                Constants.STAFF -> {
                    Log.e(TAG, "StaffList: ====${syncData?.paymentHistoryList?.size}")
                    syncData?.staffList?.let { staffDao.insertAllStaff(it) }
                }

                Constants.CUSTOMER -> {
                    syncData?.customerList?.let { customerDao.insertAllCustomer(it) }
                }

                Constants.ORDER_HISTORY -> {
                    syncData?.orderHistoryList?.let {
                        orderDao.insertAllOrder(getUpdatedOrderHistoryList(it))
                    }
                }

                Constants.PAYMENT_HISTORY -> {
                    syncData?.paymentHistoryList?.let {
                        paymentDao.insertAllPayments(getUpdatedPaymentList(it))
                    }
                }

                Constants.LANGUAGE_TRANSLATION -> {
                    syncData?.languageTranslationList?.let {
                        languageTranslationDao.insertAllLanguageTranslation(it)
                    }
                }

                else -> {

                }
            }
        }, {
            Log.d(TAG, "addSyncDataToLocalDB: collectionName $collectionName currentAPIPage $currentAPIPage totalPages $totalPages")
            if (currentAPIPage > totalPages) {
                currentAPIPage = 1
                totalPages = 0
                startSyncTime = ""

                if (collectionName == Constants.LANGUAGE_TRANSLATION) {
                    translationData.postValue("success")
                }
                Coroutines.io {
                    syncData?.syncStartTime?.let { it1 -> saveLastSyncTime(collectionName, it1) }
                }
                syncNextData(collectionName)
            } else {
                if (collectionName == Constants.LANGUAGE_TRANSLATION) {
                    syncTranslationData(startSyncTime, currentAPIPage, collectionName)
                    if (isFreshUser) {
                        val progress = (currentAPIPage.toFloat() / totalPages * 100)
                        Log.e(TAG, "translationProgress: ----------------$progress")
                        translationSyncProgress.postValue(progress.toInt())
                    }
                } else {
                    syncData(startSyncTime, currentAPIPage, collectionName)
                }
            }
        })
    }

    private fun insertLanguagesListInDB(list: List<LanguageData>) {
        Log.e(TAG, "insertLanguagesListInDB: ------------${list.size}")
        Coroutines.ioThenMain({
            languagesDao.insertLanguagesListInDB(list)
        }, {
            Log.e(TAG, "insertLanguagesListInDB: $it")
        })
    }

    private fun syncNextData(collectionName: String) {
        if (collectionName != Constants.LANGUAGE_TRANSLATION && isFreshUser) {
            return
        }
        postSyncValue(collectionName)
        Log.d(TAG, "syncNextData: collectionName $collectionName")
        when (collectionName) {
            Constants.PRODUCT -> {
                syncInit(Constants.STAFF)
            }

            Constants.STAFF -> {
//                if(preferenceManager.getPref(Constants.prefUserType, "") == Constants.USER_TYPE_CUSTOMER) {
//                    syncInit(Constants.ORDER_HISTORY)
//                } else {
                syncInit(Constants.CUSTOMER)
//                }
            }

            Constants.CUSTOMER -> {
                syncInit(Constants.ORDER_HISTORY)
            }

            Constants.ORDER_HISTORY -> {
                syncInit(Constants.PAYMENT_HISTORY)
            }

            Constants.PAYMENT_HISTORY -> {
                syncInitTranslation(Constants.LANGUAGE_TRANSLATION)
            }

            Constants.LANGUAGE_TRANSLATION -> {
                Coroutines.ioThenMain({
                    getLanguageTranslation()
                }, {
                    it?.let { it1 -> Utils.saveLanguageTranslation(it1) }
                })
                if (preferenceManager.getPref(Constants.prefUserType, "") == Constants.USER_TYPE_CUSTOMER) {
                    syncNextData(Constants.NONE) // to finish sync and send to else condition to callProfileApi
                } else {
                    syncOrder()
                }
            }

            Constants.ORDER -> {
                syncPayment()
            }

            else -> {
                currentSyncCount = 0
                callProfileApi()
            }
        }
    }

    suspend fun getLanguageTranslation(): List<LanguageTranslationData> {
        return languageTranslationDao.getLanguageTranslation(
            preferenceManager.getPref(Constants.prefLanguageCode, Constants.languageCodeDefault)
        )
    }

    private suspend fun getLastSyncTime(collectionName: String): String {
        return when (collectionName) {
            Constants.PRODUCT -> {
                preferenceManager.getPref(Constants.prefProductLastSyncTime, "")
            }

            Constants.STAFF -> {
                preferenceManager.getPref(Constants.prefStaffLastSyncTime, "")
            }

            Constants.CUSTOMER -> {
                preferenceManager.getPref(Constants.prefCustomerLastSyncTime, "")
            }

            Constants.ORDER_HISTORY -> {
                preferenceManager.getPref(Constants.prefOrderHistoryLastSyncTime, "")
            }

            Constants.PAYMENT_HISTORY -> {
                preferenceManager.getPref(Constants.prefPaymentHistoryLastSyncTime, "")
            }

            Constants.LANGUAGE_TRANSLATION -> {
                preferenceManager.getPref(Constants.prefLanguageTranslationLastSyncTime, "")
            }

            else -> {
                ""
            }
        }
    }

    private suspend fun saveLastSyncTime(collectionName: String, lastSyncTime: String) {
        when (collectionName) {
            Constants.PRODUCT -> {
                preferenceManager.savePref(Constants.prefProductLastSyncTime, lastSyncTime)
            }

            Constants.STAFF -> {
                if (mCurrentCustomerId.isNotEmpty()) {
                    setCustomerStaffLastSyncTime(lastSyncTime)
                }
                preferenceManager.savePref(Constants.prefStaffLastSyncTime, lastSyncTime)
            }

            Constants.CUSTOMER -> {
                preferenceManager.savePref(Constants.prefCustomerLastSyncTime, lastSyncTime)
            }

            Constants.ORDER_HISTORY -> {
                if (mCurrentCustomerId.isNotEmpty()) {
                    setCustomerHistoryLastSyncTime(lastSyncTime)
                }
                preferenceManager.savePref(Constants.prefOrderHistoryLastSyncTime, lastSyncTime)
            }

            Constants.PAYMENT_HISTORY -> {
                if (mCurrentCustomerId.isNotEmpty()) {
                    setCustomerPaymentHistoryLastSyncTime(lastSyncTime)
                }
                preferenceManager.savePref(Constants.prefPaymentHistoryLastSyncTime, lastSyncTime)
            }

            Constants.LANGUAGE_TRANSLATION -> {
                preferenceManager.savePref(Constants.prefLanguageTranslationLastSyncTime, lastSyncTime)
            }
        }
    }

    fun syncOrder() {
        Coroutines.ioThenMain({
            orderDao.getNotSyncOrder()
        }, {
            if (it != null) {
                for (item in it) {
                    val type: Type = object : TypeToken<List<Product?>?>() {}.type
                    item.product = Gson().fromJson(item.products, type)
                    ordersList.add(item)
                }
            }
            callSyncOrderAPI()
        })
    }

    private fun callSyncOrderAPI() {
        Log.d(TAG, "syncData: callSyncOrderAPI ordersList size ${ordersList.size}")
        if (ordersList.isEmpty()) {
            syncNextData(Constants.ORDER)
            return
        }
        val sendOrderList: MutableList<OrderData> = ArrayList()
        if (ordersList.size > Constants.SYNC_ORDER_COUNT) {
            sendOrderList.addAll(ordersList.subList(0, Constants.SYNC_ORDER_COUNT))
            ordersList.removeAll(sendOrderList)
            Log.d(TAG, "syncData: callSyncOrderAPI updated size after record removed ${ordersList.size}")
        } else {
            sendOrderList.addAll(ordersList)
            ordersList.clear()
        }
        Log.d(TAG, "syncData: callSyncOrderAPI syncOrderCount ${ordersList.size}  ordersList size ${ordersList.size}")

        val gson = GsonBuilder()
            .setExclusionStrategies(ProductJsonExclusion())
            .create()
        Log.d(TAG, "SyncData: callSyncOrderAPI: orders ${gson.toJson(sendOrderList)}")
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.syncOrder(
                        preferenceManager.getPref(Constants.prefToken, ""),
                        preferenceManager.getPref(Constants.prefLanguageCode, Constants.languageCodeDefault),
                        gson.toJson(sendOrderList)
                    )
                )
            } catch (e: ApiException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: NoInternetException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: UnauthorizedUserException) {
                unauthorizedUserHandler.postValue(Event(e.message))
            }
        }, {
            if (it != null && it is SyncOrderResponse) {
                updateSyncOrderInDB(getUpdatedOrderHistoryList(it.orderList))
            }
        })
    }

    fun syncPayment() {
        Coroutines.ioThenMain({
            paymentDao.getNotSyncPaymentOrder()
        }, {
            it?.let {
                paymentList.addAll(it)
                callSyncPaymentAPI()
            }
        })
    }

    private fun callSyncPaymentAPI() {
        Log.d(TAG, "syncData: callSyncOrderAPI ordersList size ${paymentList.size}")
        if (paymentList.isEmpty()) {
            syncNextData(Constants.PAYMENT)
            return
        }
        val sendPaymentList: MutableList<PaymentData> = ArrayList()
        if (paymentList.size > Constants.SYNC_PAYMENT_COUNT) {
            sendPaymentList.addAll(paymentList.subList(0, Constants.SYNC_PAYMENT_COUNT))
            paymentList.removeAll(sendPaymentList)
            Log.d(TAG, "syncData: callSyncPaymentAPI updated size after record removed ${paymentList.size}")
        } else {
            sendPaymentList.addAll(paymentList)
            paymentList.clear()
        }
        Log.d(TAG, "syncData: callSyncPaymentAPI syncPaymentCount ${paymentList.size}")

        val gson = GsonBuilder()
            .setExclusionStrategies(ProductJsonExclusion())
            .create()
        Log.d(TAG, "SyncData: callSyncPaymentAPI: Payments ${gson.toJson(sendPaymentList)}")
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.syncPayment(
                        preferenceManager.getPref(Constants.prefToken, ""),
                        preferenceManager.getPref(Constants.prefLanguageCode, Constants.languageCodeDefault),
                        gson.toJson(sendPaymentList)
                    )
                )
            } catch (e: ApiException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: NoInternetException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: UnauthorizedUserException) {
                unauthorizedUserHandler.postValue(Event(e.message))
            }
        }, {
            if (it != null && it is SyncPaymentResponse) {
                updateSyncPaymentInDB(getUpdatedPaymentList(it.paymentList))
            }
        })
    }

    private fun updateSyncOrderInDB(orderList: List<OrderData>) {
        Coroutines.ioThenMain({
            orderDao.insertAllOrder(orderList)
        }, {
            Log.e(TAG, "insertSyncOrderInDB: -----$it")
            if (ordersList.isEmpty()) {
                syncNextData(Constants.ORDER)
            } else {
                callSyncOrderAPI()
            }
        })
    }

    private fun updateSyncPaymentInDB(paymentList: List<PaymentData>) {
        Coroutines.ioThenMain({
            paymentDao.insertAllPayments(paymentList)
        }, {
            Log.e(TAG, "updateSyncPaymentInDB: -----$it")
            if (paymentList.isEmpty()) {
                syncNextData(Constants.PAYMENT)
            } else {
                callSyncPaymentAPI()
            }
        })
    }

    private fun postSyncValue(collectionName: String) {
        currentSyncCount++
        syncCollectionCount.postValue(ProgressData(collectionName, currentSyncCount, totalSyncCollection))
    }

    private fun getUpdatedOrderHistoryList(orderList: List<OrderData>): List<OrderData> {
        val updateList: ArrayList<OrderData> = ArrayList()
        for (updateSync in orderList) {
            updateSync.sync = 1
            updateSync.products = Utils.getOrderJson(updateSync.product)
            updateList.add(updateSync)
        }
        return updateList
    }

    private fun getUpdatedPaymentList(paymentList: List<PaymentData>): List<PaymentData> {
        val updateList: ArrayList<PaymentData> = ArrayList()
        for (updateSync in paymentList) {
            updateSync.sync = 1
            updateList.add(updateSync)
        }
        return updateList
    }

    private suspend fun setCustomerHistoryLastSyncTime(lastSyncTime: String) {
        customerLoginDao.setCustomerHistoryLastSyncTime(mCurrentCustomerId, lastSyncTime)
    }

    private suspend fun setCustomerPaymentHistoryLastSyncTime(lastSyncTime: String) {
        customerLoginDao.setCustomerPaymentHistoryLastSyncTime(mCurrentCustomerId, lastSyncTime)
    }

    private suspend fun setCustomerStaffLastSyncTime(lastSyncTime: String) {
        customerLoginDao.setCustomerStaffLastSyncTime(mCurrentCustomerId, lastSyncTime)
    }

    private fun callProfileApi() {
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.getProfile(
                        preferenceManager.getPref(Constants.prefToken, ""),
                        preferenceManager.getPref(Constants.prefLanguageCode, Constants.languageCodeDefault),
                        preferenceManager.getPref(Constants.prefUserId, ""),
                        preferenceManager.getPref(Constants.prefUserType, "")
                    )
                )
            } catch (e: ApiException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: NoInternetException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: UnauthorizedUserException) {
                unauthorizedUserHandler.postValue(Event(e.message))
            }
        }, {
            if (it != null && it is ProfileResponce) {
                with(preferenceManager) {
                    savePref(Constants.prefCountryCode, it.data?.businessOwnerData?.countryCode)
                    savePref(Constants.prefIsoCode, it.data?.businessOwnerData?.isoCode)
                    savePref(Constants.prefCurrencySymbol, it.data?.businessOwnerData?.currencySymbol)
                    savePref(Constants.prefCurrencyCountry, it.data?.businessOwnerData?.currencyCountryName)
                    if (getPref(Constants.prefUserType, "") == Constants.USER_TYPE_OWNER) {
                        savePref(Constants.prefContactNumber, it.data?.businessOwnerData?.contactNumber)
                        savePref(Constants.prefBusinessName, it.data?.businessOwnerData?.businessName)
                        savePref(Constants.prefEmail, it.data?.businessOwnerData?.email)
                    } else {
                        savePref(Constants.prefOwnerContactNumber, it.data?.businessOwnerData?.contactNumber)
                        savePref(Constants.prefBusinessName, it.data?.businessOwnerData?.businessName)
                        savePref(Constants.prefOwnerEmail, it.data?.businessOwnerData?.email)
                        if (getPref(Constants.prefUserType, "") == Constants.USER_TYPE_STAFF) {
                            savePref(Constants.prefName, it.data?.staffData?.name)
                            savePref(Constants.prefContactNumber, it.data?.staffData?.contactNumber)
                            savePref(Constants.prefEmail, it.data?.staffData?.email)
                            savePref(Constants.prefIsActive, it.data?.staffData?.active)
                        } else {
                            savePref(Constants.prefName, it.data?.customerData?.name)
                            savePref(Constants.prefContactNumber, it.data?.customerData?.contactNumber)
                            savePref(Constants.prefAddress, it.data?.customerData?.address)
                            savePref(Constants.prefIsActive, it.data?.customerData?.active)
                            updateCustomerDetailInDB(it.data?.businessOwnerData, it.data?.customerData?.id)
                        }
                    }
                }
            }
            syncSuccess.postValue(Utils.getTranslatedValue("sync_Success"))
        })
    }

    private fun updateCustomerDetailInDB(businessOwnerData: BusinessOwnerData?, customerId: String?) {
        businessOwnerData?.let { mBusinessOwnerData ->
            customerId?.let { mCustomerId ->
                with(mBusinessOwnerData) {
                    Coroutines.io {
                        customerLoginDao.updateCustomerCurrencyData(currencyCountryName, currencySymbol, countryCode, mCustomerId)
                    }
                }
            }
        }
    }

    private fun callLanguageListApi() {
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.getLanguageList(
                        "c0640735-1dfe-2fd1-51c4-c60d28963f68",
                        if (preferenceManager.getPref(Constants.prefToken, "").isEmpty()) {
                            ""
                        } else {
                            preferenceManager.getPref(Constants.prefUserId, "")
                        },
                        Utils.generateUUIDForDeviceId(preferenceManager)
                    )
                )
            } catch (e: ApiException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: NoInternetException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: UnauthorizedUserException) {
                unauthorizedUserHandler.postValue(Event(e.message))
            }

        }, {
            if (it != null && it is LanguageListResponce) {
                it.data?.let { it1 ->
                    insertLanguagesListInDB(it1.languageList)
                    val languageList: MutableList<LanguageData> = ArrayList()
                    for (item in it.data.languageList) {
                        if (item.active == 1) {
                            languageList.add(item)
                        }
                    }
                    languagesList.postValue(languageList)
                }
            }
        })
    }
}