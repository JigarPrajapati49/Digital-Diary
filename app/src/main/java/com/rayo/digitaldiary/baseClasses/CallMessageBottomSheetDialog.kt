package com.rayo.digitaldiary.baseClasses

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.databinding.DialogCallMessageBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CallMessageBottomSheetDialog(
    private val contactNo: String
) : BaseBottomSheetDialogFragment<DialogCallMessageBinding, BaseViewModel?>(),
    CallMessageDialogPresenter {

    override fun getDialogFragmentId(): Int {
        return R.layout.dialog_call_message
    }

    override val viewModel: BaseViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.presenter = this
        binding.mobileNo = preferenceManager.getPref(Constants.prefCountryCode, "")+' '+contactNo
    }

    override fun onCallClicked(contactNo: String) {
        if (contactNo.isNotEmpty()) {
            try {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${contactNo}")
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onMessageClicked(contactNo: String) {
        if (contactNo.isNotEmpty()) {
            try {
                val smsUri = Uri.parse("smsto:${contactNo}")
                val sms = Intent(Intent.ACTION_VIEW, smsUri)
                sms.putExtra("sms_body", "")
                startActivity(sms)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCopyClicked(contactNo: String) {
        val clipBoard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("Contact No", contactNo)
        clipBoard.setPrimaryClip(clip)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            context?.getString(R.string.copied_to_clipboard)?.let { context?.toast(it) }
        }
    }

    companion object {
        private const val TAG = "Bottom_sheet_dialog"

        fun display(
            fragmentManager: FragmentManager,
            contactNo: String
        ) {
            if(contactNo.isEmpty()) {
                return
            }
            val dialog = CallMessageBottomSheetDialog(contactNo)
            dialog.show(fragmentManager, TAG)
        }
    }
}