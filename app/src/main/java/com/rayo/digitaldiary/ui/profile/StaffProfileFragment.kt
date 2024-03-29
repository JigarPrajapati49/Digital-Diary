package com.rayo.digitaldiary.ui.profile

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.databinding.FragmentStaffProfileBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.staff.StaffData
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Mittal Varsani on 01/03/23.
 *
 * @author Mittal Varsani
 */
@AndroidEntryPoint
class StaffProfileFragment : BaseFragment<FragmentStaffProfileBinding, BaseViewModel?>(),
    StaffProfilePresenter {

    override val viewModel: BaseViewModel? = null
    override fun getFragmentId(): Int {
        return R.layout.fragment_staff_profile
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profilePresenter = this


        binding.isEditVisible = preferenceManager.getPref(
            Constants.prefUserType,
            ""
        ) == Constants.CUSTOMER || preferenceManager.getPref(
            Constants.prefUserType,
            ""
        ) == Constants.STAFF
        setProfileData()
    }

    override fun onEditClick(staffData: StaffData) {
        if (Utils.isSingleClick()){
            if (networkInterceptor.isInternetAvailable()) {
                StaffUpdateProfileDialogFragment.show(
                    parentFragmentManager,
                    staffData
                )
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

    override fun onLoginDevicesClick(userId: String?) {
        Bundle().let {
            it.putString(Constants.userId,userId)
            findNavController().navigate(R.id.staffLoginDetailFragment,it)
        }
    }

    private fun setProfileData() {
        StaffData().let {
            it.id = preferenceManager.getPref(Constants.prefUserId, "")
            it.email = preferenceManager.getPref(Constants.prefEmail, "")
            it.contactNumber = preferenceManager.getPref(Constants.prefContactNumber, "")
            it.name = preferenceManager.getPref(Constants.prefName, "")
            it.active = preferenceManager.getPref(Constants.prefIsActive, 0)
            binding.profileData = it
            binding.countryCode = preferenceManager.getPref(Constants.prefCountryCode)
        }
    }
}
