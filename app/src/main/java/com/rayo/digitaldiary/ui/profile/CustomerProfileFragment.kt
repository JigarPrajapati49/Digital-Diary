package com.rayo.digitaldiary.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.databinding.FragmentCustomerProfileBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.customer.CustomerData
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CustomerProfileFragment : BaseFragment<FragmentCustomerProfileBinding, ProfileViewModel>(),
    CustomerProfilePresenter {

    override val viewModel: ProfileViewModel by viewModels()

    override fun getFragmentId(): Int {
        return R.layout.fragment_customer_profile
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profilePresenter = this
        binding.countryCode = preferenceManager.getPref(Constants.prefCountryCode, "")
        val customerID = this.arguments?.getString(Constants.customerId, null) ?: kotlin.run {
            preferenceManager.getPref(Constants.prefUserId, "")
        }
        viewModel.getCurrentCustomerData(customerID)
        setProfileData()
    }

    override fun editClick(customerData: CustomerData) {
        if (Utils.isSingleClick()){
            if (networkInterceptor.isInternetAvailable()) {
                UpdateCustomerProfileDialogFragment.show(
                    parentFragmentManager, this, customerData
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

    override fun onLoginDevicesClick(userId: String) {
        Bundle().let {
            it.putString(Constants.userId, userId)
            findNavController().navigate(R.id.customerLoginDetailFragment, it)
        }
    }

    private fun setProfileData() {
        viewModel.currentCustomerData.observe(viewLifecycleOwner) { scanQRdata ->
            CustomerData().let {
                it.id = scanQRdata.userId
                it.name = scanQRdata.name
                it.address = scanQRdata.address
                it.contactNumber = scanQRdata.contactNumber
                it.active = scanQRdata.active
                binding.profileData = it
            }
        }
    }
}