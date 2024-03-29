package com.rayo.digitaldiary.ui.settings

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.FragmentManager
import com.onesignal.OneSignal
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseDialogFragment
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.databinding.FragmentLanguageSelectionBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.setAppLocale
import com.rayo.digitaldiary.ui.LanguageData
import com.rayo.digitaldiary.ui.languageTranslation.LanguageReceiver
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale


@AndroidEntryPoint
class SelectLanguageDialogFragment(
    private val languageList: ArrayList<LanguageData>,
    private val settingsPresenter: SettingsPresenter,
) :
    BaseDialogFragment<FragmentLanguageSelectionBinding, BaseViewModel?>(),
    LanguageSelectionPresenter {

    private var languageName = ""

    override val viewModel: BaseViewModel? = null

    override fun getDialogFragmentId(): Int {
        return R.layout.fragment_language_selection
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.presenter = this

        languageName = preferenceManager.getPref(Constants.prefLanguage, Constants.languageDefault)

        val position = languageList.indexOfFirst {
            it.languageName == languageName
        }

        if (preferenceManager.getPref(Constants.prefLanguageCode, "").isEmpty()) {
            dialog?.setOnCancelListener {
                val languageData = LanguageData()
                languageData.radioCheck = true
                languageData.languageCode = Constants.languageCodeDefault
                languageData.languageName = Constants.languageDefault
                onSelectClick(languageData)
            }
        }

        binding.rvCurrencyList.scrollToPosition(position)

        if (languageList.size == 0) {
            binding.isNoLanguageFound = true
        }

        for (i in languageList) {
            if (i.languageName == languageName) {
                i.radioCheck = true
                binding.settingData = i
            }
        }
        setProductAdapter()
    }

    private fun setProductAdapter() {
        for (data in languageList) {
            data.radioCheck = data.languageName == languageName
        }
        binding.languageAdapter = LanguageAdapter(languageList, this, preferenceManager)
        binding.languageAdapter?.notifyDataSetChanged()
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase(Locale.ENGLISH)
                filterWithQuery(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterWithQuery(query: String) {
        if (query.isNotEmpty()) {
            val filteredList: List<LanguageData> = onFilterChanged(query)
            binding.languageAdapter = LanguageAdapter(filteredList, this, preferenceManager)
            binding.languageAdapter?.notifyDataSetChanged()
            toggleRecyclerView(filteredList)
        } else if (query.isEmpty()) {
            binding.languageAdapter = LanguageAdapter(languageList, this, preferenceManager)
            binding.languageAdapter?.notifyDataSetChanged()
        }
    }

    private fun toggleRecyclerView(filteredList: List<LanguageData>) {
        if (filteredList.isEmpty()) {
            binding.isNoLanguageFound = true
            binding.rvCurrencyList.visibility = View.INVISIBLE
        } else {
            binding.isNoLanguageFound = false
            binding.rvCurrencyList.visibility = View.VISIBLE
        }
    }

    private fun onFilterChanged(filterQuery: String): List<LanguageData> {
        val filteredList = ArrayList<LanguageData>()
        for (currentList in languageList) {
            if (currentList.languageName.lowercase(Locale.ENGLISH).contains(filterQuery)) {
                filteredList.add(currentList)
            }
        }
        return filteredList
    }

    override fun onLanguageSelect(languageData: LanguageData) {
        binding.settingData = languageData
        for (i in 0 until languageList.size) {
            languageList[i].radioCheck = languageData.languageName == languageList[i].languageName
        }
        binding.languageAdapter?.notifyDataSetChanged()
    }

    override fun onSelectClick(languageData: LanguageData?) {
        if (languageData?.languageName == preferenceManager.getPref(Constants.prefLanguage)) {
            dismissNow()

        } else {
            val isLanguageSelectionFirstTime =
                preferenceManager.getPref(Constants.prefLanguageCode, "").isEmpty()
            languageData?.let {
                preferenceManager.savePref(Constants.prefLanguage, it.languageName)
                preferenceManager.savePref(Constants.prefLanguageCode, it.languageCode)
            }
            settingsPresenter.onLanguageSelected(
                preferenceManager.getPref(
                    Constants.prefLanguage,
                    Constants.languageDefault
                )
            )
            requireContext().setAppLocale(
                preferenceManager.getPref(
                    Constants.prefLanguageCode,
                    Constants.languageCodeDefault
                )
            )
            if (isLanguageSelectionFirstTime) {
                dismissNow()
            } else {
                val intent = Intent(requireContext(), LanguageReceiver::class.java)
                context?.sendBroadcast(intent)
            }
            dismissNow()
        }
    }

    override fun onCancelClick() {
        dialog?.cancel()
    }

    companion object {
        private const val TAG = "SettingDialogFragment"
        fun show(
            fragmentManager: FragmentManager,
            languageList: ArrayList<LanguageData>,
            settingsPresenter: SettingsPresenter,
        ) {
            val selectLanguageDialogFragment =
                SelectLanguageDialogFragment(languageList, settingsPresenter)
            selectLanguageDialogFragment.show(fragmentManager, TAG)
        }
    }
}