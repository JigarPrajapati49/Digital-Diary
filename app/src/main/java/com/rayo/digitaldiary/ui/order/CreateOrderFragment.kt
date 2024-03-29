package com.rayo.digitaldiary.ui.order

import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.databinding.FragmentCreateOrderBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.OwnerMainActivity
import com.rayo.digitaldiary.ui.customer.CustomerData
import com.rayo.digitaldiary.ui.languageTranslation.LanguageTranslationData
import com.rayo.digitaldiary.ui.product.Product
import com.rayo.digitaldiary.ui.staff.StaffMainActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class CreateOrderFragment : BaseFragment<FragmentCreateOrderBinding, CreateOrderViewModel>(),
    CreateOrderPresenter, SelectCustomerPresenter {

    private val selectedProductList: MutableList<Product> = ArrayList()
    override val viewModel: CreateOrderViewModel by viewModels()
    private val languageList :ArrayList<LanguageTranslationData> = ArrayList()

    override fun getFragmentId(): Int {
        return R.layout.fragment_create_order
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        binding.createOrderPresenter = this
        binding.rvSelectedProducts.isNestedScrollingEnabled = false
        binding.orderAmount = viewModel.totalOrderAmount.toString()
        binding.currencySymbol = preferenceManager.getPref(Constants.prefCurrencySymbol, "")
        binding.etDate.setText(Utils.getCurrentDateTimeForOrder())

        viewModel.selectedDate = Date(System.currentTimeMillis()).time
        val customerData = if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.arguments?.getParcelable(Constants.customerData, CustomerData::class.java)
        } else {
            this.arguments?.getParcelable(Constants.customerData)
        }
        viewModel.selectedCustomerData = customerData
        binding.etCustomer.setText(customerData?.name)

        viewModel.customerList.observe(viewLifecycleOwner) {
            setDataForUpdateOrder()
            setSelectedProductAdapter()
        }

        Coroutines.ioThenMain({
            viewModel.getLanguageTranslation().let {
                languageList.clear()
                languageList.addAll(it)
            }
        }, {
            Log.e("TAG", "onViewCreated: -$it" )
        })

        viewModel.orderInsertedInDB.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { orderData ->
                context?.let { it1 ->
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
                    dismissProgressDialog()
                    dialogHelper.showOneButtonDialog(
                        it1,
                        Utils.getTranslatedValue(getString(R.string.order_created))
                    ) { p0, p1 ->
                        dismissProgressDialog()
                        clearFormData(orderData.localOrderId)
                    }
                }
            }
        }
    }

    private fun clearFormData(localOrderId: String) {
        viewModel.selectedDate = 0
        binding.etDate.text?.clear()
        binding.etNote.text?.clear()
        binding.etCustomer.text?.clear()
        viewModel.selectedCustomerData = null
        selectedProductList.clear()
        binding.selectedProductAdapter?.notifyDataSetChanged()
        viewModel.productList.value?.let {
            for (item in it) {
                item.isProductSelected = 0
                item.quantity = "1"
            }
        }
        binding.isAnySelectedProduct = selectedProductList.isNotEmpty()
        viewModel.totalOrderAmount = 0f
        binding.orderAmount = viewModel.totalOrderAmount.toString()
        with(Bundle()) {
            putString(Constants.localOrderId, localOrderId)
            findNavController().navigate(R.id.ownerOrStaffOrderHistoryDetailFragment, this)
        }
    }

    private fun setDataForUpdateOrder() {
        when (activity) {
            is OwnerMainActivity -> {
                (activity as OwnerMainActivity).setupToolbar(getString(R.string.create_order))
            }

            is StaffMainActivity -> {
                (mActivity as StaffMainActivity).setupToolbar(
                    Utils.getTranslatedValue(getString(R.string.create_order)),
                    ContextCompat.getDrawable(mActivity, R.drawable.ic_back)
                )
            }
        }
    }

    private fun setSelectedProductAdapter() {
        binding.isAnySelectedProduct = selectedProductList.isNotEmpty()
        binding.selectedProductAdapter = SelectedProductAdapter(
            selectedProductList, preferenceManager.getPref(
                Constants.prefCurrencySymbol, ""
            ),
            this,
            languageList
        )
    }

    override fun onSelectDateClick() {
        if (Utils.isSingleClick()){
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText(Utils.getTranslatedValue(getString(R.string.select_date)))
                    .setSelection(if (viewModel.selectedDate > 0) viewModel.selectedDate else Calendar.getInstance().timeInMillis)
                    .build()
            datePicker.show(childFragmentManager, "CreateOrder")
            datePicker.addOnPositiveButtonClickListener {
                viewModel.selectedDate = it
                binding.etDate.setText(Utils.getFormattedDate(viewModel.selectedDate))
            }
        }
    }

    override fun onSelectCustomerClick() {
        if (Utils.isSingleClick()){
            viewModel.customerList.value?.let {
                for (item in it) {
                    item.isCustomerSelected = 0
                    if (item.id == viewModel.selectedCustomerData?.id) {
                        item.isCustomerSelected = 1
                    }
                }
                SelectCustomerDialogFragment.show(
                    childFragmentManager,
                    it as MutableList<CustomerData>,
                    this,
                    false
                )
            }
        }
    }

    override fun onCustomerSelected(customerData: CustomerData) {
        if (Utils.isSingleClick()){
            viewModel.selectedCustomerData = customerData
            binding.etCustomer.setText(customerData.name)
        }
    }

    override fun onSelectProductClick() {
        if (Utils.isSingleClick()) {
            // get allProductFrom DataBase
            val productList = viewModel.productList.value
            val notSelectedProductList: MutableList<Product> = ArrayList()
            for (i in productList!!) {
                if (i.isProductSelected != 1) {
                    notSelectedProductList.add(i)
                }
            }
            // show not selected data in Dialog
            viewModel.productList.value?.let {
                SelectProductDialogFragment.show(
                    childFragmentManager,
                    notSelectedProductList,
                    this
                )
            }
        }
    }

    override fun onProductSelected(mSelectedProductList: List<Product>) {
        selectedProductList.addAll(mSelectedProductList)
        binding.selectedProductAdapter?.notifyDataSetChanged()
        binding.isAnySelectedProduct = selectedProductList.isNotEmpty()
        viewModel.totalOrderAmount = 0f
        for (item in mSelectedProductList) {
            viewModel.totalOrderAmount += (item.quantity.ifEmpty { "0" }
                .toInt() * item.price.toFloat())
        }
        binding.orderAmount = viewModel.totalOrderAmount.toString()
    }

    override fun onProductQuantityUpdate(productList: List<Product>) {
        var removePosition = -1
        try {
            for (i in 0 until selectedProductList.size) {
                if (selectedProductList[i].quantity.toInt() == 0) {
                    removePosition = i
                    break
                }
            }
        } catch (e: java.lang.NumberFormatException) {
            Log.e("TAG", "onProductQuantityUpdate: --$e")
        }

        viewModel.totalOrderAmount = 0f
        for (item in productList) {
            viewModel.totalOrderAmount += (item.quantity.ifEmpty { "0" }
                .toInt() * item.price.toFloat())
        }
        binding.orderAmount = viewModel.totalOrderAmount.toString()
        if (removePosition != -1) {
            selectedProductList[removePosition].quantity = "1"
            selectedProductList.removeAt(removePosition)
            binding.selectedProductAdapter?.notifyItemRemoved(removePosition)
            binding.selectedProductAdapter?.notifyItemRangeChanged(
                removePosition,
                selectedProductList.size
            )
            binding.isAnySelectedProduct = selectedProductList.isNotEmpty()
        }
    }

    override fun onAddQuantityClick(position: Int) {
        if (selectedProductList[position].quantity == "999") {
            return
        }
        selectedProductList[position].quantity =
            (selectedProductList[position].quantity.ifEmpty { "0" }.toInt() + 1).toString()
        binding.selectedProductAdapter?.notifyItemChanged(position)
    }

    override fun onRemoveQuantityClick(position: Int) {
        binding.root.clearFocus()
        try {
            if (selectedProductList[position].quantity.toInt() <= 1) {
                selectedProductList[position].quantity = "1"
                selectedProductList[position].isProductSelected = 0
                selectedProductList.removeAt(position)
                binding.selectedProductAdapter?.notifyItemRemoved(position)
                binding.selectedProductAdapter?.notifyItemRangeChanged(
                    position,
                    selectedProductList.size
                )
                binding.isAnySelectedProduct = selectedProductList.isNotEmpty()
            } else {
                selectedProductList[position].isProductSelected = 1
                selectedProductList[position].quantity =
                    (selectedProductList[position].quantity.ifEmpty { "0" }.toInt() - 1).toString()
                binding.selectedProductAdapter?.notifyItemChanged(position)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onConfirmClick(orderData: OrderData) {
        showProgressDialog()
        viewModel.createOrder(orderData)
    }

    override fun onCreateOrderClick() {
        // checked whether any product has empty quantity to prevent 0 amount order
        if (Utils.isSingleClick()){
            var amount = 0f
            val iterator = selectedProductList.iterator()
            while (iterator.hasNext()) {
                val item = iterator.next()
                if (item.quantity.isEmpty()) {
                    item.quantity = "1"
                    iterator.remove()
                } else {
                    amount += (item.price.toFloat() * item.quantity.toInt())
                }
            }
            binding.isAnySelectedProduct = selectedProductList.isNotEmpty()
            binding.selectedProductAdapter?.notifyDataSetChanged()
            viewModel.totalOrderAmount = amount
            binding.orderAmount = viewModel.totalOrderAmount.toString()

            when {
                viewModel.selectedDate == 0L -> {
                    context?.let {
                        dialogHelper.showOneButtonDialog(
                            it,
                            Utils.getTranslatedValue(it.getString(R.string.please_select_order_date))
                        )
                    }
                }

                viewModel.selectedCustomerData == null -> {
                    context?.let {
                        dialogHelper.showOneButtonDialog(
                            it,
                            Utils.getTranslatedValue(it.getString(R.string.please_select_customer))
                        )
                    }
                }

                selectedProductList.isEmpty() -> {
                    context?.let {
                        dialogHelper.showOneButtonDialog(
                            it,
                            Utils.getTranslatedValue(it.getString(R.string.please_select_Product))
                        )
                    }
                }

                viewModel.totalOrderAmount <= 0 -> {
                    context?.let {
                        dialogHelper.showOneButtonDialog(
                            it,
                            Utils.getTranslatedValue(it.getString(R.string.order_amount_error))
                        )
                    }
                }

                else -> {
                    val orderData = OrderData()
                    with(orderData) {
                        localOrderId = Utils.generateUUID()
                        createdById = preferenceManager.getPref(Constants.prefUserId, "")
                        orderDate = Utils.formatDateToUTC(viewModel.selectedDate)
                        customerId = viewModel.selectedCustomerData?.id.toString()
                        products = Utils.getOrderJson(selectedProductList)
                        product = selectedProductList
                        orderAmount = viewModel.totalOrderAmount.toString()
                        createdAt = Utils.getCurrentDateTime()
                        updatedAt = createdAt
                        sync = 0
                        note = binding.etNote.text.toString()
                    }
                    val customerName = binding.etCustomer.text.toString()
                    context?.let {
                        OrderConfirmationDialogFragment.show(
                            parentFragmentManager,
                            orderData,
                            this,
                            customerName, preferenceManager.getPref(Constants.prefCurrencySymbol, "")
                        )
                    }
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        binding.etDate.setText(Utils.getCurrentDateTimeForOrder())
    }
}