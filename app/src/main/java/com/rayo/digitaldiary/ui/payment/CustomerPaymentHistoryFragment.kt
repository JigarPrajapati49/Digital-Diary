package com.rayo.digitaldiary.ui.payment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.database.StaffWithPayment
import com.rayo.digitaldiary.databinding.FragmentCustomerPaymentHistoryBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.customer.CustomerData
import dagger.hilt.android.AndroidEntryPoint
import okio.Utf8


@AndroidEntryPoint
class CustomerPaymentHistoryFragment :
    BaseFragment<FragmentCustomerPaymentHistoryBinding, PaymentHistoryViewModel>(),
    PaymentHistoryPresenter {

    private val paymentDisplayList: MutableList<StaffWithPayment> = ArrayList()

    override fun getFragmentId(): Int {
        return R.layout.fragment_customer_payment_history
    }

    override val viewModel: PaymentHistoryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.historyType =
            this.arguments?.getString(Constants.historyType, "") ?: Constants.ALL
        viewModel.getAllCustomerFromDatabase()
        initFilterMenu()
        setVisibility()
        binding.paymentPresenter = this
        this.arguments?.let {
            showProgressDialog()
            viewModel.getTransactionData(viewModel.historyType, it.getString(Constants.customerId, ""),
                it.getString(Constants.staffId, ""))
        }

        // get the Details from AddPaymentFragment Screen (this Will call only when back From AddPaymentScreen )
        setFragmentResultListener(Constants.requestPaymentId) { requestKey, bundle ->
            if(paymentDisplayList.isNotEmpty()) {
                val staffWithPaymentArrayList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bundle.getParcelableArrayList(Constants.localPaymentId, StaffWithPayment::class.java)
                } else {
                    bundle.getSerializable(Constants.localPaymentId) as ArrayList<StaffWithPayment>
                }
                staffWithPaymentArrayList?.let { paymentDisplayList.addAll(0, it) }
                binding.paymentAdapter?.notifyDataSetChanged()
            }
        }

        setPaymentAdapter()
        binding.rvHistory.setOnScrollChangeListener { itemView, i, i2, i3, i4 ->
            if (!viewModel.isFilterApplied
                && (binding.rvHistory.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() == (paymentDisplayList.size - 1)
            ) {
                setPagingData()
            }
        }

        viewModel.paymentList.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            binding.errorMessage = if (it.isEmpty()) {
                context?.getString(R.string.no_data_found)
            } else {
                ""
            }
            if (viewModel.isFilterApplied) {
                paymentDisplayList.clear()
                paymentDisplayList.addAll(viewModel.filterHistory())
                binding.paymentAdapter?.notifyDataSetChanged()
            } else {
                setPagingData()
            }
        }
    }

    private fun setVisibility() {
        when (viewModel.historyType) {
            Constants.STAFF -> {
                binding.isAddButtonVisible = preferenceManager.getPref(Constants.prefUserType, "") != Constants.CUSTOMER
                binding.isStaffVisible = false
                binding.isCustomerVisible = true
                binding.isCustomer = false
            }

            Constants.CUSTOMER -> {
                binding.isAddButtonVisible = preferenceManager.getPref(Constants.prefUserType,"") != Constants.CUSTOMER
                binding.isCustomer = true
                binding.isCustomerVisible = false
                binding.isStaffVisible = true
            }

            else -> {
                binding.isAddButtonVisible = preferenceManager.getPref(Constants.prefUserType,"") != Constants.CUSTOMER
                binding.isCustomer = false
                binding.isStaffVisible = true
                binding.isCustomerVisible = true
            }
        }
    }

    private fun setPaymentAdapter() {
        binding.paymentAdapter = PaymentHistoryAdapter(
            paymentDisplayList, preferenceManager.getPref(Constants.prefCurrencySymbol, ""),
            preferenceManager.getPref(Constants.prefBusinessName, ""),
            viewModel.historyType
        )
    }

    private fun setPagingData() {
        if (viewModel.currentPage == viewModel.totalPage) {
            return
        }
        binding.isRecyclerVisible = paymentDisplayList.isNotEmpty()
        binding.isProgressVisible = true
        Handler(Looper.getMainLooper()).postDelayed({
            paymentDisplayList.addAll(viewModel.getCurrentPageData())
            binding.errorMessage = if (paymentDisplayList.isEmpty()) {
                context?.getString(R.string.no_data_found)
            } else {
                ""
            }
            binding.paymentAdapter?.notifyItemRangeChanged(viewModel.startPage, viewModel.endPage)
            binding.isProgressVisible = false
            binding.isRecyclerVisible = paymentDisplayList.isNotEmpty()
        }, 500)
    }

    override fun onAddPaymentClick() {
        findNavController().navigate(R.id.addPaymentFragment, this.arguments)
    }

    override fun onFilterApplyClick(startDate: Long, endDate: Long, customerData: CustomerData) {
        viewModel.isFilterApplied =
            (startDate != 0L || endDate != 0L || customerData.id.isNotEmpty())
        viewModel.filterSelectedCustomer = customerData
        viewModel.filterStartDate = startDate
        viewModel.filterEndDate = endDate
        if (viewModel.isFilterApplied) {
            paymentDisplayList.clear()
            paymentDisplayList.addAll(viewModel.filterHistory())
            binding.errorMessage = if (paymentDisplayList.isEmpty()) {
                context?.getString(R.string.no_data_found)
            } else {
                ""
            }
            binding.paymentAdapter?.notifyDataSetChanged()
        } else {
            paymentDisplayList.clear()
            viewModel.resetPagingData()
            setPagingData()
        }
    }

    private fun initFilterMenu() {
        /* Don't show filter in staff or customer detail screen */
        if (this.arguments?.getString(Constants.customerId, "") != Constants.ALL) {
            return
        }
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.history_filter_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menuFilter -> {
                        viewModel.customerList.value?.let {
                            viewModel.customerList.value?.let {
                                val customerList: MutableList<CustomerData> = ArrayList()
                                if (it.isNotEmpty()) {
                                    CustomerData().apply {
                                        name = context?.getString(R.string.all) ?: ""
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
                                    PaymentFilterDialogFragment.show(
                                        childFragmentManager,
                                        viewModel.filterStartDate,
                                        viewModel.filterEndDate,
                                        customerList, this@CustomerPaymentHistoryFragment
                                    )
                                }
                            }
                        }
                        return true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}