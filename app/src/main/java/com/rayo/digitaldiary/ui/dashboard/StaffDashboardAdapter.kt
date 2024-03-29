package com.rayo.digitaldiary.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rayo.digitaldiary.databinding.ItemStaffDashboardBinding

class StaffDashboardAdapter(
     val list: MutableList<DashboardModel>,
     val presenter: StaffDashboardPresenter
) : RecyclerView.Adapter<StaffDashboardAdapter.DashBoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashBoardViewHolder {
        val binding: ItemStaffDashboardBinding =
            ItemStaffDashboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DashBoardViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DashBoardViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class DashBoardViewHolder(private val binding: ItemStaffDashboardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DashboardModel) {
            with(binding) {
                this.data = data
                staffDashboardPresenter = this@StaffDashboardAdapter.presenter
                executePendingBindings()
            }
        }
    }
}