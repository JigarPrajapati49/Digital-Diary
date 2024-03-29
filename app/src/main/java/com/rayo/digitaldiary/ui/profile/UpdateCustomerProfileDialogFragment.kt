package com.rayo.digitaldiary.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseBottomSheetDialogFragment
import com.rayo.digitaldiary.databinding.FragmentUpdateCustomerProfileDialogBinding
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.customer.AddCustomerViewModel
import com.rayo.digitaldiary.ui.customer.CustomerData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateCustomerProfileDialogFragment(
    private val profilePresenter: CustomerProfilePresenter,
    private val customerData: CustomerData
) :
    BaseBottomSheetDialogFragment<FragmentUpdateCustomerProfileDialogBinding, AddCustomerViewModel>(),
    UpdateCustomerProfilePresenter {

    override val viewModel: AddCustomerViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.presenter = this

        binding.customerProfileData = customerData

        viewModel.addCustomerSuccess.observe(viewLifecycleOwner) {
            Log.e(
                TAG,
                "onViewCreated: -------------Customer Profile Update Success ------${it.success}"
            )
            dismissProgressDialog()
            dismiss()
        }
    }

    override fun getDialogFragmentId(): Int {
        return R.layout.fragment_update_customer_profile_dialog
    }

    companion object {
        private const val TAG = "UpdateProfileDialog"
        fun show(
            fragmentManager: FragmentManager,
            profilePresenter: CustomerProfilePresenter,
            customerData: CustomerData
        ) {
            val updateProfileDialogFragment =
                UpdateCustomerProfileDialogFragment(profilePresenter, customerData)
            updateProfileDialogFragment.show(fragmentManager, TAG)
        }
    }

    override fun onUpdateClick(customerData: CustomerData) {
        isValidAddCustomerForm(
            customerData.name.trim(),
            customerData.contactNumber.trim()
        )?.let {
            context?.let { it1 ->
                dialogHelper.showOneButtonDialog(
                    it1,
                    resources.getString(it)
                )
            }
        } ?: run {
            showProgressDialog()
            viewModel.callUpdateCustomerProfile(customerData, true)
        }
    }

    override fun onCancelClick() {
        dismiss()
    }

    private fun isValidAddCustomerForm(name: String, contactNumber: String): Int? {
        return when {
            name.isEmpty() -> {
                binding.etCustomerName.requestFocus()
                R.string.enter_name
            }
            contactNumber.isNotEmpty() && !Utils.isValidPhoneNumber(contactNumber) -> {
                binding.etPhoneNumber.requestFocus()
                R.string.enter_valid_contact_number
            }
            else -> {
                null
            }
        }
    }
}
