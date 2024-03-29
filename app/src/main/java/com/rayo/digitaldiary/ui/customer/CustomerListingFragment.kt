package com.rayo.digitaldiary.ui.customer

import android.os.Bundle
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
import com.rayo.digitaldiary.baseClasses.CallMessageBottomSheetDialog
import com.rayo.digitaldiary.databinding.FragmentCustomerListingBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomerListingFragment : BaseFragment<FragmentCustomerListingBinding, CustomerViewModel>(),
    CustomerPresenter, PopupMenu.OnMenuItemClickListener {

    private val allCustomerListing: MutableList<CustomerData> = ArrayList()
    private val displayCustomerListing: MutableList<CustomerData> = ArrayList()

    override val viewModel: CustomerViewModel by viewModels()

    override fun getFragmentId(): Int {
        return R.layout.fragment_customer_listing
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllPayment()
        initFilterMenu()
        binding.customerPresenter = this
        binding.isShowDueSection = preferenceManager.getPref(Constants.prefUserType, "") == Constants.USER_TYPE_OWNER
        showProgressDialog()
        setCustomerListingAdapter()

        viewModel.customerList.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            allCustomerListing.clear()
            allCustomerListing.addAll(it)
            setCustomerData()
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
                        if (Utils.isSingleClick()) {
                            val popUpMenu = PopupMenu(context, activity?.findViewById(R.id.menuFilter))
                            val menuInflater = popUpMenu.menuInflater
                            menuInflater.inflate(R.menu.customer_filter_menu, popUpMenu.menu)
                            val filterCreatedAt = popUpMenu.menu.findItem(R.id.filterCreatedAt)
                            val filterDueAmountFirst = popUpMenu.menu.findItem(R.id.filterDueAmountFirst)
                            val filterDueAmountLast = popUpMenu.menu.findItem(R.id.filterDueAmountLast)
                            val filterAtoZ = popUpMenu.menu.findItem(R.id.filterAtoZ)
                            val filterZtoA = popUpMenu.menu.findItem(R.id.filterZtoA)
                            filterCreatedAt.title = Utils.getTranslatedValue(getString(R.string.by_registered))
                            filterDueAmountFirst.title = Utils.getTranslatedValue(getString(R.string.due_ascending))
                            filterDueAmountLast.title = Utils.getTranslatedValue(getString(R.string.due_descending))
                            filterAtoZ.title = Utils.getTranslatedValue(getString(R.string.a_to_z))
                            filterZtoA.title = Utils.getTranslatedValue(getString(R.string.z_to_a))
                            if (preferenceManager.getPref(Constants.prefUserType, "") == Constants.USER_TYPE_STAFF) {
                                filterDueAmountFirst.setVisible(false)
                                filterDueAmountLast.setVisible(false)
                            }
                            when (viewModel.filterCustomerList) {
                                Constants.CREATED_AT -> filterCreatedAt.isChecked = true
                                Constants.DUE_AMOUNT_FIRST -> filterDueAmountFirst.isChecked = true
                                Constants.DUE_AMOUNT_LAST -> filterDueAmountLast.isChecked = true
                                Constants.A_TO_Z -> filterAtoZ.isChecked = true
                                Constants.Z_TO_A -> filterZtoA.isChecked = true
                            }
                            popUpMenu.setOnMenuItemClickListener(this@CustomerListingFragment)
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

    private fun setCustomerData() {
        when (binding.btnToggle.checkedButtonId) {
            R.id.btnAll -> {
                onAllClick()
            }

            R.id.btnActive -> {
                onActiveClick()
            }

            else -> {
                onInactiveClick()
            }
        }
    }

    private fun setCustomerListingAdapter() {
        binding.customerAdapter = CustomerAdapter(
            displayCustomerListing,
            this,
            preferenceManager.getPref(Constants.prefCountryCode, ""),
            preferenceManager.getPref(Constants.prefCurrencySymbol, ""),
            preferenceManager.getPref(Constants.prefUserType, "") == Constants.USER_TYPE_OWNER
        )
    }

    override fun onAddCustomerClick() {
        if (Utils.isSingleClick()){
            if(networkInterceptor.isInternetAvailable()) {
                AddCustomerDialogFragment.show(parentFragmentManager, this)
            } else {
                context?.let {
                    dialogHelper.showOneButtonDialog(it, Utils.getTranslatedValue(it.getString(R.string.no_internet)))
                }
            }
        }
    }

    override fun onCustomerAddedSuccessfully(customerData: CustomerData) {
        viewModel.customerList.value?.add(0, customerData)
        displayCustomerListing.add(0, customerData)
        allCustomerListing.add(0, customerData)
        viewModel.filterList.add(0, customerData)
        setCustomerData()
    }

    override fun onAllClick() {
        applyCustomerFilter(Constants.STATES_ALL)
    }

    override fun onActiveClick() {
        applyCustomerFilter(Constants.STATES_ACTIVE)
    }

    override fun onInactiveClick() {
        applyCustomerFilter(Constants.STATES_INACTIVE)
    }

    private fun applyCustomerFilter(states: Int) {
        displayCustomerListing.clear()
        if (states == Constants.STATES_ALL) {
            displayCustomerListing.addAll(allCustomerListing)
        } else {
            displayCustomerListing.addAll(allCustomerListing.filter { customerData ->
                customerData.active == states
            })
        }
        binding.errorMessage = if (displayCustomerListing.isEmpty()) {
            Utils.getTranslatedValue(getString(R.string.no_customer))
        } else {
            ""
        }
        binding.customerAdapter?.notifyDataSetChanged()
    }

    override fun onItemClick(customerData: CustomerData) {
        Bundle().let {
            it.putParcelable(Constants.customerDetail, customerData)
            findNavController().navigate(R.id.customerDetailsFragment, it)
        }
    }

    override fun onPhoneNumberClick(phoneNumber: String?) {
        if (Utils.isSingleClick()){
            phoneNumber?.let { CallMessageBottomSheetDialog.display(childFragmentManager, it) }
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
        if (Utils.isSingleClick()) {
            viewModel.filterCustomerList = when (menuItem?.itemId) {
                R.id.filterDueAmountFirst -> {
                    Constants.DUE_AMOUNT_FIRST
                }

                R.id.filterDueAmountLast -> {
                    Constants.DUE_AMOUNT_LAST
                }

                R.id.filterAtoZ -> {
                    Constants.A_TO_Z
                }

                R.id.filterZtoA -> {
                    Constants.Z_TO_A
                }

                else -> {
                    Constants.CREATED_AT
                }
            }
            viewModel.filterCustomer(viewModel.filterCustomerList)
        }
        return true
    }
}