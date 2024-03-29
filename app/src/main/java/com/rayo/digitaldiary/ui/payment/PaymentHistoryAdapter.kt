package com.rayo.digitaldiary.ui.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rayo.digitaldiary.database.StaffWithPayment
import com.rayo.digitaldiary.databinding.ItemCustomerPaymentHistoryBinding
import com.rayo.digitaldiary.helper.Constants

/**
 * Created by Mittal Varsani on 02/03/23.
 *
 * @author Mittal Varsani
 */
class PaymentHistoryAdapter(
    private val transactionList: List<StaffWithPayment>,
    private val currencySymbol: String,
    private val businessName: String,
    private val historyType : String?
) :
    RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCustomerPaymentHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(transactionList[position])
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    inner class ViewHolder(private val binding: ItemCustomerPaymentHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StaffWithPayment) {
            with(binding) {
                item.paymentData.amount = item.paymentData.amount.toFloat().toString()
                paymentData = item
                businessName = this@PaymentHistoryAdapter.businessName
                currencySymbol = this@PaymentHistoryAdapter.currencySymbol
                when(historyType) {
                    Constants.STAFF -> {
                        isStaffVisible = false
                        isCustomerVisible = true
                    }

                    Constants.CUSTOMER -> {
                        isCustomerVisible = false
                        isStaffVisible = true
                    }

                    else -> {
                        isStaffVisible = true
                        isCustomerVisible = true
                    }
                }
            }
        }
    }
}