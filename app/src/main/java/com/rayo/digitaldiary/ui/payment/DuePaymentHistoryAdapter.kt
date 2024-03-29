package com.rayo.digitaldiary.ui.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rayo.digitaldiary.databinding.ItemDuePaymentHistoryBinding

/**
 * Created by Mittal Varsani on 02/03/23.
 *
 * @author Mittal Varsani
 */
class DuePaymentHistoryAdapter(
    private val duePaymentList: List<DuePaymentData>,
    private val currencySymbol: String
) :
    RecyclerView.Adapter<DuePaymentHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemDuePaymentHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(duePaymentList[position])
    }

    override fun getItemCount(): Int {
        return duePaymentList.size
    }

    inner class ViewHolder(private val binding: ItemDuePaymentHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DuePaymentData) {
            with(binding) {
                duePaymentData = item
                currencySymbol = this@DuePaymentHistoryAdapter.currencySymbol
            }
        }
    }
}