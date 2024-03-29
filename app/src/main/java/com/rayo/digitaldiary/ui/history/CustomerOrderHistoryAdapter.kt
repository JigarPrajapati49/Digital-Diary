package com.rayo.digitaldiary.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rayo.digitaldiary.database.CustomerWithHistory
import com.rayo.digitaldiary.databinding.ItemCustomerHistoryBinding


class CustomerOrderHistoryAdapter(
    private val orderList: List<CustomerWithHistory>,
    private val customerHistoryPresenter: CustomerHistoryPresenter,
    private val currencySymbol: String
) :
    RecyclerView.Adapter<CustomerOrderHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCustomerHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(orderList[position])
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    inner class ViewHolder(private val binding: ItemCustomerHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CustomerWithHistory) {
            with(binding) {
                item.orderData.orderAmount = item.orderData.orderAmount.toFloat().toString()
                customerWithHistory = item
                historyPresenter = this@CustomerOrderHistoryAdapter.customerHistoryPresenter
                currencySymbol = this@CustomerOrderHistoryAdapter.currencySymbol
                position = bindingAdapterPosition
                executePendingBindings()
            }
        }
    }
}