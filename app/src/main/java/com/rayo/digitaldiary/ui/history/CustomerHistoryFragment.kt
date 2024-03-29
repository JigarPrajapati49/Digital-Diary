package com.rayo.digitaldiary.ui.history

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.database.CustomerWithHistory
import com.rayo.digitaldiary.databinding.FragmentCustomerHistoryBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Mittal Varsani on 30/05/23.
 *
 * @author Mittal Varsani
 */
@AndroidEntryPoint
class CustomerHistoryFragment :
    BaseFragment<FragmentCustomerHistoryBinding, CustomerHistoryViewModel>(),
    CustomerHistoryPresenter, PopupMenu.OnMenuItemClickListener {

    override val viewModel: CustomerHistoryViewModel by viewModels()
    private val customerWithHistoryList: ArrayList<CustomerWithHistory> = ArrayList()

    override fun getFragmentId(): Int {
        return R.layout.fragment_customer_history
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFilterMenu()
        setMonthText()
        binding.currencySymbol = preferenceManager.getPref(Constants.prefCurrencySymbol, "")
        binding.historyPresenter = this
        preferenceManager.getPref(Constants.prefCurrencySymbol, "")
        viewModel.customerId = this.arguments?.getString(Constants.customerId) ?: ""

        val date = viewModel.getMonthText()
        // format we receive
        val format = SimpleDateFormat(Constants.monthYearFormat, Locale.ENGLISH).parse(date)

        // convert format this format from (year)this format
        val year = SimpleDateFormat(Constants.onlyYearFormat, Locale.ENGLISH).format(format!!)
        val month = SimpleDateFormat("MM", Locale.ENGLISH).format(format)

        Log.e("TAG", "Today Month--$month----------Year-----$year")

        if (!viewModel.isPaymentDataFetched) {
            Coroutines.ioThenMain({
                viewModel.getAllPayment(month, year)
            }, {
                setCustomerOrderHistoryAdapter()
            })
        } else {
            viewModel.getCustomerOrderHistoryFromDB(month, year)
            setCustomerOrderHistoryAdapter()
        }

        viewModel.orderHistoryList.observe(viewLifecycleOwner) {
            // if list is empty from database show no data fount
            binding.isNoOrderHistoryFound = it.isEmpty()
            customerWithHistoryList.clear()
            customerWithHistoryList.addAll(it)
            setHistorySummary(customerWithHistoryList)
            try {
                if (viewModel.scrollPosition > -1) {
                    binding.customerHistoryAdapter?.notifyItemChanged(viewModel.scrollPosition)
                    viewModel.scrollPosition = -1
                } else {
                    binding.customerHistoryAdapter?.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e("CustomerHistoryFragment", "---${e.message} ")
            }
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
                        if (Utils.isSingleClick()){
                            val popUpMenu = PopupMenu(context, activity?.findViewById(R.id.menuFilter))
                            val menuInflater = popUpMenu.menuInflater
                            menuInflater.inflate(R.menu.customer_history_filter_menu, popUpMenu.menu)
                            val filterAll = popUpMenu.menu.findItem(R.id.filterAll)
                            val filterCompleted = popUpMenu.menu.findItem(R.id.filterCompleted)
                            val filterCancelled = popUpMenu.menu.findItem(R.id.filterCancelled)
                            filterAll.title = Utils.getTranslatedValue(getString(R.string.all))
                            filterCompleted.title =
                                Utils.getTranslatedValue(getString(R.string.completed))
                            filterCancelled.title =
                                Utils.getTranslatedValue(getString(R.string.cancelled))
                            when (viewModel.filterOrderStatus) {
                                Constants.ALL -> filterAll.isChecked = true
                                Constants.COMPLETED -> filterCompleted.isChecked = true
                                Constants.CANCELLED -> filterCancelled.isChecked = true
                            }
                            popUpMenu.setOnMenuItemClickListener(this@CustomerHistoryFragment)
                            popUpMenu.menu.setGroupCheckable(R.id.filterAll, true, false)
                            popUpMenu.show()
                        }
                        return true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
        if (Utils.isSingleClick()){
            viewModel.filterOrderStatus = when (menuItem?.itemId) {
                R.id.filterCompleted -> {
                    Log.e(
                        "TAG",
                        "onMenuItemClick: ----------------${viewModel.filterOrderStatus}"
                    )
                    viewModel.filterHistory(
                        viewModel.getOrderStatusInInt(Constants.COMPLETED),
                        viewModel.customerId
                    )
                    Constants.COMPLETED
                }

                R.id.filterCancelled -> {
                    Log.e(
                        "TAG",
                        "onMenuItemClick: ----------------${viewModel.filterOrderStatus}"
                    )
                    viewModel.filterHistory(
                        viewModel.getOrderStatusInInt(Constants.CANCELLED),
                        viewModel.customerId
                    )
                    Constants.CANCELLED
                }

                else -> {
                    viewModel.filterHistory(
                        viewModel.getOrderStatusInInt(Constants.ALL),
                        viewModel.customerId
                    )
                    Constants.ALL
                }
            }
        }
        return true
    }

    override fun onHistoryItemClick(customerWithHistory: CustomerWithHistory, position: Int) {
        Bundle().let {
            it.putString(Constants.localOrderId, customerWithHistory.orderData.localOrderId)
            viewModel.scrollPosition = position
            findNavController().navigate(R.id.ownerOrStaffOrderHistoryDetailFragment, it)
        }
    }

    override fun onNextMonthClick() {
        binding.isImageNextEnable = viewModel.nextMonthClick()

        setMonthText()

        val currentMonth =
            Utils.getStringFromDate("MMMM", Date()) + " " + Utils.getStringFromDate("yyyy", Date())

        val date = viewModel.getMonthText()
        val parsedDateObj =
            SimpleDateFormat(Constants.monthYearFormat, Locale.ENGLISH).parse(date)

        val year =
            SimpleDateFormat(Constants.onlyYearFormat, Locale.ENGLISH).format(parsedDateObj!!)
        val month = SimpleDateFormat("MM", Locale.ENGLISH).format(parsedDateObj)

        Log.e("TAG", "onNextMonthClick: -Month--$month----------Year-----$year")

        viewModel.getCustomerOrderHistoryFromDB(month, year)
        if (binding.tvMonthName.text.toString() == currentMonth) {
            binding.isImageNextEnable = false
        }
    }

    override fun onPreviousMonthClick() {
        binding.isImageNextEnable = viewModel.previousMonthClick()
        setMonthText()
        val date = viewModel.getMonthText()
        val parsedDateObj =
            SimpleDateFormat(Constants.monthYearFormat, Locale.ENGLISH).parse(date)

        val year =
            SimpleDateFormat(Constants.onlyYearFormat, Locale.ENGLISH).format(parsedDateObj!!)
        val month = SimpleDateFormat("MM", Locale.ENGLISH).format(parsedDateObj)
        Log.e("TAG", "onNextMonthClick: -Month--$month----------Year-----$year")

        viewModel.getCustomerOrderHistoryFromDB(month, year)
    }

    private fun setMonthText() {
        binding.tvMonthName.text = viewModel.getMonthText()
    }

    private fun setCustomerOrderHistoryAdapter() {
        binding.customerHistoryAdapter = CustomerOrderHistoryAdapter(
            customerWithHistoryList,
            this,
            preferenceManager.getPref(Constants.prefCurrencySymbol, "")
        )
    }

    private fun setHistorySummary(historyList: List<CustomerWithHistory>) {
        var totalAmount = 0f
        var totalOrder = 0
        for (item in historyList) {
            if (item.orderData.cancelled == 0) {
                totalAmount += item.orderData.orderAmount.toFloat()
                totalOrder++
            }
        }
        CustomerHistorySummaryData().let {
            it.totalOrders = totalOrder.toString()
            it.totalAmount = preferenceManager.getPref(Constants.prefCurrencySymbol, "")+ totalAmount
            it.totalDueAmount = viewModel.totalDueAmount
            binding.summaryData = it
        }
    }
}