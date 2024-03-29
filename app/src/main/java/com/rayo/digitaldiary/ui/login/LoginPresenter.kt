package com.rayo.digitaldiary.ui.login

import com.rayo.digitaldiary.baseClasses.BaseInterface

/**
 * Created by Mittal Varsani on 09/02/23.
 *
 * @author Mittal Varsani
 */
interface LoginPresenter {
    fun onSignUpClick()

    fun onForgotPasswordClick()

    fun onLoginClick()

    fun onLoginWithGoogleClick()

    fun onScanQRCodeClick()
}

interface RegisterPresenter : BaseInterface {
    fun onSingInClick() {}

    fun onRegisterClick()
}

interface ForgotPasswordPresenter : BaseInterface {
    fun onRequestToChangeClick()

    fun onBackToLoginClick()
}

interface VerifyOtpPresenter: BaseInterface {
    fun onVerifyClick()

    fun onUpdateClick()

    fun onResendOtpClick()
}

