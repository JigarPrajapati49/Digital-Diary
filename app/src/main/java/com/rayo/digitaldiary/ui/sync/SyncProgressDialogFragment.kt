package com.rayo.digitaldiary.ui.sync

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.rayo.digitaldiary.DigitalDiaryApplication
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseDialogFragment
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.baseClasses.ProgressData
import com.rayo.digitaldiary.databinding.FragmentSyncProgressDialogBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Mittal Varsani on 08/08/23.
 *
 * @author Mittal Varsani
 */
@AndroidEntryPoint
class SyncProgressDialogFragment :
    BaseDialogFragment<FragmentSyncProgressDialogBinding, BaseViewModel?>() {

    override val viewModel: BaseViewModel? = null

    override fun getDialogFragmentId(): Int {
        return R.layout.fragment_sync_progress_dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.syncProgress = 0
    }

    fun onProgressChanged(progressData: ProgressData) {
        binding.syncProgress = (progressData.syncCount * 100) / progressData.totalSyncCollection
    }

    companion object {
        private const val TAG = "SelectCurrencyDialog"
        fun show(
            fragmentManager: FragmentManager
        ): SyncProgressDialogFragment {
            DigitalDiaryApplication.instance.isShowSyncProgress = false
            val syncProgressDialog =
                SyncProgressDialogFragment()
            syncProgressDialog.show(fragmentManager, TAG)
            return syncProgressDialog
        }
    }
}