package com.rayo.digitaldiary.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseBottomSheetDialogFragment
import com.rayo.digitaldiary.databinding.FragmentUpdateStaffProfileDialogBinding
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.staff.AddStaffViewModel
import com.rayo.digitaldiary.ui.staff.StaffData
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class StaffUpdateProfileDialogFragment(private val staffData: StaffData) :
    BaseBottomSheetDialogFragment<FragmentUpdateStaffProfileDialogBinding, AddStaffViewModel>(),
    UpdateStaffProfilePresenter {

    override val viewModel: AddStaffViewModel by viewModels()

    override fun getDialogFragmentId(): Int {
        return R.layout.fragment_update_staff_profile_dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.presenter = this
        binding.staffData = this@StaffUpdateProfileDialogFragment.staffData
        viewModel.addStaffSuccess.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            dismiss()
        }
    }

    override fun onUpdateClick(staffData: StaffData) {
        isValidAddStaffForm(staffData)?.let {
            context?.let { it1 -> dialogHelper.showOneButtonDialog(it1, Utils.getTranslatedValue(resources.getString(it))) }
        } ?: run {
            showProgressDialog()
            viewModel.callUpdateStaffProfile(staffData, true)
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
        private const val TAG = "UpdateStaffProfileDialog"
        fun show(
            fragmentManager: FragmentManager,
            staffData: StaffData,
        ) {
            val updateProfileDialogFragment = StaffUpdateProfileDialogFragment(staffData)
            updateProfileDialogFragment.show(fragmentManager, TAG)
        }
    }
}