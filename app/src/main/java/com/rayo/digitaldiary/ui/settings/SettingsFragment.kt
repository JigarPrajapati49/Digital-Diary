package com.rayo.digitaldiary.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.rayo.digitaldiary.BuildConfig
import com.rayo.digitaldiary.DigitalDiaryApplication
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.databinding.FragmentSettingsBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.LanguageData
import com.rayo.digitaldiary.ui.OwnerMainActivity
import com.rayo.digitaldiary.ui.staff.StaffMainActivity
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Mittal Varsani on 01/03/23.
 *
 * @author Mittal Varsani
 */
@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding, SettingsViewModel>(), SettingsPresenter {

    private val languageList: ArrayList<LanguageData> = ArrayList()

    override val viewModel: SettingsViewModel by viewModels()
    val TAG = "SettingFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.versionName = BuildConfig.VERSION_NAME
        with(binding) {
            presenter = this@SettingsFragment
            selectedLanguage = preferenceManager.getPref(Constants.prefLanguage, Constants.languageDefault)
            isCustomerLogin = preferenceManager.getPref(Constants.prefUserType, "") == Constants.USER_TYPE_CUSTOMER
            isNotificationVisible = preferenceManager.getPref(Constants.prefUserType, "") != Constants.USER_TYPE_STAFF
            isResetPasswordVisible = preferenceManager.getPref(Constants.prefLoginType, "") == Constants.EMAIL
        }
        viewModel.getLanguagesListFromDB()

        viewModel.languagesList.observe(viewLifecycleOwner) {
            Log.e(TAG, "onViewCreated: ------ LanguageList From Database--------${it.size}")
            languageList.clear()
            languageList.addAll(it)
        }
        if (preferenceManager.getPref(Constants.prefUserType, "") != Constants.USER_TYPE_CUSTOMER) {
            binding.isTroubleshootingVisible = true
        } else {
            binding.isTroubleshootingVisible = false
        }

    }

    override fun getFragmentId(): Int {
        return R.layout.fragment_settings
    }

    override fun logOutClick() {
        lateinit var mGoogleSignInClient: GoogleSignInClient
        if (!networkInterceptor.isInternetAvailable()) {
            context?.let {
                dialogHelper.showOneButtonDialog(it, Utils.getTranslatedValue(it.getString(R.string.no_internet)))
            }
            return
        }
        context?.let {
            dialogHelper.showTwoButtonDialog(
                it,
                Utils.getTranslatedValue(getString(R.string.logout_confirmation))
            ) { dialog, _ ->
                dialog.dismiss()
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
                mGoogleSignInClient.signOut().addOnCompleteListener {
                    DigitalDiaryApplication.instance.isLogoutFromSettings = true
                    if (preferenceManager.getPref(Constants.prefUserType, "") == Constants.USER_TYPE_CUSTOMER) { // logout customer
                        callLogoutAPI(true)
                    } else { // logout business owner or staff member after syncing order & payment
                        when (activity) {
                            is OwnerMainActivity -> {
                                (activity as OwnerMainActivity).callSyncOrderAPI(true)
                            }
                            is StaffMainActivity -> {
                                (activity as StaffMainActivity).callSyncOrderAPI(true)
                            }
                        }
                    }
                }

            }
        }
    }

    override fun languageChangeClick() {
        if (Utils.isSingleClick()) {
            SelectLanguageDialogFragment.show(parentFragmentManager, languageList, this)
        }
    }

    override fun onLanguageSelected(title: String) {
        preferenceManager.savePref(Constants.prefLanguage, title)
        binding.selectedLanguage = title
    }

    override fun onResetPasswordClick() {
        if (networkInterceptor.isInternetAvailable()) {
            findNavController().navigate(R.id.resetPasswordFragment)
        } else {
            context?.let {
                dialogHelper.showOneButtonDialog(it, Utils.getTranslatedValue(it.getString(R.string.no_internet)))
            }
        }
    }

    override fun onNotificationClick() {
        if (networkInterceptor.isInternetAvailable()) {
            findNavController().navigate(R.id.notificationFragment)
        } else {
            context?.let {
                dialogHelper.showOneButtonDialog(it, Utils.getTranslatedValue(it.getString(R.string.no_internet)))
            }
        }
    }

    override fun onProfileClick() {
        super.onProfileClick()
        if (preferenceManager.getPref(Constants.prefUserType, "") == Constants.USER_TYPE_STAFF) {
            findNavController().navigate(R.id.staffProfileFragment)
        } else if (preferenceManager.getPref(Constants.prefUserType, "") == Constants.USER_TYPE_CUSTOMER) {
            findNavController().navigate(R.id.customerProfileFragment)
        } else {
            findNavController().navigate(R.id.profileFragment)
        }
    }

    override fun onTroubleshootingClick() {
        findNavController().navigate(R.id.troubleShootingFragment)
    }
}