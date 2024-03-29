package com.rayo.digitaldiary.ui.customer

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.rayo.digitaldiary.DigitalDiaryApplication
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseActivity
import com.rayo.digitaldiary.database.StaffWithPayment
import com.rayo.digitaldiary.databinding.ActivityCustomerMainBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.KeyboardUtils
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.helper.setupWithNavController
import com.rayo.digitaldiary.helper.toast
import com.rayo.digitaldiary.notification.DialogNotificationDetailsFragment
import com.rayo.digitaldiary.ui.dashboard.CustomerDashboardPresenter
import com.rayo.digitaldiary.ui.payment.PaymentData
import com.rayo.digitaldiary.ui.staff.StaffData
import com.rayo.digitaldiary.ui.sync.SyncProgressDialogFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Mittal Varsani on 05/06/23.
 *
 * @author Mittal Varsani
 */

@AndroidEntryPoint
class CustomerMainActivity : BaseActivity<CustomerViewModel?>(), CustomerMainActivityPresenter {

    override val viewModel: CustomerViewModel by viewModels()

    lateinit var binding: ActivityCustomerMainBinding
    private lateinit var navController: NavController
    var customerBusinessPresenter: CustomerBusinessPresenter? = null
    var customerDashboardPresenter: CustomerDashboardPresenter? = null
    private var syncProgressDialog: SyncProgressDialogFragment? = null
    private var switchBusinessPresenter: SwitchBusinessPresenter? = null

    interface SwitchBusinessPresenter {
        fun switchCustomer()
    }

