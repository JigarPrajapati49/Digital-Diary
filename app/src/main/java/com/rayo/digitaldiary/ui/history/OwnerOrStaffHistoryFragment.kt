package com.rayo.digitaldiary.ui.history

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.database.CustomerWithHistory
import com.rayo.digitaldiary.databinding.FragmentOwnerOrStaffHistoryBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.KeyboardUtils
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.customer.CustomerData
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Mittal Varsani on 30/05/23.
 *
 * @author Mittal Varsani
 */
@AndroidEntryPoint
class OwnerOrStaffHistoryFragment :
    BaseFragment<FragmentOwnerOrStaffHistoryBinding, OwnerOrStaffHistoryViewModel>(),
    HistoryPresenter {

    override val viewModel: OwnerOrStaffHistoryViewModel by viewModels()

    private val orderHistoryList: MutableList<CustomerWithHistory> = ArrayList()

    override fun getFragmentId(): Int {
        return R.layout.fragment_owner_or_staff_history
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFilterMenu()
        setDateText()
        viewModel.initStartEndDate()
        binding.historyPresenter = this
        viewModel.createdById = this.arguments?.getString(Constants.createdById) ?: ""
        viewModel.staffCreatedById = this.arguments?.getString(Constants.createdById) ?: ""

        viewModel.getOrderHistory()
        setOrderHistoryAdapter()

        viewModel.orderHistoryList.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {historyList ->
                setHistorySummary(historyList)
                // if list is empty from database show no data fount
                binding.isNoOrderHistoryFound = historyList.isEmpty()
                orderHistoryList.clear()
                orderHistoryList.addAll(historyList)
                binding.orderHistoryAdapter?.notifyDataSetChanged()
            }
        }
    }

    private fun setHistorySummary(historyList: List<CustomerWithHistory>) {
        var totalAmount = 0f
        var totalEarned = 0f
        for(item in historyList) {
            totalAmount+= item.orderData.orderAmount.toFloat()
            if(item.orderData.cancelled == 0) {
                totalEarned += item.orderData.orderAmount.toFloat()
            }
        }
        HistorySummaryData().let {
            it.totalOrders = historyList.size.toString()
            it.totalAmount = preferenceManager.getPref(Constants.prefCurrencySymbol, "")+ totalAmount
            it.totalEarned = preferenceManager.getPref(Constants.prefCurrencySymbol, "")+ totalEarned
            binding.summaryData = it
        }
    }

    private fun initFilterMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.history_filter_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menuFilter -> {
                        viewModel.customerList.value?.let {
                            val customerList: MutableList<CustomerData> = ArrayList()
                            if (it.isNotEmpty()) {
                                CustomerData().apply {
                                    name = context?.getString(R.string.all)?:""
                                    isCustomerSelected =
                                        if (viewModel.filterSelectedCustomer.id.isEmpty()) 1 else 0
                                    customerList.add(0, this)
                                }
                            }
                            customerList.addAll(it)
                            viewModel.customerList.value.let {
                                if (it != null) {
                                    for (item in it) {
                                        item.isCustomerSelected = 0
                                        if (item.id == viewModel.filterSelectedCustomer.id) {
                                            item.isCustomerSelected = 1
                                        }
                                    }
                                }
                            }
                            if (Utils.isSingleClick()){
                                HistoryFilterDialogFragment.show(
                                    parentFragmentManager,
                                    customerList,
                                    viewModel.filterOrderStatus,
                                    viewModel.filterDate,
                                    this@OwnerOrStaffHistoryFragment
                                )
                            }
                        }
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDateClick() {
        if (Utils.isSingleClick()){
            val datePicker =
                MaterialDatePicker.Builder.datePicker().setSelection(viewModel.calendar.timeInMillis)
                    .build()
            datePicker.show(childFragmentManager, "DatePicker")
            datePicker.addOnPositiveButtonClickListener {
                viewModel.calendar.timeInMillis = it
                viewModel.initStartEndDate()
                viewModel.getOrderHistory()
                setDateText()
            }
        }
    }

    override fun onFilterApplyClick(
        paymentStatus: String,
        selectedCustomer: CustomerData
    ) {
        val orderStatus = viewModel.getOrderStatusInInt(paymentStatus)
        viewModel.filterOrderStatus = paymentStatus
        viewModel.filterSelectedCustomer = selectedCustomer
        viewModel.filterHistory(orderStatus, selectedCustomer)
    }

    override fun onNextDateClick() {
        viewModel.nextDayClick()
        viewModel.getOrderHistory()
        setDateText()
    }

    override fun onPreviousDateClick() {
        viewModel.previousDayClick()
        viewModel.getOrderHistory()
        setDateText()
    }

    private fun setDateText() {
        binding.tvMonthName.text = viewModel.getDayText()
    }

    private fun setOrderHistoryAdapter() {
        binding.orderHistoryAdapter = OrderHistoryAdapter(
            orderHistoryList,
            this,
            preferenceManager.getPref(Constants.prefCurrencySymbol, ""),
            preferenceManager.getPref(Constants.prefBusinessName, "")
        )
    }

    override fun onHistoryItemClick(customerWithHistory: CustomerWithHistory) {
        Bundle().let {
            it.putString(Constants.localOrderId, customerWithHistory.orderData.localOrderId)
            findNavController().navigate(R.id.ownerOrStaffOrderHistoryDetailFragment, it)
        }
    }
}