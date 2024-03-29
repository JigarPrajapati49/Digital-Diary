package com.rayo.digitaldiary.ui.settings

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.databinding.FragmentResetPasswordBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.helper.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordFragment :
    BaseFragment<FragmentResetPasswordBinding, ResetPasswordViewModel?>(), ResetPasswordPresenter {

    private var email: String? = null

    override val viewModel: ResetPasswordViewModel by viewModels()

    override fun getFragmentId(): Int {
        return R.layout.fragment_reset_password
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.presenter = this
        email = preferenceManager.getPref(Constants.prefEmail, "")

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        viewModel.resetPasswordSuccess.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            it.message?.let { it1 ->
                context?.toast(it1)
                findNavController().popBackStack()
            }
        }
    }

    override fun onSubmitButtonClick() {
        if (Utils.isSingleClick()){
            val oldPassword = binding.etOldPassword.text.toString()
            val newPassword = binding.etNewPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            isValidPassword(oldPassword, newPassword, confirmPassword)?.let {
                context?.let { it1 ->
                    dialogHelper.showOneButtonDialog(
                        it1,
                        Utils.getTranslatedValue(resources.getString(it))
                    )
                }
            } ?: run {
                showProgressDialog()
                email?.let { viewModel.changePassword(oldPassword, newPassword) }
            }
        }
    }

    private fun isValidPassword(oldPassword: String, newPassword: String, confirmPassword: String): Int? {
        return when {
            oldPassword.trim().isEmpty() -> {
                binding.etOldPassword.requestFocus()
                R.string.enter_old_password
            }
            !Utils.isValidPassword(oldPassword) -> {
                binding.etOldPassword.requestFocus()
                R.string.enter_valid_password
            }
            newPassword.trim().isEmpty() -> {
                binding.etNewPassword.requestFocus()
                R.string.enter_new_password
            }
            !Utils.isValidPassword(newPassword) -> {
                binding.etNewPassword.requestFocus()
                R.string.enter_valid_password
            }
            confirmPassword.trim().isEmpty() -> {
                binding.etConfirmPassword.requestFocus()
                R.string.enter_confirm_password
            }
            (newPassword != confirmPassword) -> {
                binding.etConfirmPassword.requestFocus()
                R.string.password_and_confirm_password
            }
            else -> {
                null
            }
        }
    }


}