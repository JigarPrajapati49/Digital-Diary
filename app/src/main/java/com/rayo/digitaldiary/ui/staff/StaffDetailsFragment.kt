package com.rayo.digitaldiary.ui.staff

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.baseClasses.CallMessageBottomSheetDialog
import com.rayo.digitaldiary.databinding.FragmentStaffDetailsBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Mittal Varsani on 14/04/23.
 *
 * @author Mittal Varsani
 */

@AndroidEntryPoint
class StaffDetailsFragment : BaseFragment<FragmentStaffDetailsBinding, StaffDetailViewModel>(),
    StaffDetailsPresenter, StaffPresenter {

    override val viewModel: StaffDetailViewModel by viewModels()

    override fun getFragmentId(): Int {
        return R.layout.fragment_staff_details
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.countryCode = preferenceManager.getPref(Constants.prefCountryCode, "")
        binding.staffDetailPresenter = this
        binding.staffData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.arguments?.getParcelable(Constants.staffDetail, StaffData::class.java)
        } else {
            this.arguments?.getParcelable(Constants.staffDetail)
        }
    }

    override fun onEditClick(staffData: StaffData) {
        if (Utils.isSingleClick()){
            if (networkInterceptor.isInternetAvailable()) {
                AddStaffDialogFragment.show(parentFragmentManager, this, staffData)
            } else {
                context?.let {
                    dialogHelper.showOneButtonDialog(
                        it, Utils.getTranslatedValue(it.getString(R.string.no_internet))
                    )
                }
            }
        }
    }

    override fun onStaffMemberAddedSuccessfully(staffData: StaffData) {
        binding.staffData = staffData
    }

    override fun onStaffMemberUpdatedSuccessfully(staffData: StaffData) {
        binding.staffData = staffData
    }

    override fun onGenerateQRClick(staffId: String) {
        if (networkInterceptor.isInternetAvailable()) {
            showProgressDialog()
            viewModel.generateQRCode(staffId)
        } else {
            context?.let {
                dialogHelper.showOneButtonDialog(
                    it, Utils.getTranslatedValue(it.getString(R.string.no_internet))
                )
            }
        }
    }

    override fun onShowHistoryClick(staffId: String) {
        Bundle().let {
            it.putString(Constants.createdById, staffId)
            findNavController().navigate(R.id.ownerOrStaffOrderHistoryFragment, it)
        }
    }

    override fun onPhoneNumberClick(phoneNumber: String?) {
        if (Utils.isSingleClick()){
            phoneNumber?.let { CallMessageBottomSheetDialog.display(childFragmentManager, it) }
        }
    }

    override fun onStaffLoginDevicesClick(userId: String?) {
        Bundle().let {
            it.putString(Constants.userId, userId)
            findNavController().navigate(R.id.sessionFragment, it)
        }
    }

    override fun onPaymentsClick(staffData: StaffData) {
        Bundle().let {
            it.putString(Constants.historyType, Constants.STAFF)
            it.putString(Constants.staffId, staffData.id)
            findNavController().navigate(R.id.paymentHistoryFragment, it)
        }
    }

    override fun onQrClick(customerId: String) {
        Bundle().let {
            it.putString(Constants.customerId, customerId)
            findNavController().navigate(R.id.generateQrCodeFragment, it)
        }
    }
}