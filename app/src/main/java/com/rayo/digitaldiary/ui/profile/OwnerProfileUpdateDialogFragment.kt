package com.rayo.digitaldiary.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseBottomSheetDialogFragment
import com.rayo.digitaldiary.databinding.DialogUpdateProfileBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.CurrencyData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OwnerProfileUpdateDialogFragment(private val profilePresenter: ProfilePresenter) :
    BaseBottomSheetDialogFragment<DialogUpdateProfileBinding, OwnerProfileUpdateViewModel>(),
    UpdateProfilePresenter {

    private var selectedCurrencyObject: CurrencyData? = null

    override val viewModel: OwnerProfileUpdateViewModel by viewModels()

    override fun getDialogFragmentId() = R.layout.dialog_update_profile

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.presenter = this
        setProfileData()
        viewModel.updateProfileSuccess.observe(viewLifecycleOwner) {
            it.data?.let { data ->
                with(preferenceManager) {
                    savePref(Constants.prefCountryCode, data.countryCode)
                    savePref(Constants.prefCurrencySymbol, data.currencySymbol)
                    savePref(Constants.prefCurrencyCountry, data.currencyCountryName)
                }
            }
            dismissProgressDialog()
            profilePresenter.onProfileUpdated()
            dismiss()
        }
    }

    private fun setProfileData() {
        binding.apply {
            val countryCode = preferenceManager.getPref(Constants.prefCountryCode, "+91").replace("+", "").toInt()
            countryCodePicker.setCountryForPhoneCode(countryCode)
            etBusinessName.setText(preferenceManager.getPref(Constants.prefBusinessName, ""))
            etPhoneNumber.setText(preferenceManager.getPref(Constants.prefContactNumber, ""))
            etCurrency.setText(
                preferenceManager.getPref(Constants.prefCurrencyCountry, "")
                        + " (" +
                        preferenceManager.getPref(Constants.prefCurrencySymbol, "")
                        + ")"
            )
        }
    }

    override fun updateClick() {
        isValidUpdateForm(
            binding.etBusinessName.text.toString().trim(),
            binding.etPhoneNumber.text.toString().trim()
        )?.let {
            context?.let { it1 ->
                dialogHelper.showOneButtonDialog(it1, Utils.getTranslatedValue(resources.getString(it)))
            }
        } ?: run {
            showProgressDialog()
            viewModel.callUpdateProfileAPI(
                binding.etBusinessName.text.toString(), binding.etPhoneNumber.text.toString(),
                "+" + binding.countryCodePicker.selectedCountryCode.toString(),
                binding.countryCodePicker.selectedCountryNameCode.toString(),
                preferenceManager.getPref(Constants.prefCurrencyCountry, ""),
                preferenceManager.getPref(Constants.prefCurrencySymbol, "")
            )
        }
    }

    private fun isValidUpdateForm(
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

    override fun onCurrencySelected(currencyData: CurrencyData) {
        selectedCurrencyObject = currencyData
        preferenceManager.savePref(Constants.prefCurrencyCountry, currencyData.countryName)
        preferenceManager.savePref(Constants.prefCurrencySymbol, currencyData.symbol)
        binding.etCurrency.setText(currencyData.countryName + '(' + currencyData.symbol + ')')
    }

    override fun onSelectCurrencyClick() {
        if (Utils.isSingleClick()){
            if (networkInterceptor.isInternetAvailable()) {
                SelectCurrencyDialogFragment.show(
                    parentFragmentManager,
                    this,
                    viewModel.lastSelectedCountryName
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

    override fun setLastSelectedCurrency(countryName: String) {
        viewModel.lastSelectedCountryName = countryName
    }

    override fun onCancelClick() {
        dismiss()
    }

    companion object {
        private const val TAG = "UpdateProfileDialog"
        fun show(
            fragmentManager: FragmentManager,
            profilePresenter: ProfilePresenter
        ) {
            val ownerProfileUpdateDialogFragment = OwnerProfileUpdateDialogFragment(profilePresenter)
            ownerProfileUpdateDialogFragment.show(fragmentManager, TAG)
        }
    }
}
