package com.rayo.digitaldiary.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rayo.digitaldiary.databinding.ItemDashboardBinding

class DashboardAdapter(
    private val list: MutableList<DashboardModel>,
    private val presenter: DashboardPresenter
) : RecyclerView.Adapter<DashboardAdapter.DashBoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashBoardViewHolder {
        val binding: ItemDashboardBinding =
            ItemDashboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DashBoardViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DashBoardViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class DashBoardViewHolder(private val binding: ItemDashboardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DashboardModel) {
            with(binding) {
                this.data = data
                dashBoardPresenter = this@DashboardAdapter.presenter
                executePendingBindings()
            }
        }
    }
}