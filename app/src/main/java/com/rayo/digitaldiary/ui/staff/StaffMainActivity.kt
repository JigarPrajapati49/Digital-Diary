package com.rayo.digitaldiary.ui.staff

import android.content.Intent
import android.graphics.drawable.Drawable
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
import com.rayo.digitaldiary.databinding.ActivityStaffMainBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.KeyboardUtils
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.helper.setupWithNavController
import com.rayo.digitaldiary.helper.toast
import com.rayo.digitaldiary.ui.dashboard.StaffDashboardPresenter
import com.rayo.digitaldiary.ui.sync.SyncProgressDialogFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Mittal Varsani on 05/06/23.
 *
 * @author Mittal Varsani
 */

@AndroidEntryPoint
class StaffMainActivity : BaseActivity<StaffMainViewModel>() {

    override val viewModel: StaffMainViewModel by viewModels()
    lateinit var binding: ActivityStaffMainBinding
    private lateinit var navController: NavController
    var setDashBoardListener: StaffDashboardPresenter? = null
    private var syncProgressDialog: SyncProgressDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setDataBindingView(R.layout.activity_staff_main)
        setSupportActionBar(binding.toolbar.topAppBar)
        setupBottomNavigationBar(intent)

        object : KeyboardUtils(binding.constraintStaffMain){
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
                getCustomerCount()
            }
        }

        viewModel.customerCountSuccess.observe(this) {
            setDashBoardListener?.setCustomerCount(viewModel.totalCustomer)
        }

        viewModel.productCountSuccess.observe(this) {
            setDashBoardListener?.setProductCount(viewModel.totalProduct)
        }

        viewModel.errorMessage.observe(this) {
            dismissProgressDialog()
            syncProgressDialog?.dismiss()
        }

        changeMenuItemText()
    }

    private fun changeMenuItemText() {
        val menuDashboard: MenuItem = binding.bottomNavigation.menu.findItem(R.id.menuDashboard)
        val menuSetting: MenuItem = binding.bottomNavigation.menu.findItem(R.id.menuSettings)
        val menuCustomer :MenuItem = binding.bottomNavigation.menu.findItem(R.id.menuCustomers)
        menuDashboard.title = Utils.getTranslatedValue(getString(R.string.dashboard))
        menuSetting.title = Utils.getTranslatedValue(getString(R.string.settings))
        menuCustomer.title = Utils.getTranslatedValue(getString(R.string.customers))
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

    private fun setupBottomNavigationBar(notificationIntent: Intent?) {
        val navGraphIds =
            listOf(
                R.navigation.nav_staff_dashboard,
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
        /*onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.e("backButton", "handleOnBackPressed: ----Back Button Press ")
                if (!navController.popBackStack()) {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })*/
    }

    private fun updateTopBar(fragmentId: Int) {
        when (fragmentId) {
            R.id.dashboardFragment -> {
                setupToolbar(getString(R.string.dashboard))
            }
            R.id.profileFragment -> {
                setupToolbar(getString(R.string.staff))
            }
            R.id.settingsFragment -> {
                setupToolbar(getString(R.string.settings))
            }
            R.id.staffListingFragment -> {
                setupToolbar(getString(R.string.staff), ContextCompat.getDrawable(this, R.drawable.ic_back))
            }
            R.id.staffDetailsFragment -> {
                setupToolbar(getString(R.string.staff_profile), ContextCompat.getDrawable(this, R.drawable.ic_back))
            }
            R.id.customerListingFragment -> {
                setupToolbar(getString(R.string.customers))
            }
            R.id.customerDetailsFragment -> {
                setupToolbar((getString(R.string.customer_profile)), ContextCompat.getDrawable(this, R.drawable.ic_back))
            }
            R.id.productFragment -> {
                setupToolbar(getString(R.string.product))
            }
            R.id.ownerOrStaffOrderHistoryFragment -> {
                setupToolbar(getString(R.string.order_history), ContextCompat.getDrawable(this, R.drawable.ic_back))
            }
            R.id.ownerOrStaffOrderHistoryDetailFragment -> {
                setupToolbar(getString(R.string.order_history), ContextCompat.getDrawable(this, R.drawable.ic_back))
            }
            R.id.productListFragment -> {
                setupToolbar(getString(R.string.product), ContextCompat.getDrawable(this, R.drawable.ic_back))
            }
            R.id.ownerProfileFragment -> {
                setupToolbar(getString(R.string.owner_details), ContextCompat.getDrawable(this, R.drawable.ic_back))
            }
            R.id.staffProfileFragment -> {
                setupToolbar(getString(R.string.staff_profile),ContextCompat.getDrawable(this, R.drawable.ic_back))
            }
            R.id.addPaymentFragment -> {
                setupToolbar(getString(R.string.payments), ContextCompat.getDrawable(this, R.drawable.ic_back))
            }
            R.id.paymentHistoryFragment -> {
                setupToolbar(getString(R.string.payment_history), ContextCompat.getDrawable(this, R.drawable.ic_back))
            }
            R.id.customerHistoryFragment -> {
                setupToolbar(getString(R.string.order_history), ContextCompat.getDrawable(this, R.drawable.ic_back))
            }

            R.id.sessionFragment -> {
                setupToolbar(getString(R.string.sessions), ContextCompat.getDrawable(this, R.drawable.ic_back))
            }

            R.id.troubleShootingFragment -> {
                setupToolbar(getString(R.string.troubleshoot), ContextCompat.getDrawable(this, R.drawable.ic_back))
            }
        }
    }

    fun setupToolbar(title: String, navigationIcon: Drawable? = null) {
        binding.toolbar.topAppBar.title = Utils.getTranslatedValue(title)
        binding.toolbar.topAppBar.navigationIcon = navigationIcon
    }

    fun getCustomerCount() {
        Log.d("TAG", "getCustomerAndStaffCount: ")
        viewModel.getCustomerCount()
    }

    fun moveToProduct() {
        val bottomNavigationMenuView = binding.bottomNavigation.getChildAt(0) as BottomNavigationMenuView
        val view = bottomNavigationMenuView.getChildAt(Constants.INDEX_PRODUCT)
        val itemView = view as BottomNavigationItemView
        itemView.performClick()
    }
    fun moveToCustomer() {
        val bottomNavigationMenuView = binding.bottomNavigation.getChildAt(0) as BottomNavigationMenuView
        val view = bottomNavigationMenuView.getChildAt(1)
        val itemView = view as BottomNavigationItemView
        itemView.performClick()
    }

    fun syncAllData() {
        syncProgressDialog = SyncProgressDialogFragment.show(supportFragmentManager)
        viewModel.syncInit(Constants.PRODUCT)
    }

    override fun onBackPressed() {
        if (!navController.popBackStack()) {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}