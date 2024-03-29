package com.rayo.digitaldiary.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.baseClasses.CallMessageBottomSheetDialog
import com.rayo.digitaldiary.database.CustomerLoginDao
import com.rayo.digitaldiary.databinding.FragmentOwnerDetailsBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.ui.login.ScanQRData
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OwnerDetailsFragment : BaseFragment<FragmentOwnerDetailsBinding, BaseViewModel?>(),
    OwnerProfilePresenter {

    override val viewModel: BaseViewModel? = null

    @Inject
    lateinit var customerLoginDao: CustomerLoginDao

    override fun getFragmentId(): Int {
        return R.layout.fragment_owner_details
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ownerProfilePresenter = this
        binding.countryCode = preferenceManager.getPref(Constants.prefCountryCode, "")
        if (preferenceManager.getPref(Constants.prefUserType, "") == Constants.USER_TYPE_CUSTOMER) {
            Coroutines.ioThenMain({
                customerLoginDao.getCurrentCustomerData(
                    preferenceManager.getPref(
                        Constants.prefUserId,
                        ""
                    )
                )
            }, {
                binding.ownerProfileData = it ?: ScanQRData()
            })
        } else {
            setOwnerProfileData()
        }
    }

    private fun setOwnerProfileData() {
        with(binding) {
            val ownerDetailData = ScanQRData()
            ownerDetailData.businessName =
                preferenceManager.getPref(Constants.prefBusinessName, "--")
            ownerDetailData.ownerEmail = preferenceManager.getPref(Constants.prefOwnerEmail, "--")
            ownerDetailData.ownerContactNumber =
                preferenceManager.getPref(Constants.prefOwnerContactNumber, "--")
            binding.ownerProfileData = ownerDetailData
            tvPhoneNumber.isEnabled =
                preferenceManager.getPref(Constants.prefOwnerContactNumber, "").isNotEmpty()
        }
    }

    override fun onPhoneNumberClick(phoneNumber: String?) {
        Log.e("TAG", "onPhoneNumberClick: ---------------------$phoneNumber")
        phoneNumber?.let { CallMessageBottomSheetDialog.display(childFragmentManager, it) }
    }


    override fun onProfileUpdated() {
        setOwnerProfileData()
    }
}