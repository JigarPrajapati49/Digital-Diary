package com.rayo.digitaldiary.ui.customer

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.rayo.digitaldiary.DigitalDiaryApplication
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseActivity
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.databinding.FragmentCustomerBusinessBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.login.ScanQRData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomerBusinessFragment :
    BaseFragment<FragmentCustomerBusinessBinding, CustomerBusinessViewModel>(),
    CustomerBusinessPresenter {

    override val viewModel: CustomerBusinessViewModel by viewModels()
    private val customerDetailsList: MutableList<ScanQRData> = ArrayList()

    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        Log.d("TAG", ": barcodeLauncher result $result")
        result.contents?.let {
            showProgressDialog()
            viewModel.callScanQRCodeAPI(it, true)
        }
    }

    override fun getFragmentId(): Int {
        return R.layout.fragment_customer_business
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.presenter = this
        (requireActivity() as CustomerMainActivity).customerBusinessPresenter = this
        showProgressDialog()
        setCustomerDetailsListAdapter()

        viewModel.scanQRSuccess.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                it.scanQRData?.let { it1 ->
                    dismissProgressDialog()
                    viewModel.getCustomerLoginDetailsFromDB()
                    saveCurrentBusinessData(it.scanQRData)
                    (requireActivity() as CustomerMainActivity).syncAllDataForNewCustomer()
                }
            }
        }
        viewModel.getCustomerLoginDetailsFromDB()

        viewModel.customerLoginDetailsList.observe(viewLifecycleOwner) {
            customerDetailsList.clear()
            for(item in it) {
                if(item.userId == preferenceManager.getPref(Constants.prefUserId, "")) {
                    item.isSelected = true
                }
            }
            customerDetailsList.addAll(it)
            binding.customerDetailsAdapter?.notifyDataSetChanged()
            dismissProgressDialog()
        }
    }

    override fun onQRCodeClick() {
        if (networkInterceptor.isInternetAvailable()) {
            barcodeLauncher.launch(
                ScanOptions()
                    .setOrientationLocked(false)
                    .setBeepEnabled(false)
                    .setCameraId(0)
                    .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            )
        } else {
            context?.let {
                dialogHelper.showOneButtonDialog(
                    it,
                    Utils.getTranslatedValue(it.getString(R.string.no_internet))
                )
            }
        }
    }

    override fun onItemClick(scanData: ScanQRData) {
        saveCurrentBusinessData(scanData)
        (mActivity as CustomerMainActivity).moveToDashboard()
    }

    private fun saveCurrentBusinessData(scanData: ScanQRData) {
        DigitalDiaryApplication.instance.currentCustomerId = scanData.userId
        (activity as BaseActivity<*>).saveCurrentSelectedCustomerData(scanData)
    }

    private fun setCustomerDetailsListAdapter() {
        binding.customerDetailsAdapter = CustomerDetailsAdapter(customerDetailsList, this)
    }
}