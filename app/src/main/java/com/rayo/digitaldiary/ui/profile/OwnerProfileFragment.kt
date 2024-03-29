package com.rayo.digitaldiary.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.databinding.FragmentProfileBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Mittal Varsani on 01/03/23.
 *
 * @author Mittal Varsani
 */
@AndroidEntryPoint
class OwnerProfileFragment : BaseFragment<FragmentProfileBinding, SessionVewModel>(),
    ProfilePresenter {

    override val viewModel: SessionVewModel by viewModels()
    override fun getFragmentId(): Int {
        return R.layout.fragment_profile
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profilePresenter = this
        setProfileData()
    }

    override fun onEditClick() {
        if (Utils.isSingleClick()){
            if (networkInterceptor.isInternetAvailable()) {
                OwnerProfileUpdateDialogFragment.show(
                    parentFragmentManager, this
                )
            } else {
                context?.let {
                    dialogHelper.showOneButtonDialog(
                        it, Utils.getTranslatedValue(it.getString(R.string.no_internet))
                    )
                }
            }
        }
    }

    override fun onProfileUpdated() {
        setProfileData()
    }

    override fun onLoginDevice() {
        findNavController().navigate(R.id.sessionFragment)
    }

    private fun setProfileData() {
        with(preferenceManager) {
            binding.email = getPref(Constants.prefEmail, "")
            binding.phoneNumber = if (getPref(Constants.prefContactNumber, "").isEmpty()) {
                "--"
            } else {
                getPref(Constants.prefCountryCode, "") + " " +
                        getPref(Constants.prefContactNumber, "")
            }
            binding.businessName = getPref(Constants.prefBusinessName, "")
            binding.currency = getPref(
                Constants.prefCurrencyCountry, ""
            ) + "(" + getPref(Constants.prefCurrencySymbol, "") + ")"
        }
    }

    override fun onDeleteAccountClick() {
        findNavController().navigate(R.id.deleteAccountFragment)
    }
}
