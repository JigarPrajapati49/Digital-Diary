package com.rayo.digitaldiary.ui.staff

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.baseClasses.CallMessageBottomSheetDialog
import com.rayo.digitaldiary.databinding.FragmentStaffListingBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Mittal Varsani on 10/04/23.
 *
 * @author Mittal Varsani
 */

@AndroidEntryPoint
class StaffListingFragment : BaseFragment<FragmentStaffListingBinding, StaffViewModel>(),
    StaffPresenter {

    private val allStaffListing: MutableList<StaffData> = ArrayList()
    private val displayStaffListing: MutableList<StaffData> = ArrayList()

    override val viewModel: StaffViewModel by viewModels()

    override fun getFragmentId(): Int {
        return R.layout.fragment_staff_listing
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.staffPresenter = this

        showProgressDialog()
        setStaffListingAdapter()

        viewModel.staffList.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            allStaffListing.clear()
            allStaffListing.addAll(it)
            setStaffData()
        }
    }

    private fun setStaffData() {
        when (binding.btnToggle.checkedButtonId) {
            R.id.btnAll -> {
                onAllClick()
            }

            R.id.btnActive -> {
                onActiveClick()
            }

            else -> {
                onInactiveClick()
            }
        }
    }

    private fun setStaffListingAdapter() {
        binding.staffAdapter = StaffAdapter(
            displayStaffListing,
            this,
            preferenceManager.getPref(Constants.prefCountryCode, "")
        )
    }

    override fun onAddStaffClick() {
        if (Utils.isSingleClick()){
            if (networkInterceptor.isInternetAvailable()) {
                AddStaffDialogFragment.show(parentFragmentManager, this)
            } else {
                context?.let {
                    dialogHelper.showOneButtonDialog(
                        it,
                        Utils.getTranslatedValue(it.getString(R.string.no_internet))
                    )
                }
            }
        }
    }

    override fun onStaffMemberAddedSuccessfully(staffData: StaffData) {
        viewModel.staffList.value?.add(0, staffData)
        displayStaffListing.add(0, staffData)
        allStaffListing.add(0, staffData)
        setStaffData()
    }

    override fun onAllClick() {
        applyStaffFilter(Constants.STATES_ALL)
    }

    override fun onActiveClick() {
        applyStaffFilter(Constants.STATES_ACTIVE)
    }

    override fun onInactiveClick() {
        applyStaffFilter(Constants.STATES_INACTIVE)
    }

    private fun applyStaffFilter(states: Int) {
        displayStaffListing.clear()
        if (states == Constants.STATES_ALL) {
            displayStaffListing.addAll(allStaffListing)
        } else {
            displayStaffListing.addAll(allStaffListing.filter { staffData ->
                staffData.active == states
            })
        }
        binding.errorMessage = if (displayStaffListing.isEmpty()) {
            Utils.getTranslatedValue(getString(R.string.no_staff))
        } else {
            ""
        }
        binding.staffAdapter?.notifyDataSetChanged()
    }

    override fun onItemClick(staffData: StaffData) {
        Bundle().let {
            it.putParcelable(Constants.staffDetail, staffData)
            findNavController().navigate(R.id.staffDetailsFragment, it)
        }
    }

    override fun onPhoneNumberClick(phoneNumber: String?) {
        if (Utils.isSingleClick()){
            phoneNumber?.let { CallMessageBottomSheetDialog.display(childFragmentManager, it) }
        }
    }
}