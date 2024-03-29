package com.rayo.digitaldiary.ui.staff

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseBottomSheetDialogFragment
import com.rayo.digitaldiary.databinding.DialogAddStaffBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Mittal Varsani on 12/04/23.
 *
 * @author Mittal Varsani
 */

@AndroidEntryPoint
class AddStaffDialogFragment(
    private val staffPresenter: StaffPresenter, private val mStaffData: StaffData? = null
) : BaseBottomSheetDialogFragment<DialogAddStaffBinding, AddStaffViewModel>(), AddStaffPresenter {

    private val copyStaffData = mStaffData?.copy(mStaffData)
    override val viewModel: AddStaffViewModel by viewModels()

    override fun getDialogFragmentId(): Int {
        return R.layout.dialog_add_staff
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.staffData = copyStaffData ?: StaffData()
        binding.addStaffPresenter = this
        viewModel.addStaffSuccess.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            if (copyStaffData != null) {
                mStaffData?.update(mStaffData, copyStaffData)
                it.staffData?.let { it1 -> staffPresenter.onStaffMemberUpdatedSuccessfully(it1) }
            } else {
                it.staffData?.let { it1 -> staffPresenter.onStaffMemberAddedSuccessfully(it1) }
            }
            context?.let { it1 ->
                dialogHelper.showOneButtonDialog(it1, Utils.getTranslatedValue(it.message.toString()))
            }
            dismiss()
        }
    }

    override fun onAddStaffClick(staffData: StaffData) {
        if (Utils.isSingleClick()){
            staffData.active =
                if (binding.radioActive.isChecked) Constants.STATES_ACTIVE else Constants.STATES_INACTIVE
            isValidAddStaffForm(staffData)?.let {
                context?.let { it1 -> dialogHelper.showOneButtonDialog(it1, Utils.getTranslatedValue(resources.getString(it))) }
            } ?: run {
                showProgressDialog()
                viewModel.callAddOrUpdateStaffAPI(staffData)
            }
        }
    }

    private fun isValidAddStaffForm(staffData: StaffData): Int? {
        return when {
            staffData.name.trim().isEmpty() -> {
                binding.etStaffName.requestFocus()
                R.string.enter_name
            }
            staffData.contactNumber.trim()
                .isNotEmpty() && !Utils.isValidPhoneNumber(staffData.contactNumber.trim()) -> {
                binding.etPhoneNumber.requestFocus()
                R.string.enter_valid_contact_number
            }
            staffData.email.trim().isNotEmpty() && !Utils.isValidEmail(staffData.email.trim()) -> {
                binding.etEmail.requestFocus()
                R.string.invalid_email
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
            staffPresenter: StaffPresenter,
            staffData: StaffData? = null,
        ) {
            val addStaffDialog = AddStaffDialogFragment(staffPresenter, staffData)
            addStaffDialog.show(fragmentManager, TAG)
        }
    }
}