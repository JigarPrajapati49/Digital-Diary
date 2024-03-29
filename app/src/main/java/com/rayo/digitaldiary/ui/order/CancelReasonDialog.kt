package com.rayo.digitaldiary.ui.order

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseDialogFragment
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.database.CustomerWithHistory
import com.rayo.digitaldiary.databinding.DialogCancelReasonBinding
import com.rayo.digitaldiary.ui.history.CancelOrderPresenter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CancelReasonDialog(val customerWithHistory: CustomerWithHistory, val cancelOrderPresenter: CancelOrderPresenter) :
    BaseDialogFragment<DialogCancelReasonBinding, BaseViewModel?>(),
    CancelOrderPresenter {

    override val viewModel: BaseViewModel? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.presenter = this
        binding.customerWithHistory = customerWithHistory
    }

    override fun getDialogFragmentId(): Int {
        return R.layout.dialog_cancel_reason
    }

    companion object {
        private const val TAG = "CancelReasonDialog"

        fun show(fragmentManager: FragmentManager, customerWithHistory: CustomerWithHistory,cancelOrderPresenter: CancelOrderPresenter) {
            val cancelReasonDialog = CancelReasonDialog(customerWithHistory,cancelOrderPresenter)
            cancelReasonDialog.show(fragmentManager, TAG)
        }
    }

    override fun onCancelClick() {
        dismiss()
    }

    override fun onOkyClick(customerWithHistory: CustomerWithHistory) {
        dismiss()
        if (binding.etNote.text?.trim().toString().isEmpty()) {
            context?.let {
                dialogHelper.showOneButtonDialog(
                    it,
                    "You must provide a reason to cancel order"
                )
            }
        } else {
            customerWithHistory.orderData.cancelReason = binding.etNote.text?.trim().toString()
            cancelOrderPresenter.onOkyClick(customerWithHistory)
        }
    }
}