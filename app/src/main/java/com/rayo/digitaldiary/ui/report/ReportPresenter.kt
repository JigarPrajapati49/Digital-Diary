package com.rayo.digitaldiary.ui.report

interface ReportPresenter {
    fun onOrderClick()
    fun onPaymentClick()
    fun onDueClick()
}

interface OrderReportPresenter {
    fun onNextButtonClick()
    fun onPreviousButtonClick()
    fun onOrderDateClick() {}
}

interface PaymentReportPresenter {
    fun onNextButtonClick()
    fun onPreviousButtonClick()
    fun onPaymentDateClick() {}
}

interface DueReportPresenter {
    fun onPhoneNumberClick(phoneNumber: String?)
}