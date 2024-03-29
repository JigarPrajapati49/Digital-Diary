package com.rayo.digitaldiary.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.FragmentManager
import com.rayo.digitaldiary.DigitalDiaryApplication
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseDialogFragment
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.databinding.DialogSelectCurrencyBinding
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.CurrencyData
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Mittal Varsani on 26/07/23.
 *
 * @author Mittal Varsani
 */

@AndroidEntryPoint
class SelectCurrencyDialogFragment(
    private val updateProfilePresenter: UpdateProfilePresenter,
    private val lastSelectedCountry: String
) :
    BaseDialogFragment<DialogSelectCurrencyBinding, BaseViewModel?>(), CurrencyPresenter,
    SearchView.OnQueryTextListener {

    private val currencyList: MutableList<CurrencyData> = ArrayList()
    private val copyCurrencyList: MutableList<CurrencyData> = ArrayList()

    override val viewModel: BaseViewModel? = null

    override fun getDialogFragmentId(): Int {
        return R.layout.dialog_select_currency
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()

        // Selected Currency show in top when dialog is open // match country name and last selected countryName
        val position = currencyList.indexOfFirst {
            it.countryName == lastSelectedCountry
        }
        binding.rvCurrencyList.scrollToPosition(position)
        binding.searchCurrency.setOnQueryTextListener(this)
        binding.currencyPresenter = this
        binding.currencyAdapter = CurrencyAdapter(currencyList, updateProfilePresenter)
    }

    private fun initList() {
        currencyList.clear()
        copyCurrencyList.clear()
        currencyList.addAll(DigitalDiaryApplication.currencyList)
        for(item in currencyList) {
            item.isSelected =
                item.countryName == lastSelectedCountry
        }
        copyCurrencyList.addAll(currencyList)
    }

    override fun onSelectCurrencyClick() {
        dismiss()
        binding.currencyAdapter?.getSelectedCurrency()
            ?.let { updateProfilePresenter.onCurrencySelected(it) }
    }

    override fun onCancelClick() {
        dismiss()
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        binding.searchCurrency.clearFocus()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        currencyList.clear()
        for (i in 0 until copyCurrencyList.size) {
            if (copyCurrencyList[i].countryName.contains(newText.toString(), ignoreCase = true)) {
                copyCurrencyList[i].isSelected =
                    lastSelectedCountry == copyCurrencyList[i].countryName
                currencyList.add(copyCurrencyList[i])
            }
        }
        if (currencyList.isEmpty()) {
            binding.errorMessage = Utils.getTranslatedValue(getString(R.string.no_country))
        } else {
            binding.errorMessage = ""
        }
        binding.currencyAdapter?.notifyDataSetChanged()
        return false
    }

    companion object {
        private const val TAG = "SelectCurrencyDialog"
        fun show(
            fragmentManager: FragmentManager,
            updateProfilePresenter: UpdateProfilePresenter,
            lastSelectedCountry: String
        ) {
            val selectCurrencyDialog =
                SelectCurrencyDialogFragment(updateProfilePresenter, lastSelectedCountry)
            selectCurrencyDialog.show(fragmentManager, TAG)
        }
    }
}