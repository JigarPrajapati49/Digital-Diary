package com.rayo.digitaldiary.ui.report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rayo.digitaldiary.databinding.ItemDueReportBinding
import com.rayo.digitaldiary.ui.customer.CustomerData

/**
 * Created by Mittal Varsani on 02/03/23.
 *
 * @author Mittal Varsani
 */
class DueReportAdapter(
    private val customerList: List<CustomerData>,
    private val dueReportPresenter: DueReportPresenter,
    private val countryCode: String,
    private val currencySymbol: String
) :
    RecyclerView.Adapter<DueReportAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemDueReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(customerList[position])
    }

    override fun getItemCount(): Int {
        return customerList.size
    }

    inner class ViewHolder(private val binding: ItemDueReportBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CustomerData) {
            with(binding) {
                currencySymbol = this@DueReportAdapter.currencySymbol
                dueReportPresenter = this@DueReportAdapter.dueReportPresenter
                countryCode = this@DueReportAdapter.countryCode
                customerData = item
            }
        }

    }
}