package com.rayo.digitaldiary.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.rayo.digitaldiary.DigitalDiaryApplication
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.databinding.FragmentLoginBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.helper.toast
import com.rayo.digitaldiary.ui.OwnerMainActivity
import com.rayo.digitaldiary.ui.customer.CustomerMainActivity
import com.rayo.digitaldiary.ui.staff.StaffMainActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Mittal Varsani on 12/04/22.
 *
 * @author Mittal Varsani
 */

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>(), LoginPresenter {

    override val viewModel: LoginViewModel by viewModels()

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        Log.d("TAG", ": barcodeLauncher result $result")
        result.contents?.let {
            showProgressDialog()
            viewModel.callScanQRCodeAPI(it)
        }
    }

    private var googleSignInResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                account.email?.let { mEmail ->
                    showProgressDialog()
                    viewModel.callLoginWithGoogleAPI(
                        account.id.toString(),
                        mEmail
                    )
                }
            } catch (e: ApiException) {
                e.printStackTrace()
                dismissProgressDialog()
                Log.w("TAG", "signInResult:failed code=" + e.statusCode)
            }
        }

    override fun getFragmentId(): Int {
        return R.layout.fragment_login
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.presenter = this

        /* Google login */
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(mActivity, gso)

        viewModel.loginSuccess.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            openNextActivity(it.message)
        }

        viewModel.loginWithGoogleSuccess.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                dismissProgressDialog()
                if (it.googleLoginData?.isNewUser == 1) {
                    findNavController().navigate(R.id.completeGoogleLoginFragment)
                } else {
                    openNextActivity(it.message)
                }
            }
        }

        viewModel.scanQRSuccess.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                dismissProgressDialog()
                // set as default selected business detail for customer
                preferenceManager.savePref(Constants.prefUserId, it.scanQRData?.userId)
                preferenceManager.savePref(Constants.prefUserType, it.scanQRData?.userType)
                preferenceManager.savePref(Constants.businessName, it.scanQRData?.businessName)
                openNextActivity(it.message)
            }
        }
    }

    private fun openNextActivity(message: String?) {
        DigitalDiaryApplication.instance.isShowSyncProgress = true
        message?.let { it1 -> context?.toast(it1) }
        when (preferenceManager.getPref(Constants.prefUserType, "")) {
            Constants.USER_TYPE_CUSTOMER -> {
                startActivity(Intent(context, CustomerMainActivity::class.java))
            }

            Constants.USER_TYPE_STAFF -> {
                startActivity(Intent(context, StaffMainActivity::class.java))
            }

            else -> { // Business owner login
                startActivity(Intent(context, OwnerMainActivity::class.java))
            }
        }
        activity?.finish()
    }

    override fun onSignUpClick() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    override fun onForgotPasswordClick() {
        findNavController().navigate(R.id.forgotPasswordFragment)
    }

    override fun onLoginClick() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        isValidLoginForm(email, password)?.let {
            context?.let { it1 ->
                dialogHelper.showOneButtonDialog(
                    it1,
                    Utils.getTranslatedValue(resources.getString(it))
                )
            }
        } ?: run {
            showProgressDialog()
            viewModel.callLoginAPI(email, password)
        }
    }

    private fun isValidLoginForm(email: String, password: String): Int? {
        return when {
            email.trim().isEmpty() -> {
                binding.etEmail.requestFocus()
                R.string.enter_email
            }
            !Utils.isValidEmail(email) -> {
                binding.etEmail.requestFocus()
                R.string.invalid_email
            }
            password.trim().isEmpty() -> {
                binding.etPassword.requestFocus()
                R.string.enter_password
            }
            else -> {
                null
            }
        }
    }

    override fun onLoginWithGoogleClick() {
        val signInIntent = mGoogleSignInClient.signInIntent
        googleSignInResultLauncher.launch(signInIntent)
    }

    override fun onScanQRCodeClick() {
        context?.let {
            dialogHelper.showOneButtonDialog(
                it,
                Utils.getTranslatedValue(getString(R.string.qr_guide))
            ) { dialog, _ ->
                run {
                    dialog.dismiss()
                    barcodeLauncher.launch(
                        ScanOptions()
                            .setOrientationLocked(false)
                            .setBeepEnabled(false)
                            .setCameraId(0)
                            .setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
                    )
                }
            }

        }

    }
}