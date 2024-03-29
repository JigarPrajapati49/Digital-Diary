package com.rayo.digitaldiary.ui.customer

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.baseClasses.CallMessageBottomSheetDialog
import com.rayo.digitaldiary.databinding.FragmentCustomerDetailsBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.OwnerMainActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Mittal Varsani on 14/04/23.
 *
 * @author Mittal Varsani
 */

@AndroidEntryPoint
class CustomerDetailsFragment :
    BaseFragment<FragmentCustomerDetailsBinding, CustomerDetailViewModel>(),
    CustomerDetailsPresenter, CustomerPresenter {

    override val viewModel: CustomerDetailViewModel by viewModels()

    override fun getFragmentId(): Int {
        return R.layout.fragment_customer_details
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.countryCode = preferenceManager.getPref(Constants.prefCountryCode, "")
        binding.customerDetailPresenter = this
        binding.qrCode = viewModel.QRCode

        binding.editVisibility = if (activity is OwnerMainActivity) {
            View.VISIBLE
        } else {
            View.GONE
        }
        binding.isSessionVisible = preferenceManager.getPref(Constants.prefUserType, "") != Constants.USER_TYPE_STAFF
        binding.customerData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.arguments?.getParcelable(Constants.customerDetail, CustomerData::class.java)
        } else {
            this.arguments?.getParcelable(Constants.customerDetail)
        }

        viewModel.getCustomerProfileData.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            binding.customerData = it.customerData[0]
        }

        viewModel.generateQRSuccess.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            binding.qrCode = viewModel.QRCode
        }
    }


    override fun onEditClick(customerData: CustomerData) {
        if (Utils.isSingleClick()){
            if (networkInterceptor.isInternetAvailable()) {
                AddCustomerDialogFragment.show(parentFragmentManager, this, customerData)
            } else {
                context?.let {
                    dialogHelper.showOneButtonDialog(
                        it, Utils.getTranslatedValue(it.getString(R.string.no_internet))
                    )
                }
            }
        }
    }

    override fun onCustomerAddedSuccessfully(customerData: CustomerData) {
        binding.customerData = customerData
    }

    override fun onCustomerUpdatedSuccessfully(customerData: CustomerData) {
        binding.customerData = customerData
    }

    override fun onGenerateQRClick(customerId: String) {
        showProgressDialog()
        viewModel.generateQRCode(customerId)
    }

    override fun onShowHistoryClick(customerId: String) {
        Bundle().let {
            it.putString(Constants.customerId, customerId)
            findNavController().navigate(R.id.customerHistoryFragment, it)
        }
    }

    override fun onPhoneNumberClick(phoneNumber: String?) {
        if (Utils.isSingleClick()){
            phoneNumber?.let { CallMessageBottomSheetDialog.display(childFragmentManager, it) }
        }
    }

    override fun onCreateOrderClick(customerData: CustomerData) {
        Bundle().let {
            it.putParcelable(Constants.customerData, customerData)
            findNavController().navigate(R.id.createOrderFragment, it)
        }
    }

    override fun onCustomerLoginDevicesClick(userId: String?) {
        Bundle().let {
            it.putString(Constants.userId, userId)
            findNavController().navigate(R.id.sessionFragment, it)
        }
    }

    override fun onQRClick(customerId: String) {
        Bundle().let {
            it.putString(Constants.customerId, customerId)
            findNavController().navigate(R.id.generateQrCodeFragment, it)
        }
    }

    override fun onPaymentsClick(customerData: CustomerData) {
        Bundle().let {
            it.putString(Constants.historyType, Constants.CUSTOMER)
            it.putString(Constants.customerId, customerData.id)
            it.putParcelable(Constants.customerData, customerData)
            findNavController().navigate(R.id.paymentHistoryFragment, it)
        }
    }
}