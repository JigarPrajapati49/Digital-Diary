package com.rayo.digitaldiary.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.databinding.FragmentSessionBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.helper.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SessionFragment : BaseFragment<FragmentSessionBinding, SessionVewModel>(), LogoutPresenter {
    private val otherDeviceList: MutableList<SessionData> = ArrayList()
    override val viewModel: SessionVewModel by viewModels()

    override fun getFragmentId(): Int {
        return R.layout.fragment_session
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            logoutPresenter = this@SessionFragment
            layoutThisDevice.logoutPresenter = this@SessionFragment
        }
        val userId = arguments?.getString(Constants.userId)
        setSessionAdapter()
        getSessionData(userId)
        if (preferenceManager.getPref(Constants.prefUserType, "") == Constants.USER_TYPE_STAFF || preferenceManager.getPref(
                Constants.prefUserType,
                ""
            ) == Constants.USER_TYPE_CUSTOMER
        ) {
            binding.isThisDeviceVisible = false
            binding.isOtherDeviceVisible = false
        } else {
            binding.isThisDeviceVisible = userId == preferenceManager.getPref(Constants.prefUserId, "")
            binding.isOtherDeviceVisible = userId == preferenceManager.getPref(Constants.prefUserId, "")
        }

        viewModel.logoutAllDeviceSuccess.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            otherDeviceList.clear()
            binding.sessionAdapter?.notifyDataSetChanged()
            binding.isOtherDeviceVisible = otherDeviceList.isNotEmpty()
            binding.errorMessage = Utils.getTranslatedValue(getString(R.string.no_data_found))
        }
        viewModel.sessionList.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            otherDeviceList.clear()
            for (sessionData in it) {
                if (sessionData.deviceId != preferenceManager.getPref(Constants.prefDeviceId, "")) {
                    otherDeviceList.add(sessionData)
                } else {
                    binding.isThisDeviceVisible = true
                    binding.layoutThisDevice.sessionData = sessionData
                }
            }
            binding.isOtherDeviceVisible = otherDeviceList.isNotEmpty()
            binding.sessionAdapter?.notifyDataSetChanged()

            if (it.isEmpty()) {
                binding.errorMessage = Utils.getTranslatedValue(getString(R.string.no_data_found))
            } else {
                binding.errorMessage = ""
            }
            if(userId != preferenceManager.getPref(Constants.prefUserId, "") && it.size == 1) {
                binding.isOtherDeviceVisible =false
            }
        }

        viewModel.sessionLogoutSuccess.observe(viewLifecycleOwner) { deviceId ->
            dismissProgressDialog()
            for (i in 0 until otherDeviceList.size) {
                if (otherDeviceList[i].deviceId == deviceId) {
                    otherDeviceList.removeAt(i)
                    binding.sessionAdapter?.notifyItemRemoved(i)
                    binding.sessionAdapter?.notifyItemRangeChanged(i, otherDeviceList.size)
                    break
                }
            }
            if (otherDeviceList.isEmpty() && binding.layoutThisDevice.sessionData == null) {
                binding.errorMessage = Utils.getTranslatedValue(getString(R.string.no_data_found))
            } else {
                binding.errorMessage = ""
            }

            if (otherDeviceList.size == 1) {
                binding.isOtherDeviceVisible = false
            } else {
                binding.isOtherDeviceVisible = otherDeviceList.isNotEmpty()
            }
            context?.toast(Utils.getTranslatedValue(getString(R.string.success_logout)))
        }
    }

    private fun getSessionData(userId: String?) {
        showProgressDialog()
        val sessionUserId = if (userId.isNullOrEmpty()) {
            preferenceManager.getPref(Constants.prefUserId, "")
        } else {
            userId
        }
        binding.currentSessionUserId = sessionUserId
        viewModel.getSessionData(sessionUserId)
    }

    private fun setSessionAdapter() {
        binding.sessionAdapter = SessionAdapter(otherDeviceList, this)
    }

    override fun onLogoutAllDeviceClick(userId: String) {
        context?.let {
            dialogHelper.showTwoButtonDialog(
                it,
                Utils.getTranslatedValue(getString(R.string.logout_confirmation))
            ) { dialog, _ ->
                showProgressDialog()
                viewModel.logoutAllDevice(userId)
            }
        }
    }

    override fun logoutClick(sessionData: SessionData) {
        context?.let {
            dialogHelper.showTwoButtonDialog(it, Utils.getTranslatedValue(getString(R.string.logout_confirmation))) { dialog, _ ->
                dialog.dismiss()
                showProgressDialog()
                viewModel.logout(sessionData)
            }
        }
    }
}