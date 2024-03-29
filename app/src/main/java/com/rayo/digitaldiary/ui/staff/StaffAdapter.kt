package com.rayo.digitaldiary.ui.staff

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rayo.digitaldiary.databinding.ItemStaffBinding

/**
 * Created by Mittal Varsani on 02/03/23.
 *
 * @author Mittal Varsani
 */
class StaffAdapter(
    private val productList: List<StaffData>,
    private val staffPresenter: StaffPresenter,
    private val countryCode: String
) :
    RecyclerView.Adapter<StaffAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStaffBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class ViewHolder(private val binding: ItemStaffBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StaffData) {
            with(binding) {
                countryCode = this@StaffAdapter.countryCode
                staffData = item
                staffPresenter = this@StaffAdapter.staffPresenter
            }
        }

    }
}