package com.rayo.digitaldiary.ui.history

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.database.CustomerWithHistory
import com.rayo.digitaldiary.databinding.FragmentOwnerOrStaffHistoryDetailBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.OwnerMainActivity
import com.rayo.digitaldiary.ui.customer.CustomerData
import com.rayo.digitaldiary.ui.order.CancelReasonDialog
import com.rayo.digitaldiary.ui.order.OrderData
import com.rayo.digitaldiary.ui.product.Product
import com.rayo.digitaldiary.ui.staff.StaffData
import com.rayo.digitaldiary.ui.staff.StaffMainActivity
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONException


/**
 * Created by Mittal Varsani on 12/06/23.
 *
 * @author Mittal Varsani
 */
@AndroidEntryPoint
class OwnerOrStaffHistoryDetailFragment :
    BaseFragment<FragmentOwnerOrStaffHistoryDetailBinding, HistoryDetailViewModel>(),
    CancelOrderPresenter {

    private val productList: MutableList<Product> = ArrayList()

    override val viewModel: HistoryDetailViewModel by viewModels()

    override fun getFragmentId(): Int {
        return R.layout.fragment_owner_or_staff_history_detail
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProductAdapter()
        binding.cancelOrderPresenter = this
        binding.currencySymbol = preferenceManager.getPref(Constants.prefCurrencySymbol)
        binding.businessOwnerName = preferenceManager.getPref(Constants.prefBusinessName, "")

        val isFromNotification = this.arguments?.getBoolean(Constants.isFromNotification, false)
        if (isFromNotification == true) {
            binding.isFromNotification = true
            val orderData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                this.arguments?.getParcelable(Constants.orderData, OrderData::class.java)
            } else {
                this.arguments?.getParcelable(Constants.orderData)
            }

            val customerData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                this.arguments?.getParcelable(Constants.customerData, CustomerData::class.java)
            } else {
                this.arguments?.getParcelable(Constants.customerData)
            }

            val createdByStaffData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                this.arguments?.getParcelable(Constants.createdByStaffData, StaffData::class.java)
            } else {
                this.arguments?.getParcelable(Constants.createdByStaffData)
            }

            val cancelByStaffData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                this.arguments?.getParcelable(Constants.cancelledByStaffData, StaffData::class.java)
            } else {
                this.arguments?.getParcelable(Constants.cancelledByStaffData)
            }

            val customerWithHistory =
                CustomerWithHistory(
                    orderData!!,
                    customerData,
                    createdByStaffData,
                    cancelByStaffData
                )
            binding.isCancelVisible = false
            productList.clear()
            productList.addAll(customerWithHistory.orderData.product)
            binding.productAdapter?.notifyDataSetChanged()
            customerWithHistory.orderData.orderAmount = customerWithHistory.orderData.orderAmount.toFloat().toString()
            binding.historyDetailData = customerWithHistory
        } else {
            this.arguments?.let { mBundle ->
                mBundle.getString(Constants.localOrderId, "")
                    ?.let { viewModel.getOrderDetailsFromDB(it) }
                binding.isCancelVisible = preferenceManager.getPref(Constants.prefUserType,"") != Constants.USER_TYPE_CUSTOMER
            }
        }

        binding.isCreateOrderVisible = preferenceManager.getPref(Constants.prefUserType, "") != Constants.USER_TYPE_CUSTOMER
        viewModel.cancelOrderSuccess.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            it?.getContentIfNotHandled()?.let {
                if (preferenceManager.getPref(Constants.prefUserType, "") == Constants.USER_TYPE_STAFF) {
                    it.staffDatas = StaffData()
                    binding.historyDetailData = it // Update the OrderHistory Data For Update UI
                } else {
                    binding.historyDetailData = it // Update the OrderHistory Data For Update UI
                }
                binding.isCancelVisible = false
                if (networkInterceptor.isInternetAvailable()) {
                    when (activity) {
                        is OwnerMainActivity -> {
                            (activity as OwnerMainActivity).callSyncOrderAPI()
                        }

                        is StaffMainActivity -> {
                            (activity as StaffMainActivity).callSyncOrderAPI()
                        }
                    }
                }
            }
        }

        viewModel.historyDetailsData.observe(viewLifecycleOwner) {
            it.orderData.orderAmount = it.orderData.orderAmount.toFloat().toString()
            binding.historyDetailData = it

            binding.isCancelVisible = preferenceManager.getPref(
                Constants.prefUserType,
                ""
            ) != Constants.CUSTOMER && it.orderData.cancelled == 0
            productList.clear()
            val jsonArray = JSONArray(it.orderData.products) // Convert Json Array to ArrayList
            try {
                for (i in 0 until jsonArray.length()) {
                    val jsonObj = jsonArray.getJSONObject(i)
                    val productObj = Product()
                    productObj.id = jsonObj.getString("_id")
                    productObj.name = jsonObj.getString("name")
                    productObj.price = jsonObj.getString("price")
                    productObj.quantity = jsonObj.getString("quantity")
                    productObj.unit = jsonObj.getString("unit")
                    productObj.weight = jsonObj.getString("weight")
                    productList.add(productObj)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            binding.productAdapter?.notifyDataSetChanged()
        }
    }

    private fun setProductAdapter() {
        binding.productAdapter = OwnerOrStaffDetailAdapter(
            productList,
            preferenceManager.getPref(Constants.prefCurrencySymbol, "")
        )
    }

    override fun onCancelOrderClick(orderData: CustomerWithHistory) {
        if (Utils.isSingleClick()){
            CancelReasonDialog.show(parentFragmentManager, orderData, this)
        }
    }

    override fun onOkyClick(orderData: CustomerWithHistory) {
        showProgressDialog()
        viewModel.cancelOrder(orderData)
    }
}