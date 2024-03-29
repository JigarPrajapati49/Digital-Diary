package com.rayo.digitaldiary.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rayo.digitaldiary.databinding.ItemCurrencyBinding
import com.rayo.digitaldiary.ui.CurrencyData

/**
 * Created by Mittal Varsani on 02/03/23.
 *
 * @author Mittal Varsani
 */
class CurrencyAdapter(
    private val currencyList: List<CurrencyData>,
    private val updateProfilePresenter: UpdateProfilePresenter
) :
    RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCurrencyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currencyList[position])
    }

    override fun getItemCount(): Int {
        return currencyList.size
    }

    fun getSelectedCurrency(): CurrencyData? {
        var selectedCurrencyData: CurrencyData? = null
        for (item in currencyList) {
            if (item.isSelected) {
                selectedCurrencyData = item
            }
        }
        return selectedCurrencyData
    }

    inner class ViewHolder(private val binding: ItemCurrencyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CurrencyData) {
            with(binding) {
                root.setOnClickListener {
                    for (currencyData in currencyList) {
                        currencyData.isSelected = false
                    }
                    this@CurrencyAdapter.updateProfilePresenter.setLastSelectedCurrency(currencyList[bindingAdapterPosition].countryName)
                    currencyList[bindingAdapterPosition].isSelected = true
                    notifyDataSetChanged()
                }
                currencyData = item
                currencySymbol = item.symbol
                executePendingBindings()
            }
        }
    }
}