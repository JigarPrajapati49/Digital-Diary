package com.rayo.digitaldiary.notification

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseDialogFragment
import com.rayo.digitaldiary.baseClasses.BaseInterface
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.database.StaffWithPayment
import com.rayo.digitaldiary.databinding.FragmentDialogNotificationDetailsBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.ui.customer.CustomerMainActivityPresenter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DialogNotificationDetailsFragment(
    private val staffWithPayment: StaffWithPayment,
    val presenter: CustomerMainActivityPresenter,
) :
    BaseDialogFragment<FragmentDialogNotificationDetailsBinding, BaseViewModel?>(),
    BaseInterface {

    override val viewModel: BaseViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            mainActivityPresenter = presenter
            isCustomer = preferenceManager.getPref(Constants.prefUserType, "") == Constants.USER_TYPE_CUSTOMER
            notificationDataPresenter = this@DialogNotificationDetailsFragment
            staffWithPayment = this@DialogNotificationDetailsFragment.staffWithPayment
            currencySymbol = preferenceManager.getPref(Constants.prefCurrencySymbol, "")
        }
    }

    override fun onStart() {
        super.onStart()
        dialogNotificationDetail = dialog
    }

    override fun getDialogFragmentId(): Int {
        return R.layout.fragment_dialog_notification_details
    }

    companion object {
        private const val TAG = "NotificationDataDialog"
        private var dialogNotificationDetail: Dialog? = null
        fun show(
            fragmentManager: FragmentManager,
            staffWithPayment: StaffWithPayment,
            presenter: CustomerMainActivityPresenter,
        ) {
            val notificationDataDialog = DialogNotificationDetailsFragment(staffWithPayment, presenter)
            notificationDataDialog.show(fragmentManager, TAG)
        }

        fun dismiss() {
            dialogNotificationDetail?.dismiss()
        }

    }


}