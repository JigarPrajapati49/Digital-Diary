package com.rayo.digitaldiary.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rayo.digitaldiary.databinding.ItemSelectCustomerBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.ui.customer.CustomerData


/**
 * Created by Mittal Varsani on 02/03/23.
 *
 * @author Mittal Varsani
 */
class SelectCustomerAdapter(
    private val customerList: List<CustomerData>,
    private val countryCode: String,
    private val currencySymbol: String,
    private val isShowDueAmount: Boolean = false,
    private val presenter: SelectCustomerPresenter
) : RecyclerView.Adapter<SelectCustomerAdapter.ViewHolder>() {

    var lastCustomerSelectedId = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSelectCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(customerList[position])
    }

    override fun getItemCount(): Int {
        return customerList.size
    }

    fun getSelectedCustomer(): CustomerData? {
        var selectedCustomerData: CustomerData? = null
        for (item in customerList) {
            if (item.isCustomerSelected == 1) {
                selectedCustomerData = item
            }
        }
        return selectedCustomerData
    }

    fun setAllSelected() {
        for (item in customerList) {
            if (item.name.lowercase() == Constants.ALL && item.id == "") {
                item.isCustomerSelected = 1
            } else {
                item.isCustomerSelected = 0
            }
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemSelectCustomerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(mCustomerData: CustomerData) {
            with(binding) {
                countryCode = this@SelectCustomerAdapter.countryCode
                currencySymbol = this@SelectCustomerAdapter.currencySymbol
                isShowDueAmount = this@SelectCustomerAdapter.isShowDueAmount
                presenter = this@SelectCustomerAdapter.presenter
                if (mCustomerData.isCustomerSelected == 1) {
                    lastCustomerSelectedId = mCustomerData.id
                }
                customerData = mCustomerData
                executePendingBindings()
            }
        }
    }
}