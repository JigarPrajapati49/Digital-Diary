package com.rayo.digitaldiary.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rayo.digitaldiary.database.CustomerWithHistory
import com.rayo.digitaldiary.databinding.ItemOwnerOrStaffHistoryBinding
import com.rayo.digitaldiary.ui.order.OrderData

class OrderHistoryAdapter(
    private val productList: List<CustomerWithHistory>,
    private var presenter: HistoryPresenter,
    private val currencySymbol: String,
    private val businessName :String
) :
    RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOwnerOrStaffHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class ViewHolder(private val binding: ItemOwnerOrStaffHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CustomerWithHistory) {
            with(binding) {
                item.orderData.orderAmount = item.orderData.orderAmount.toFloat().toString()
                customerWithHistory = item
                businessOwnerName = businessName
                historyPresenter = this@OrderHistoryAdapter.presenter
                binding.currencySymbol = this@OrderHistoryAdapter.currencySymbol
            }
        }

    }
}