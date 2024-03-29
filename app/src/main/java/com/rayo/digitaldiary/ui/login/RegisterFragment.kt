package com.rayo.digitaldiary.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rayo.digitaldiary.DigitalDiaryApplication
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.databinding.FragmentRegisterBinding
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.helper.toast
import com.rayo.digitaldiary.ui.OwnerMainActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Mittal Varsani on 12/04/22.
 *
 * @author Mittal Varsani
 */

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding, RegisterViewModel>(),
    RegisterPresenter {

    override val viewModel: RegisterViewModel by viewModels()

    override fun getFragmentId(): Int {
        return R.layout.fragment_register
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.presenter = this
        try {
            binding.countryCodePicker.setCountryForPhoneCode(binding.ccp.defaultCountryCode.toInt())
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }

        viewModel.registerSuccess.observe(viewLifecycleOwner) {
            DigitalDiaryApplication.instance.isShowSyncProgress = true
            dismissProgressDialog()
            it.message?.let { it1 -> context?.toast(it1) }
            startActivity(Intent(context, OwnerMainActivity::class.java))
            activity?.finish()
        }
    }

    override fun onSingInClick() {
        findNavController().popBackStack()
    }

    override fun onRegisterClick() {
        val businessName = binding.etBusinessName.text.toString().trim()
        val contactNumber = binding.etPhoneNumber.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        isValidRegisterForm(
            businessName,
            contactNumber,
            email,
            password
        )?.let {
            context?.let { it1 ->
                dialogHelper.showOneButtonDialog(
                    it1,
                    Utils.getTranslatedValue(resources.getString(it))
                )
            }
        } ?: run {
            showProgressDialog()
            viewModel.callRegisterAPI(
                email,
                password,
                "+" + binding.countryCodePicker.selectedCountryCode.toString(),
                binding.countryCodePicker.selectedCountryNameCode.toString(),
                contactNumber,
                businessName
            )
        }
    }

    override fun onBackClick() {
        findNavController().popBackStack()
    }

    private fun isValidRegisterForm(
        businessName: String,
        contactNumber: String,
        email: String,
        password: String,
    ): Int? {
        return when {
            businessName.isEmpty() -> {
                binding.etBusinessName.requestFocus()
                R.string.enter_business_name
            }
            contactNumber.isNotEmpty() && !Utils.isValidPhoneNumber(contactNumber) -> {
                binding.etPhoneNumber.requestFocus()
                R.string.enter_valid_contact_number
            }
            email.isEmpty() -> {
                binding.etEmail.requestFocus()
                R.string.enter_email
            }
            !Utils.isValidEmail(email) -> {
                binding.etEmail.requestFocus()
                R.string.invalid_email
            }
            password.isEmpty() -> {
                binding.etPassword.requestFocus()
                R.string.enter_password
            }
            !Utils.isValidPassword(password) -> {
                binding.etPassword.requestFocus()
                R.string.enter_valid_password
            }
            else -> {
                null
            }
        }
    }
}