package com.rayo.digitaldiary.ui.customer

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.databinding.FragmentGenerateQrCodeBinding
import com.rayo.digitaldiary.helper.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GenerateQrCodeFragment : BaseFragment<FragmentGenerateQrCodeBinding, GenerateQrCodeViewModel>(), GenerateQRCodePresenter {

    override val viewModel: GenerateQrCodeViewModel by viewModels()
    var customerId: String = ""

    override fun getFragmentId(): Int {
        return R.layout.fragment_generate_qr_code
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.arguments?.let {
            showProgressDialog()
            customerId = it.getString(Constants.customerId, "")
        }

        binding.qrCode = viewModel.QRCode
        binding.customerId = customerId
        binding.generateQrCodePresenter = this
        viewModel.generateQRCode(customerId)
        viewModel.generateQRSuccess.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            binding.qrCode = viewModel.QRCode
        }
    }

    override fun onGenerateQRClick(customerId: String) {
        showProgressDialog()
        viewModel.generateQRCode(customerId)
    }
}