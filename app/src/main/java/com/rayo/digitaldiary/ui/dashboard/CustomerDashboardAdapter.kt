package com.rayo.digitaldiary.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rayo.digitaldiary.databinding.ItemCustomerDashboardBinding

class CustomerDashboardAdapter(
    val list: MutableList<DashboardModel>,
    val presenter: CustomerDashboardPresenter
) : RecyclerView.Adapter<CustomerDashboardAdapter.DashBoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashBoardViewHolder {
        val binding: ItemCustomerDashboardBinding =
            ItemCustomerDashboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DashBoardViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DashBoardViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class DashBoardViewHolder(private val binding: ItemCustomerDashboardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DashboardModel) {
            with(binding) {
                this.data = data
                customerDashboardPresenter = this@CustomerDashboardAdapter.presenter
                executePendingBindings()
            }
        }
    }
}