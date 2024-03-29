package com.rayo.digitaldiary.baseClasses

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.onesignal.OneSignal
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.api.NetworkConnectionInterceptor
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.DialogHelper
import com.rayo.digitaldiary.helper.MyProgressDialog
import com.rayo.digitaldiary.helper.PreferenceManager
import com.rayo.digitaldiary.helper.toast
import com.rayo.digitaldiary.ui.login.AuthenticationActivity
import javax.inject.Inject

/**
 * Created by Mittal Varsani on 23/11/21.
 * @author Mittal Varsani
 */

abstract class BaseBottomSheetDialogFragment<T : ViewDataBinding, VM : ViewModel?> :
    BottomSheetDialogFragment() {
    protected lateinit var binding: T
    private var myProgressDialog: MyProgressDialog? = null
    lateinit var mActivity: Activity
    abstract val viewModel: VM

    @Inject
    lateinit var preferenceManager: PreferenceManager
    @Inject
    lateinit var networkInterceptor: NetworkConnectionInterceptor
    @Inject
    lateinit var dialogHelper: DialogHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getDialogFragmentId(), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (viewModel as BaseViewModel?)?.errorMessage?.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            it.getContentIfNotHandled()?.let { error ->
                context?.toast(error)
            }
        }
        (viewModel as BaseViewModel?)?.unauthorizedUserHandler?.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            logout()
            it.getContentIfNotHandled()?.let { error ->
                context?.toast(error)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    abstract fun getDialogFragmentId(): Int

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(true)
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
            dialog.window?.setWindowAnimations(R.style.Theme_DigitalDiary_Slide)
        }
    }


    fun showProgressDialog() {
        if (myProgressDialog != null) {
            myProgressDialog?.show()
        } else {
            myProgressDialog = MyProgressDialog.show(mActivity, "")
        }
    }

    fun dismissProgressDialog() {
        myProgressDialog?.dismiss()
    }

    private fun logout() {
        val prefKeyList = ArrayList<String>()
        prefKeyList.add(Constants.prefLanguageCode)
        prefKeyList.add(Constants.prefLanguage)
        prefKeyList.add(Constants.prefLanguageTranslationLastSyncTime)
        preferenceManager.clearAll(prefKeyList)
        OneSignal.removeExternalUserId()
        startActivity(Intent(context, AuthenticationActivity::class.java))
        mActivity.finish()
    }
}