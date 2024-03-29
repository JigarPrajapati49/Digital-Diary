package com.rayo.digitaldiary.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.databinding.FragmentTroubleShootingBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TroubleShootingFragment : BaseFragment<FragmentTroubleShootingBinding, TroubleShootingViewModel>() {

    override val viewModel: TroubleShootingViewModel by viewModels()
    override fun getFragmentId(): Int {
        return R.layout.fragment_trouble_shooting
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        if (preferenceManager.getPref(Constants.prefUserType, "") == Constants.USER_TYPE_STAFF) {
            Coroutines.ioThenMain({
                viewModel.getTotalRecordOfStaffOrder(preferenceManager.getPref(Constants.prefUserId, ""))
            }, {
                if (it != null) {
                    binding.totalRecordsOrder = it
                }
            })

            Coroutines.ioThenMain({
                viewModel.getTotalRecordOfStaffPayment(preferenceManager.getPref(Constants.prefUserId, ""))
            }, {
                if (it != null) {
                    binding.totalRecordsPayment = it
                }
            })

            Coroutines.ioThenMain({
                viewModel.getTotalRecordOfStaffUnSyncPayment(preferenceManager.getPref(Constants.prefUserId, ""))
            }, {
                if (it != null) {
                    binding.unSyncRecordPayment = it
                }
            })

            Coroutines.ioThenMain({
                viewModel.getTotalRecordOfStaffUnSyncOrder(preferenceManager.getPref(Constants.prefUserId, ""))
            }, {
                if (it != null) {
                    binding.unSyncRecordOrder = it
                }
            })
        } else {
            Coroutines.ioThenMain({
                viewModel.getTotalRecordOrder()
            }, {
                if (it != null) {
                    binding.totalRecordsOrder = it
                }
            })


            Coroutines.ioThenMain({
                viewModel.getTotalRecordPayment()
            }, {
                if (it != null) {
                    binding.totalRecordsPayment = it
                }
            })

            Coroutines.ioThenMain({
                viewModel.unSyncRecordOrder()
            }, {
                if (it != null) {
                    binding.unSyncRecordOrder = it
                }
            })

            Coroutines.ioThenMain({
                viewModel.unSyncRecordPayment()
            }, {
                if (it != null) {
                    binding.unSyncRecordPayment = it
                }
            })
        }
    }
}