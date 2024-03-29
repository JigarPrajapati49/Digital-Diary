package com.rayo.digitaldiary.ui.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rayo.digitaldiary.databinding.ItemCustomerHomeBinding
import com.rayo.digitaldiary.ui.login.ScanQRData

/**
 * Created by Mittal Varsani on 02/03/23.
 *
 * @author Mittal Varsani
 */
class CustomerDetailsAdapter(
    private val productList: List<ScanQRData>,
    private val presenter: CustomerBusinessPresenter
) :
    RecyclerView.Adapter<CustomerDetailsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCustomerHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class ViewHolder(private val binding: ItemCustomerHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(scanQRData: ScanQRData) {
            with(binding) {
                customerDetailsData = scanQRData
                customerBusinessPresenter = presenter
                executePendingBindings()
            }
        }
    }
}