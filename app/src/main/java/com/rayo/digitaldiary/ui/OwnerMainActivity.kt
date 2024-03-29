package com.rayo.digitaldiary.ui

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
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
import com.rayo.digitaldiary.databinding.ActivityMainBinding
import com.rayo.digitaldiary.di.PreferenceEntryPoint
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.KeyboardUtils
import com.rayo.digitaldiary.helper.PreferenceManager
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.helper.setAppLocale
import com.rayo.digitaldiary.helper.setupWithNavController
import com.rayo.digitaldiary.helper.toast
import com.rayo.digitaldiary.notification.DialogNotificationDetailsFragment
import com.rayo.digitaldiary.ui.customer.CustomerData
import com.rayo.digitaldiary.ui.customer.CustomerMainActivityPresenter
import com.rayo.digitaldiary.ui.dashboard.DashboardPresenter
import com.rayo.digitaldiary.ui.payment.PaymentData
import com.rayo.digitaldiary.ui.product.Product
import com.rayo.digitaldiary.ui.staff.StaffData
import com.rayo.digitaldiary.ui.sync.SyncProgressDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject


@AndroidEntryPoint
class OwnerMainActivity : BaseActivity<OwnerMainViewModel>(), CustomerMainActivityPresenter {

    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    var setDashBoardListener: DashboardPresenter? = null
    private var syncProgressDialog: SyncProgressDialogFragment? = null
    val productList: MutableList<Product> = ArrayList()

    @Inject
    lateinit var preferenceManagers: PreferenceManager

    override val viewModel: OwnerMainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setDataBindingView(R.layout.activity_main)

        object : KeyboardUtils(binding.constrainOwnerMain){
            override fun onKeyboardVisibilityChanged(opened: Boolean) {
                super.onKeyboardVisibilityChanged(opened)
                if (opened)
                    binding.bottomNavigation.visibility = View.GONE
                else
                    binding.bottomNavigation.visibility = View.VISIBLE
            }
        }
        setupBottomNavigationBar(intent)

        val isFromNotification = intent.getBooleanExtra(Constants.isFromNotification, false)

        if (isFromNotification) {
            when (intent.getStringExtra(Constants.notificationType)) {
                Constants.ORDER -> {
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController(R.id.fragmentContainer).navigate(R.id.ownerOrStaffOrderHistoryDetailFragment, intent.extras)
                    }, 500)
                }
                Constants.PAYMENT -> {
                    val customerData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(Constants.customerData, CustomerData::class.java)
                    } else {
                        intent.getParcelableExtra(Constants.customerData)
                    }

