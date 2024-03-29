package com.rayo.digitaldiary.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.databinding.FragmentCompleteGoogleLoginBinding
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.helper.toast
import com.rayo.digitaldiary.ui.OwnerMainActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
 * Created by Mittal Varsani on 11/04/23.
 *
 * @author Mittal Varsani
 */

@AndroidEntryPoint
class CompleteGoogleLoginFragment :
    BaseFragment<FragmentCompleteGoogleLoginBinding, CompleteGoogleLoginViewModel>(),
    RegisterPresenter {

    override val viewModel: CompleteGoogleLoginViewModel by viewModels()

    override fun getFragmentId(): Int {
        return R.layout.fragment_complete_google_login
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.presenter = this
        binding.countryCodePicker.setCountryForNameCode(Locale.ENGLISH.country)
        viewModel.registerSuccess.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            it.message?.let { it1 -> context?.toast(it1) }
            startActivity(Intent(context, OwnerMainActivity::class.java))
            activity?.finish()
        }
    }

    override fun onRegisterClick() {
        val businessName = binding.etBusinessName.text.toString().trim()
        val contactNumber = binding.etPhoneNumber.text.toString().trim()
        isValidRegisterForm(
            businessName, contactNumber
        )?.let {
            context?.let { it1 ->
                dialogHelper.showOneButtonDialog(
                    it1, Utils.getTranslatedValue(resources.getString(it))
                )
            }
        } ?: run {
            showProgressDialog()
            viewModel.callCompleteGoogleRegistrationAPI(
                businessName,
                contactNumber,
                "+" + binding.countryCodePicker.selectedCountryCode.toString(),
                binding.countryCodePicker.selectedCountryNameCode.toString()
            )
        }
    }

    override fun onBackClick() {
        findNavController().popBackStack()
    }

    private fun isValidRegisterForm(
        businessName: String,
        contactNumber: String,
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
            else -> {
                null
            }
        }
    }
}