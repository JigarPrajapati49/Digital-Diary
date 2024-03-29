package com.rayo.digitaldiary.ui.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.databinding.FragmentNotificationBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Mittal Varsani on 21/06/23.
 *
 * @author Mittal Varsani
 */
@AndroidEntryPoint
class NotificationFragment : BaseFragment<FragmentNotificationBinding, NotificationViewModel>(),
    NotificationPresenter {

    override val viewModel: NotificationViewModel by viewModels()

    override fun getFragmentId(): Int {
        return R.layout.fragment_notification
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        Log.e("TAG", "-------PermissionResult---------$isGranted: ")
        if (isGranted) {
            viewModel.position?.let { onNotificationItemClick(it, viewModel.notificationData) }
        } else {
            viewModel.position = null
            viewModel.notificationData = NotificationData()
            dialogHelper.showPermissionRequiredDialog(
                requireContext(),
                requireContext().getString(R.string.permission_required)
            ) { dialogInterface, i ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            viewModel.isNotificationGrantOrNot = checkNotificationPermission()
        }
        showProgressDialog()
        setNotificationListAdapter()
        viewModel.notificationList.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            binding.notificationAdapter?.notifyDataSetChanged()
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission(): Boolean {
        val cameraPermission = Manifest.permission.POST_NOTIFICATIONS
        return ContextCompat.checkSelfPermission(requireContext(), cameraPermission) == PackageManager.PERMISSION_GRANTED
    }


    override fun onNotificationItemClick(position: Int, notificationData: NotificationData) {
        if (networkInterceptor.isInternetAvailable()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
            ) {
                viewModel.position = position
                viewModel.notificationData = notificationData
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // if value is true then click on the item click value will be false
                notificationData.enable = notificationData.enable != true
                viewModel.updateNotification(
                    preferenceManager.getPref(Constants.prefUserId, ""),
                    viewModel.notificationLists[0].enable,
                    viewModel.notificationLists[1].enable
                )
                binding.notificationAdapter?.notifyItemChanged(position)
            }
        } else {
            context?.let {
                dialogHelper.showOneButtonDialog(
                    it,
                    Utils.getTranslatedValue(it.getString(R.string.no_internet))
                )
            }
        }
    }

    private fun setNotificationListAdapter() {
        binding.notificationAdapter = NotificationAdapter(viewModel.notificationLists, this)
    }
}