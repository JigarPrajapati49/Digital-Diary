package com.rayo.digitaldiary.ui.customer

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseBottomSheetDialogFragment
import com.rayo.digitaldiary.databinding.DialogAddCustomerBinding
import com.rayo.digitaldiary.helper.Utils
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Mittal Varsani on 12/04/23.
 *
 * @author Mittal Varsani
 */

@AndroidEntryPoint
class AddCustomerDialogFragment(
    private val customerPresenter: CustomerPresenter,
    private val mCustomerData: CustomerData? = null
) : BaseBottomSheetDialogFragment<DialogAddCustomerBinding, AddCustomerViewModel>(),
    AddCustomerPresenter {

    private val copyCustomerData = mCustomerData?.copy(mCustomerData)

    override val viewModel: AddCustomerViewModel by viewModels()

    override fun getDialogFragmentId(): Int {
        return R.layout.dialog_add_customer
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.customerData = copyCustomerData ?: CustomerData()
        binding.addCustomerPresenter = this
        viewModel.addCustomerSuccess.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            if (copyCustomerData != null) {
                mCustomerData?.update(mCustomerData, copyCustomerData)
                it.customerData?.let { it1 -> customerPresenter.onCustomerUpdatedSuccessfully(it1) }
            } else {
                it.customerData?.let { it1 -> customerPresenter.onCustomerAddedSuccessfully(it1) }
            }
            context?.let { it1 ->
                dialogHelper.showOneButtonDialog(it1, it.message.toString())
            }
            dismiss()
        }
    }

    override fun onAddCustomerClick(customerData: CustomerData) {
        if (Utils.isSingleClick()){
            customerData.active = if (binding.radioActive.isChecked) 1 else 0
            isValidAddCustomerForm(
                customerData.name.trim(),
                customerData.contactNumber.trim()
            )?.let {
                context?.let { it1 ->
                    dialogHelper.showOneButtonDialog(
                        it1,
                        Utils.getTranslatedValue(resources.getString(it))
                    )
                }
            } ?: run {
                showProgressDialog()
                viewModel.callAddOrUpdateCustomerAPI(customerData)
            }
        }
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

    override fun onCancelClick() {
        dismiss()
    }

    companion object {
        private const val TAG = "AddStaffDialog"
        fun show(
            fragmentManager: FragmentManager,
            customerPresenter: CustomerPresenter,
            customerData: CustomerData? = null,
        ) {
            val addStaffDialog = AddCustomerDialogFragment(customerPresenter, customerData)
            addStaffDialog.show(fragmentManager, TAG)
        }
    }
}