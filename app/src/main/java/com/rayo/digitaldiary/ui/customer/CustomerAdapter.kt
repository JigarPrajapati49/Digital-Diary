package com.rayo.digitaldiary.ui.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rayo.digitaldiary.databinding.ItemCustomerBinding

/**
 * Created by Mittal Varsani on 02/03/23.
 *
 * @author Mittal Varsani
 */
class CustomerAdapter(
    private val customerList: List<CustomerData>,
    private val customerPresenter: CustomerPresenter,
    private val countryCode: String,
    private val currencySymbol: String,
    private val isShowDueSection: Boolean
) :
    RecyclerView.Adapter<CustomerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(customerList[position])
    }

    override fun getItemCount(): Int {
        return customerList.size
    }

    inner class ViewHolder(private val binding: ItemCustomerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CustomerData) {
            with(binding) {
                currencySymbol = this@CustomerAdapter.currencySymbol
                countryCode = this@CustomerAdapter.countryCode
                customerData = item
                customerPresenter = this@CustomerAdapter.customerPresenter
                isShowDueSection = this@CustomerAdapter.isShowDueSection
                executePendingBindings()
            }
        }
    }
}