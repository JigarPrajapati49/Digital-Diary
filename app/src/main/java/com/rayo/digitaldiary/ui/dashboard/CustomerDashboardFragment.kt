package com.rayo.digitaldiary.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.databinding.FragmentCustomerDashboardBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.ui.customer.CustomerMainActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CustomerDashboardFragment :
    BaseFragment<FragmentCustomerDashboardBinding, CustomerDashboardViewModel>(),
    CustomerDashboardPresenter, CustomerMainActivity.SwitchBusinessPresenter {

    override val viewModel: CustomerDashboardViewModel by viewModels()
    private val dashboardList: MutableList<DashboardModel> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as CustomerMainActivity).customerDashboardPresenter = this
        (requireActivity() as CustomerMainActivity).setSwitchPresenter(this)

        context?.let {
            dashboardList.clear()
            dashboardList.addAll(viewModel.getDashBoardData(it))
        }

        binding.customerDashBoardAdapter = CustomerDashboardAdapter(dashboardList, this)
    }

    override fun getFragmentId(): Int {
        return R.layout.fragment_customer_dashboard
    }

    override fun onItemClicked(dashBoardData: DashboardModel) {
        when (dashBoardData.id) {
            Constants.DASHBOARD_ORDER_HISTORY -> {
                Bundle().let {
                    it.putString(
                        Constants.customerId,
                        preferenceManager.getPref(Constants.prefUserId)
                    )
                    findNavController().navigate(R.id.customerHistoryFragment, it)
                }
            }
            Constants.DASHBOARD_OWNER_DETAIL -> {
                findNavController().navigate(R.id.ownerProfileFragment)
            }
            Constants.DASHBOARD_ADD_PAYMENT -> {
                Bundle().let {
                    it.putString(Constants.historyType, Constants.CUSTOMER)
                    it.putString(
                        Constants.customerId,
                        preferenceManager.getPref(Constants.prefUserId)
                    )
                    findNavController().navigate(R.id.paymentHistoryFragment, it)
                }
            }
        }
    }

    override fun switchCustomer() {
        context?.let {
            dashboardList.clear()
            dashboardList.addAll(viewModel.getDashBoardData(it))
        }
        binding.customerDashBoardAdapter?.notifyDataSetChanged()

    }
}