package com.rayo.digitaldiary.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rayo.digitaldiary.databinding.ItemLanguageSelectionBinding
import com.rayo.digitaldiary.helper.PreferenceManager
import com.rayo.digitaldiary.ui.LanguageData

/**
 * Created by Mittal Varsani on 02/03/23.
 *
 * @author Mittal Varsani
 */
class LanguageAdapter(
    private val languageList: List<LanguageData>,
    private val presenter: LanguageSelectionPresenter,
    private val preferenceManager: PreferenceManager
) :
    RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLanguageSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(languageList[position])
    }

    override fun getItemCount(): Int {
        return languageList.size
    }

    inner class ViewHolder(private val binding: ItemLanguageSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LanguageData) {
            with(binding) {
                selectedLanguageData = item
                presenter = this@LanguageAdapter.presenter
//                item.radioCheck = item.languageName == preferenceManager.getPref(Constants.prefLanguageId,Constants.languageDefault)
                executePendingBindings()
            }
        }
    }
}