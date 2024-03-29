package com.rayo.digitaldiary.baseClasses

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.onesignal.OneSignal
import com.rayo.digitaldiary.DigitalDiaryApplication
import com.rayo.digitaldiary.api.APIRepository
import com.rayo.digitaldiary.api.NetworkConnectionInterceptor
import com.rayo.digitaldiary.database.DigitalDiaryDatabase
import com.rayo.digitaldiary.helper.*
import com.rayo.digitaldiary.ui.customer.CustomerMainActivity
import com.rayo.digitaldiary.ui.login.AuthenticationActivity
import com.rayo.digitaldiary.ui.login.ScanQRData
import javax.inject.Inject

abstract class BaseActivity<VM : ViewModel?> : AppCompatActivity() {

    private var myProgressDialog: MyProgressDialog? = null
    abstract val viewModel: VM

    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Inject
    lateinit var networkInterceptor: NetworkConnectionInterceptor

    @Inject
    lateinit var dialogHelper: DialogHelper

    @Inject
    lateinit var apiRepository: APIRepository

    @Inject
    lateinit var localDatabaseInstance: DigitalDiaryDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (viewModel as BaseViewModel?)?.errorMessage?.observe(this) {
            dismissProgressDialog()
            it.getContentIfNotHandled()?.let { error ->
                toast(error)
            }
        }
        (viewModel as BaseViewModel?)?.unauthorizedUserHandler?.observe(this) {
            dismissProgressDialog()
            logoutSuccess()
            it.getContentIfNotHandled()?.let { error ->
                toast(error)
            }
        }
        (viewModel as BaseViewModel?)?.logoutSuccess?.observe(this) {
            dismissProgressDialog()
            logoutSuccess()
        }
    }

    fun <T : ViewDataBinding> setDataBindingView(layoutId: Int): T {
        return DataBindingUtil.setContentView(this@BaseActivity, layoutId)
    }

    fun showProgressDialog() {
        if (myProgressDialog != null) {
            myProgressDialog?.show()
        } else {
            myProgressDialog = MyProgressDialog.show(this, "")
        }
    }

    fun dismissProgressDialog() {
        myProgressDialog?.dismiss()
    }

    fun callLogoutAPI() {
        showProgressDialog()
        (viewModel as BaseViewModel?)?.callLogoutAPI(apiRepository, preferenceManager)
    }

    fun logoutSuccess() {
        Coroutines.ioThenMain({
            localDatabaseInstance.customerLoginDao().getCustomerLoginDetails()
        }, {loginCustomerList ->
            val mLoginCustomerList : MutableList<ScanQRData> = ArrayList()
            loginCustomerList?.let { mLoginCustomerList.addAll(it) }
            if (preferenceManager.getPref(Constants.prefUserType, "") == Constants.USER_TYPE_CUSTOMER && mLoginCustomerList.size > 1
                && !DigitalDiaryApplication.instance.isLogoutFromSettings) {
                DigitalDiaryApplication.instance.isLogoutFromSettings = false
                Coroutines.ioThenMain({
                    for(item in mLoginCustomerList) {
                        if(item.userId == DigitalDiaryApplication.instance.currentCustomerId) {
                            localDatabaseInstance.customerLoginDao().deleteById(item.userId)
                            mLoginCustomerList.remove(item)
                            break
                        }
                    }
                }, {
                    dismissProgressDialog()
                    DigitalDiaryApplication.instance.currentCustomerId = mLoginCustomerList[0].userId
                    saveCurrentSelectedCustomerData(mLoginCustomerList[0])
                    (this as CustomerMainActivity).moveToDashboard()
                })
            } else {
                DigitalDiaryApplication.instance.isLogoutFromSettings = false
                (viewModel as BaseViewModel?)?.clearAllTableFromDatabase(localDatabaseInstance)
                val prefKeyList = ArrayList<String>()
                prefKeyList.add(Constants.prefLanguageCode)
                prefKeyList.add(Constants.prefLanguage)
                prefKeyList.add(Constants.prefLanguageTranslationLastSyncTime)
                preferenceManager.clearAll(prefKeyList)
                OneSignal.removeExternalUserId()
                startActivity(Intent(this, AuthenticationActivity::class.java))
                finish()
            }
        })
    }

    private fun showPermissionSetting() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    /* Customer login -> this will save data for current selected business */
    fun saveCurrentSelectedCustomerData(scanData: ScanQRData) {
        with(preferenceManager) {
            savePref(Constants.prefToken, scanData.token)
            savePref(Constants.prefUserId, scanData.userId)
            savePref(Constants.prefContactNumber, scanData.contactNumber)
            savePref(Constants.prefAddress, scanData.address)
            savePref(Constants.businessName, scanData.businessName)
            savePref(Constants.prefStaffLastSyncTime, scanData.staffLastSyncTime)
            savePref(Constants.prefOrderHistoryLastSyncTime, scanData.orderHistoryLastSyncTime)
            savePref(Constants.prefPaymentHistoryLastSyncTime, scanData.paymentHistoryLastSyncTime)
            savePref(Constants.prefCurrencySymbol, scanData.currencySymbol)
            savePref(Constants.prefCountryCode, scanData.countryCode)
            savePref(Constants.prefCurrencyCountry, scanData.currencyCountryName)
        }
    }
}