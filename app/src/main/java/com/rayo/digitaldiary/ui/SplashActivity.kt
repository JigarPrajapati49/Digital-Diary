package com.rayo.digitaldiary.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseActivity
import com.rayo.digitaldiary.baseClasses.MainViewModel
import com.rayo.digitaldiary.database.LanguageTranslationDao
import com.rayo.digitaldiary.databinding.ActivitySplashBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.helper.setAppLocale
import com.rayo.digitaldiary.ui.customer.CustomerMainActivity
import com.rayo.digitaldiary.ui.login.AuthenticationActivity
import com.rayo.digitaldiary.ui.settings.SelectLanguageDialogFragment
import com.rayo.digitaldiary.ui.settings.SettingsPresenter
import com.rayo.digitaldiary.ui.staff.StaffMainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * Created by Mittal Varsani on 24/02/23.
 *
 * @author Mittal Varsani
 */

@AndroidEntryPoint
class SplashActivity : BaseActivity<MainViewModel>(), SettingsPresenter {
    lateinit var binding: ActivitySplashBinding

    @Inject
    lateinit var languageTranslationDao: LanguageTranslationDao
    private val languageList: ArrayList<LanguageData> = ArrayList()
    private var translationLastSyncTime = ""
    override val viewModel: MainViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        Log.e("TAG", "-------Permission Result---------$isGranted: ")
        initNextData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setDataBindingView(R.layout.activity_splash)

        translationLastSyncTime = preferenceManager.getPref(Constants.prefLanguageTranslationLastSyncTime, "")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            initNextData()
        }

        viewModel.languagesList.observe(this) {
            languageList.clear()
            languageList.addAll(it)
            if (languageList.isNotEmpty()) {
                showLanguageListDialog()
            } else {
                startMainActivity()
            }
        }

        viewModel.translationData.observe(this) {
            binding.isProgressBarVisible = false
            try {
                startMainActivity()
            } catch (e: Exception) {
                Log.e("TAG", "translationData----- ${e.message}")
            }
        }

        viewModel.errorMessage.observe(this) {
            binding.isProgressBarVisible = false
            this.let { it1 ->
                dialogHelper.showInternetConnection(it1, Utils.getTranslatedValue("No Internet Connection")) { dialogInterface, i ->
                    if (networkInterceptor.isInternetAvailable()) {
                        binding.isProgressBarVisible = true
                        viewModel.syncInitTranslation(Constants.LANGUAGE_TRANSLATION)
                    } else {
                        noInternetConnectionDialog()
                    }
                }
            }
        }

        viewModel.translationSyncProgress.observe(this) {
            binding.syncProgress = it
        }

        if(preferenceManager.getPref(Constants.prefCountryCode, "").isEmpty()) {
            binding.ccp.defaultCountryCode?.let {
                preferenceManager.savePref(Constants.prefCountryCode, "+$it")
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        ContextWrapper(newBase?.setAppLocale(Constants.languageCodeDefault))
        super.attachBaseContext(newBase)
    }

    private fun startMainActivity() {
        Coroutines.ioThenMain({
            viewModel.getLanguageTranslation()
        }, {
            it?.let { it1 -> Utils.saveLanguageTranslation(it1) }
            if (preferenceManager.getPref<String>(Constants.prefToken).isNullOrEmpty()
                || preferenceManager.getPref<Int>(Constants.prefIsNewUser) == 1
            ) {
                startActivity(Intent(this@SplashActivity, AuthenticationActivity::class.java))
            } else {
                when (preferenceManager.getPref(Constants.prefUserType, "")) {
                    Constants.USER_TYPE_CUSTOMER -> {
                        startActivity(Intent(this@SplashActivity, CustomerMainActivity::class.java))
                    }

                    Constants.USER_TYPE_STAFF -> {
                        startActivity(Intent(this@SplashActivity, StaffMainActivity::class.java))
                    }

                    else -> { // Business owner login
                        startActivity(Intent(this@SplashActivity, OwnerMainActivity::class.java))
                    }
                }
            }
            finish()
        })
    }

    private fun showLanguageListDialog() {
        if (preferenceManager.getPref(Constants.prefLanguageCode, "").isEmpty()) {
            // When user comes very first time language dialog will be shown
            binding.syncProgress = 0
            viewModel.isFreshUser = true
            SelectLanguageDialogFragment.show(supportFragmentManager, languageList,this)
        } else {
            if (translationLastSyncTime.isEmpty()) {
                binding.syncProgress = 0
                viewModel.isFreshUser = true
                viewModel.syncInitTranslation(Constants.LANGUAGE_TRANSLATION)
                binding.isProgressBarVisible = true
            } else {
                binding.isProgressBarVisible = false
                startMainActivity()
            }
        }
    }

    override fun onLanguageSelected(title: String) {
        binding.isProgressBarVisible = true
        if (preferenceManager.getPref(Constants.prefToken, "").isEmpty()) { // When user comes very first time language dialog will be shown
            viewModel.syncInitTranslation(Constants.LANGUAGE_TRANSLATION)
        }
    }

    private fun noInternetConnectionDialog() {
        if (preferenceManager.getPref(Constants.prefLanguageCode, "").isEmpty()) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(Utils.getTranslatedValue(this.getString(R.string.no_internet_dialog)))
            builder.setPositiveButton(this.getText(R.string.retry)) { dialog, which ->
                if (networkInterceptor.isInternetAvailable()) {
                    viewModel.getAppSettings()
                } else {
                    noInternetConnectionDialog()
                }
            }
            builder.create().show()
        } else {
            if (translationLastSyncTime.isEmpty()) {
                binding.syncProgress = 0
                viewModel.isFreshUser = true
                viewModel.syncInitTranslation(Constants.LANGUAGE_TRANSLATION)
                binding.isProgressBarVisible = true
            } else {
                startMainActivity()
            }
        }
    }

    private fun initNextData() {
        if (!networkInterceptor.isInternetAvailable()) {
            noInternetConnectionDialog()
        } else if (preferenceManager.getPref(Constants.prefToken, "").isNotEmpty()) {
            startMainActivity()
        }
    }
}