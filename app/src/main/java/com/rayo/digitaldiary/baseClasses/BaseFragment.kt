package com.rayo.digitaldiary.baseClasses

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.rayo.digitaldiary.DigitalDiaryApplication
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.api.APIRepository
import com.rayo.digitaldiary.api.NetworkConnectionInterceptor
import com.rayo.digitaldiary.database.DigitalDiaryDatabase
import com.rayo.digitaldiary.helper.DialogHelper
import com.rayo.digitaldiary.helper.MyProgressDialog
import com.rayo.digitaldiary.helper.PreferenceManager
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.helper.toast
import com.rayo.digitaldiary.ui.OwnerMainActivity
import com.rayo.digitaldiary.ui.customer.CustomerMainActivity
import com.rayo.digitaldiary.ui.staff.StaffMainActivity
import java.io.File
import javax.inject.Inject

abstract class BaseFragment<T : ViewDataBinding, VM : ViewModel?> : Fragment(), BaseInterface {

    protected lateinit var binding: T
    abstract val viewModel: VM
    private var myProgressDialog: MyProgressDialog? = null

    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Inject
    lateinit var networkInterceptor: NetworkConnectionInterceptor

    @Inject
    lateinit var dialogHelper: DialogHelper
    lateinit var mActivity: Activity

    @Inject
    lateinit var apiRepository: APIRepository

    @Inject
    lateinit var localDatabaseInstance: DigitalDiaryDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        if (!this::binding.isInitialized) {
            binding = DataBindingUtil.inflate(inflater, getFragmentId(), container, false)
        }
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
            (mActivity as BaseActivity<*>).logoutSuccess()
            it.getContentIfNotHandled()?.let { error ->
                context?.toast(error)
            }
        }
        (viewModel as BaseViewModel?)?.logoutSuccess?.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            (mActivity as BaseActivity<*>).logoutSuccess()
        }
    }

    abstract fun getFragmentId(): Int

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
    }

    internal fun showPermissionSetting() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", context?.packageName, null)
        intent.data = uri
        context?.startActivity(intent)
    }

    fun openDialer(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:$phoneNumber")
        startActivity(callIntent)
    }

    fun getFileUri(file: File): Uri? {
        context?.let { mContext ->
            return FileProvider.getUriForFile(
                mContext, context?.packageName + ".provider", file
            )
        }
        return null
    }

    override fun onBackClick() {
        findNavController().popBackStack()
    }

    override fun onCancelClick() {
        findNavController().popBackStack()
    }

    override fun onSyncClick() {
        if (!networkInterceptor.isInternetAvailable()) {
            context?.let {
                dialogHelper.showOneButtonDialog(
                    it,
                    Utils.getTranslatedValue(it.getString(R.string.no_internet))
                )
            }
            return
        }
        DigitalDiaryApplication.instance.isShowSyncProgress = true
        when (activity) {
            is OwnerMainActivity -> {
                (activity as OwnerMainActivity).syncAllData()
            }

            is StaffMainActivity -> {
                (activity as StaffMainActivity).syncAllData()
            }

            is CustomerMainActivity -> {
                (activity as CustomerMainActivity).syncAllDataForNewCustomer()
            }
        }
    }

    fun callLogoutAPI(isMultipleDeviceLogout: Boolean) {
        showProgressDialog()
        (viewModel as BaseViewModel?)?.callLogoutAPI(apiRepository, preferenceManager, isMultipleDeviceLogout)
    }
}