                    val staffData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(Constants.createdByStaffData, StaffData::class.java)
                    } else {
                        intent.getParcelableExtra(Constants.createdByStaffData)
                    }

                    val paymentData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(Constants.paymentData, PaymentData::class.java)
                    } else {
                        intent.getParcelableExtra(Constants.paymentData)
                    }

                    val staffWithPayment =
                        paymentData?.let { StaffWithPayment(it, customerData, staffData) }

                    paymentData.let {
                        DialogNotificationDetailsFragment.show(this.supportFragmentManager, staffWithPayment!!, this)
                    }
                }
            }
        }
        val languageCode = preferenceManagers.getPref(Constants.prefLanguageCode, "EN")

        // for hide bottom navigation when open keyboard open
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        Log.e("TAG", "onCreate: -----------------------$languageCode")

        setSupportActionBar(binding.toolbar.topAppBar)
        binding.toolbar.topAppBar.setNavigationOnClickListener {
                onBackPressed()
        }

        changeMenuItemText()
        if (networkInterceptor.isInternetAvailable() && DigitalDiaryApplication.instance.isShowSyncProgress) {
            syncProgressDialog = SyncProgressDialogFragment.show(supportFragmentManager)
        }

        viewModel.errorMessage.observe(this) {
            syncProgressDialog?.dismiss()
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
                getCustomerAndStaffCount()
            }
        }

        viewModel.staffAndCustomerCountSuccess.observe(this) {
            setDashBoardListener?.setStaffAndCustomerCount(viewModel.totalStaff, viewModel.totalCustomer, viewModel.totalProduct)
        }
    }

    private fun changeMenuItemText() {
        val menuDashboard: MenuItem = binding.bottomNavigation.menu.findItem(R.id.menuDashboard)
        val menuProduct: MenuItem = binding.bottomNavigation.menu.findItem(R.id.menuProduct)
        val menuSetting: MenuItem = binding.bottomNavigation.menu.findItem(R.id.menuSettings)
        val menuCustomers: MenuItem = binding.bottomNavigation.menu.findItem(R.id.menuCustomers)
        menuDashboard.title = Utils.getTranslatedValue(getString(R.string.dashboard))
        menuProduct.title = Utils.getTranslatedValue(getString(R.string.products))
        menuSetting.title = Utils.getTranslatedValue(getString(R.string.settings))
        menuCustomers.title = Utils.getTranslatedValue(getString(R.string.customers))
    }

    override fun attachBaseContext(newBase: Context?) {
        val pref = newBase?.let {
            EntryPointAccessors.fromApplication(it, PreferenceEntryPoint::class.java).preference
        }
        val code = pref?.getPref(Constants.prefLanguageCode, "EN")
        Log.e("TAG", "attachBaseContext: --------------------$code")
        ContextWrapper(pref?.getPref(Constants.prefLanguageCode, "EN")?.let { newBase.setAppLocale(it) })
        super.attachBaseContext(newBase)
    }

    fun getCustomerAndStaffCount() {
        Log.d("TAG", "getCustomerAndStaffCount: ")
        viewModel.getCustomerAndStaffCount()
    }

    fun callSyncOrderAPI(isSyncAPICallForLogout: Boolean = false) {
        showProgressDialog()
        viewModel.isSyncAPICallWhileLogout = isSyncAPICallForLogout
        viewModel.syncOrder()
    }

    fun callSyncPaymentAPI(isSyncAPICallForLogout: Boolean = false) {
        showProgressDialog()
        viewModel.isSyncAPICallWhileLogout = isSyncAPICallForLogout
        viewModel.syncPayment()
    }

    fun syncAllData() {
        syncProgressDialog = SyncProgressDialogFragment.show(supportFragmentManager)
        viewModel.syncInit(Constants.PRODUCT)
    }

    private fun setupBottomNavigationBar(notificationIntent: Intent?) {
        val navGraphIds =
            listOf(
                R.navigation.nav_dashboard,
                R.navigation.nav_product,
                R.navigation.nav_profile,
                R.navigation.nav_settings,
                R.navigation.nav_customer
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
            R.id.dashboardFragment -> {
                setupToolbar(getString(R.string.dashboard), false)
            }

            R.id.staffListingFragment -> {
                setupToolbar(getString(R.string.staff))
            }

            R.id.staffDetailsFragment -> {
                setupToolbar(getString(R.string.staff_profile))
            }

            R.id.customerListingFragment -> {
                setupToolbar(getString(R.string.customers),false)
            }

            R.id.customerDetailsFragment -> {
                setupToolbar(getString(R.string.customer_profile))
            }

            R.id.profileFragment -> {
                setupToolbar(getString(R.string.profile), true)
            }

            R.id.settingsFragment -> {
                setupToolbar(getString(R.string.settings), false)
            }

            R.id.productFragment -> {
                setupToolbar(getString(R.string.products), false)
            }

            R.id.ownerOrStaffOrderHistoryFragment,
            R.id.ownerOrStaffOrderHistoryDetailFragment,
            R.id.customerHistoryFragment -> {
                setupToolbar(getString(R.string.order_history))
            }

            R.id.notificationFragment -> {
                setupToolbar(getString(R.string.notification))
            }

            R.id.resetPasswordFragment -> {
                setupToolbar(getString(R.string.change_password))
            }

            R.id.sessionFragment -> {
                setupToolbar(getString(R.string.sessions))
            }

            R.id.addPaymentFragment -> {
                setupToolbar(getString(R.string.payments))
            }

            R.id.paymentHistoryFragment -> {
                setupToolbar(getString(R.string.payment_history))
            }
            R.id.deleteAccountFragment -> {
                setupToolbar(getString(R.string.delete_account))
            }
            R.id.generateQrCodeFragment ->{
                setupToolbar(getString(R.string.re_generate_qr_code),true)
            }
            R.id.troubleShootingFragment ->{
                setupToolbar(getString(R.string.troubleshoot),true)
            }
        }
    }

    fun setupToolbar(title: String, isShowBackArrow: Boolean = true) {
        binding.toolbar.topAppBar.title = Utils.getTranslatedValue(title)
        binding.toolbar.topAppBar.navigationIcon = if (isShowBackArrow) {
            ContextCompat.getDrawable(this, R.drawable.ic_back)
        } else {
            null
        }
    }

    fun moveToProduct() {
        val bottomNavigationMenuView = binding.bottomNavigation.getChildAt(0) as BottomNavigationMenuView
        val view = bottomNavigationMenuView.getChildAt(Constants.INDEX_PRODUCT)
        val itemView = view as BottomNavigationItemView
        itemView.performClick()
    }
    fun moveToCustomer() {
        val bottomNavigationMenuView = binding.bottomNavigation.getChildAt(0) as BottomNavigationMenuView
        val view = bottomNavigationMenuView.getChildAt(Constants.INDEX_CUSTOMER)
        val itemView = view as BottomNavigationItemView
        itemView.performClick()
    }

    override fun onBackPressed() {
        if (!navController.popBackStack()) {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onOkyClick() {
        DialogNotificationDetailsFragment.dismiss()
    }
}