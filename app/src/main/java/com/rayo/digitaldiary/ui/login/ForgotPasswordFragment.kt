package com.rayo.digitaldiary.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.databinding.FragmentForgotPasswordBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.helper.toast
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Mittal Varsani on 02/05/22.
 *
 * @author Mittal Varsani
 */

@AndroidEntryPoint
class ForgotPasswordFragment :
    BaseFragment<FragmentForgotPasswordBinding, ForgotPasswordViewModel>(),
    ForgotPasswordPresenter {
    override val viewModel: ForgotPasswordViewModel by viewModels()
    override fun getFragmentId(): Int {
        return R.layout.fragment_forgot_password
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.presenter = this
        viewModel.forgotPasswordSuccess.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            it.getContentIfNotHandled()?.let { error ->
                error.message?.let { it1 -> context?.toast(it1) }
                Bundle().let { mBundle ->
                    mBundle.putString(Constants.email, binding.etEmail.text.toString())
                    findNavController().navigate(R.id.verifyOtp, mBundle)
                }
            }
        }
    }

    override fun onRequestToChangeClick() {
        val email = binding.etEmail.text.toString()
        isValidEmail(email)?.let {
            context?.let { it1 -> dialogHelper.showOneButtonDialog(it1, Utils.getTranslatedValue(resources.getString(it))) }
        } ?: run {
            showProgressDialog()
            viewModel.callForgotPasswordAPI(email)
        }
    }

    private fun isValidEmail(email: String): Int? {
        return when {
            email.trim().isEmpty() -> {
                binding.etEmail.requestFocus()
                R.string.enter_email
            }
            !Utils.isValidEmail(email) -> {
                binding.etEmail.requestFocus()
                R.string.invalid_email
            }
            else -> {
                null
            }
        }
    }

    override fun onBackToLoginClick() {
        findNavController().popBackStack()
    }
}