    fun setSwitchPresenter(presenter: SwitchBusinessPresenter) {
        this.switchBusinessPresenter = presenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setDataBindingView(R.layout.activity_customer_main)
        setSupportActionBar(binding.toolbar.topAppBar)
        setupBottomNavigationBar(intent)

        object : KeyboardUtils(binding.constrainCustomerMain){
            override fun onKeyboardVisibilityChanged(opened: Boolean) {
                super.onKeyboardVisibilityChanged(opened)
                if(opened)
                    binding.bottomNavigation.visibility = View.GONE
                else
                    binding.bottomNavigation.visibility = View.VISIBLE
            }
        }
        binding.toolbar.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        val userId = preferenceManager.getPref(Constants.prefUserId, "")
        binding.toolbar.topAppBar.title = preferenceManager.getPref(Constants.prefBusinessName, "")
        val isFromNotification = intent.getBooleanExtra(Constants.isFromNotification, false)

        if (isFromNotification) {
            when (intent.getStringExtra(Constants.notificationType)) {
                Constants.ORDER -> {
                    val customerIdFromNotification = intent.getStringExtra(Constants.customerId)
                    viewModel.getCustomerId(customerIdFromNotification!!)

                    viewModel.userId.observe(this@CustomerMainActivity) {
                        if (customerIdFromNotification != userId) {
                            with(preferenceManager) {
                                savePref(Constants.prefToken, it.token)
                                savePref(Constants.prefUserId, it.userId)
                                savePref(Constants.prefContactNumber, it.contactNumber)
                                savePref(Constants.prefAddress, it.address)
                                savePref(Constants.businessName, it.businessName)
                                savePref(Constants.prefStaffLastSyncTime, it.staffLastSyncTime)
                                savePref(Constants.prefOrderHistoryLastSyncTime, it.orderHistoryLastSyncTime)
                                savePref(Constants.prefPaymentHistoryLastSyncTime, it.paymentHistoryLastSyncTime)
                                savePref(Constants.prefCurrencySymbol, it.currencySymbol)
                                savePref(Constants.prefCountryCode, it.countryCode)
                                savePref(Constants.prefCurrencyCountry, it.currencyCountryName)
                            }
                        }
                    }
                    Handler(Looper.getMainLooper()).postDelayed({
                        Bundle().let {
                            findNavController(R.id.fragmentContainer).navigate(R.id.ownerOrStaffOrderHistoryDetailFragment, intent.extras)
                        }
                    }, 500)
                }
                Constants.PAYMENT -> {
                    val paymentData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(Constants.paymentData, PaymentData::class.java)
                    } else {
                        intent.getParcelableExtra(Constants.paymentData)
                    }
                    val staffData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(Constants.createdByStaffData, StaffData::class.java)
                    } else {
                        intent.getParcelableExtra(Constants.createdByStaffData)
                    }

                    val customerData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(Constants.customerData, CustomerData::class.java)
                    } else {
                        intent.getParcelableExtra(Constants.customerData)
                    }

                    val staffWithPayment = paymentData?.let { StaffWithPayment(it, customerData, staffData) }

                    val customerIdFromNotification = intent.getStringExtra(Constants.customerId)

                    viewModel.getCustomerId(customerIdFromNotification!!)

                    viewModel.userId.observe(this@CustomerMainActivity) {
                        if (customerIdFromNotification != userId) {
                            saveCurrentSelectedCustomerData(it)
                            switchBusinessPresenter?.switchCustomer()
                            binding.toolbar.topAppBar.title = it.businessName
                        }
                    }
                    paymentData.let {
                        staffWithPayment?.let { it1 ->
                            DialogNotificationDetailsFragment.show(
                                this.supportFragmentManager,
                                it1, this
                            )
                        }
                    }

                }
            }
        }

        if (networkInterceptor.isInternetAvailable() && DigitalDiaryApplication.instance.isShowSyncProgress) {
            syncProgressDialog = SyncProgressDialogFragment.show(supportFragmentManager)
        }

        viewModel.syncCollectionCount.observe(this) {
            Log.d(
                "ProgressData",
                "collectionName ${it.collectionName}" +
                        " syncCount ${it.syncCount} " +
                        " totalSyncCollection ${it.totalSyncCollection} " +
                        " progress ${(it.syncCount * 100) / it.totalSyncCollection}"
            )
            it?.let {
                syncProgressDialog?.onProgressChanged(it)
            }
        }

        viewModel.syncSuccess.observe(this) {
            Log.d("ProgressData", "sync success ${viewModel.syncCollectionCount.value?.syncCount}")
            Handler(Looper.getMainLooper()).postDelayed({
                syncProgressDialog?.dismiss()
                toast(it)
            }, 300)
            if (viewModel.isSyncAPICallWhileLogout) {
                viewModel.callLogoutAPI(apiRepository, preferenceManager)
            } else {
                dismissProgressDialog()
            }
        }

        changeMenuItemText()
    }

    private fun setupBottomNavigationBar(notificationIntent: Intent?) {
        val navGraphIds =
            listOf(
                R.navigation.nav_customer_dashboard,
                R.navigation.nav_customer_business,
                R.navigation.nav_settings
            )

        val controller = binding.bottomNavigation.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.fragmentContainer,
            intent = intent
        )

        controller.observe(this) { navController ->
            this.navController = navController

            navController.addOnDestinationChangedListener { _, destination, _ ->
                Log.d("navigate", "navigated to -----> " + destination.label)
                updateTopBar(destination.id)
            }
        }
    }

    private fun updateTopBar(fragmentId: Int) {
        when (fragmentId) {
            R.id.customerDashboardFragment -> {
                setupToolbar(preferenceManager.getPref(Constants.businessName, ""))
            }
            R.id.customerBusinessFragment -> {
                setupToolbar(getString(R.string.business))
            }
            R.id.settingsFragment -> {
                setupToolbar(getString(R.string.settings))
            }
            R.id.customerProfileFragment -> {
                setupToolbar(getString(R.string.customer_profile), ContextCompat.getDrawable(this, R.drawable.ic_back))
            }
            R.id.customerHistoryFragment -> {
                setupToolbar(getString(R.string.order_history), ContextCompat.getDrawable(this, R.drawable.ic_back))
            }
            R.id.ownerOrStaffOrderHistoryDetailFragment -> {
                setupToolbar(getString(R.string.order_history), ContextCompat.getDrawable(this, R.drawable.ic_back))
            }
            R.id.ownerProfileFragment -> {
                setupToolbar(getString(R.string.owner_details), ContextCompat.getDrawable(this, R.drawable.ic_back))
            }
            R.id.notificationFragment -> {
                setupToolbar(getString(R.string.notification), ContextCompat.getDrawable(this, R.drawable.ic_back))
            }
            R.id.paymentHistoryFragment -> {
                setupToolbar(getString(R.string.payment_history), ContextCompat.getDrawable(this, R.drawable.ic_back))
            }
            R.id.customerLoginDetailFragment -> {
                setupToolbar(getString(R.string.sessions), ContextCompat.getDrawable(this, R.drawable.ic_back))
            }
        }
    }

    private fun setupToolbar(title: String, navigationIcon: Drawable? = null) {
        binding.toolbar.topAppBar.title = Utils.getTranslatedValue(title)
        binding.toolbar.topAppBar.navigationIcon = navigationIcon
    }


    private fun changeMenuItemText() {
        val menuDashboard: MenuItem = binding.bottomNavigation.menu.findItem(R.id.customerDashboard)
        val menuBusiness: MenuItem = binding.bottomNavigation.menu.findItem(R.id.business)
        val menuSetting: MenuItem = binding.bottomNavigation.menu.findItem(R.id.menuSettings)
        menuDashboard.title = Utils.getTranslatedValue(getString(R.string.dashboard))
        menuBusiness.title = Utils.getTranslatedValue(getString(R.string.business))
        menuSetting.title = Utils.getTranslatedValue(getString(R.string.settings))
    }

    override fun onBackPressed() {
        if (!navController.popBackStack()) {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun moveToDashboard() {
        val bottomNavigationMenuView =
            binding.bottomNavigation.getChildAt(0) as BottomNavigationMenuView
        val view = bottomNavigationMenuView.getChildAt(Constants.INDEX_DASHBOARD)
        val itemView = view as BottomNavigationItemView
        itemView.performClick()
        syncAllDataForNewCustomer()
    }

    fun syncAllDataForNewCustomer() {
        DigitalDiaryApplication.instance.isShowSyncProgress = true
        if (networkInterceptor.isInternetAvailable() && DigitalDiaryApplication.instance.isShowSyncProgress) {
            syncProgressDialog = SyncProgressDialogFragment.show(supportFragmentManager)
            viewModel.syncInit(Constants.STAFF, DigitalDiaryApplication.instance.currentCustomerId)
        }
    }

    override fun onOkyClick() {
        DialogNotificationDetailsFragment.dismiss()
    }
}