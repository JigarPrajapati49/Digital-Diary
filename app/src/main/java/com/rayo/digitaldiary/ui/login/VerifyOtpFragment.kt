package com.rayo.digitaldiary.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.fragment.app.viewModels
import com.rayo.digitaldiary.DigitalDiaryApplication
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.databinding.FragmentVerifyOtpBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.helper.toast
import com.rayo.digitaldiary.ui.OwnerMainActivity
import com.rayo.digitaldiary.ui.customer.CustomerMainActivity
import com.rayo.digitaldiary.ui.staff.StaffMainActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


/**
 * Created by Mittal Varsani on 02/05/22.
 *
 * @author Mittal Varsani
 */

@AndroidEntryPoint
class VerifyOtpFragment : BaseFragment<FragmentVerifyOtpBinding, ForgotPasswordViewModel>(),
    VerifyOtpPresenter {

    private var email: String? = null
    private var mCountDownTimer: CountDownTimer? = null
    private var count = 30

    override val viewModel: ForgotPasswordViewModel by viewModels()

    override fun getFragmentId(): Int {
        return R.layout.fragment_verify_otp
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.presenter = this
        email = this.arguments?.getString(Constants.email, "")
        startTimer()

        viewModel.forgotPasswordSuccess.observe(viewLifecycleOwner) { response ->
            dismissProgressDialog()
            response.getContentIfNotHandled()?.let { error ->
                error.message?.let { it1 -> context?.toast(it1) }
                count = 30
                binding.btnResendOtp.isEnabled = false
                mCountDownTimer = object : CountDownTimer(30000, 1000) {

                    override fun onTick(millisUntilFinished: Long) {
                        binding.btnResendOtp.text =
                            String.format(Utils.getTranslatedValue(mActivity.getString(R.string.resend_otp)), count) + String.format(
                                Locale(
                                    preferenceManager.getPref(
                                        Constants.prefLanguageCode,
                                        Constants.languageCodeDefault
                                    )
                                ),
                                mActivity.getString(R.string.resent_otp_time),
                                count
                            )
                        count--
                    }

                    override fun onFinish() {
                        binding.btnResendOtp.text = String.format(Utils.getTranslatedValue(mActivity.getString(R.string.resend_otp)), count)
                        binding.btnResendOtp.isEnabled = true
                        mCountDownTimer?.cancel()
                    }
                }
                mCountDownTimer?.start()
            }
        }

        viewModel.verifyOtpSuccess.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            binding.etVerificationCode.text?.clear()
            binding.isOTPVerified = true
            mCountDownTimer?.cancel()
        }

        viewModel.loginSuccess.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            openNextActivity(it.message)
        }

        viewModel.resetPasswordSuccess.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            it.message?.let { it1 -> context?.toast(it1) }
            val password = binding.etPassword.text.toString()
            email?.let { it1 -> viewModel.callLoginAPI(it1, password) }
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

    private fun startTimer() {
        mCountDownTimer = object : CountDownTimer(30000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                if (count > 0) {
                    binding.btnResendOtp.text =
                        String.format(
                            Utils.getTranslatedValue(mActivity.getString(R.string.resend_otp)),
                            count
                        ) +
                                String.format(
                                    Locale(
                                        preferenceManager.getPref(
                                            Constants.prefLanguageCode,
                                            Constants.languageCodeDefault
                                        )
                                    ),
                                    mActivity.getString(R.string.resent_otp_time),
                                    count
                                )
                    count--
                } else {
                    mCountDownTimer?.onFinish()
                }
            }

            override fun onFinish() {
                binding.btnResendOtp.text = String.format(
                    Utils.getTranslatedValue(mActivity.getString(R.string.resend_otp)),
                    count
                )
                binding.btnResendOtp.isEnabled = true
                mCountDownTimer?.cancel()
            }
        }
        mCountDownTimer?.start()
    }

    override fun onVerifyClick() {
        val otp = binding.etVerificationCode.text.toString()
        isValidOtp(otp)?.let {
            context?.let { it1 ->
                dialogHelper.showOneButtonDialog(
                    it1,
                    Utils.getTranslatedValue(resources.getString(it))
                )
            }
        } ?: run {
            showProgressDialog()
            email?.let { viewModel.callVerifyOtp(it, otp) }
        }
    }

    private fun isValidOtp(otp: String): Int? {
        return when {
            otp.trim().isEmpty() -> {
                binding.etVerificationCode.requestFocus()
                R.string.enter_otp
            }
            else -> {
                null
            }
        }
    }

    override fun onUpdateClick() {
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()
        isValidPassword(password, confirmPassword)?.let {
            context?.let { it1 ->
                dialogHelper.showOneButtonDialog(
                    it1,
                    Utils.getTranslatedValue(resources.getString(it))
                )
            }
        } ?: run {
            showProgressDialog()
            email?.let { viewModel.callResetPassword(it, password) }
        }
    }

    private fun isValidPassword(password: String, confirmPassword: String): Int? {
        return when {
            password.trim().isEmpty() -> {
                binding.etPassword.requestFocus()
                R.string.enter_password
            }
            !Utils.isValidPassword(password) -> {
                binding.etPassword.requestFocus()
                R.string.enter_valid_password
            }
            confirmPassword.trim().isEmpty() -> {
                binding.etConfirmPassword.requestFocus()
                R.string.enter_confirm_password
            }
            (password != confirmPassword) -> {
                binding.etConfirmPassword.requestFocus()
                R.string.password_and_confirm_password
            }
            else -> {
                null
            }
        }
    }

    override fun onResendOtpClick() {
        val email = email.toString()
        isValidEmail(email)?.let {
            context?.let { it1 ->
                dialogHelper.showOneButtonDialog(
                    it1,
                    Utils.getTranslatedValue(resources.getString(it))
                )
            }
        } ?: run {
            showProgressDialog()
            viewModel.callForgotPasswordAPI(email)
        }
    }

    private fun isValidEmail(email: String): Int? {
        return when {
            email.trim().isEmpty() -> {
                R.string.enter_email
            }
            !Utils.isValidEmail(email) -> {
                R.string.invalid_email
            }
            else -> {
                null
            }
        }
    }
}