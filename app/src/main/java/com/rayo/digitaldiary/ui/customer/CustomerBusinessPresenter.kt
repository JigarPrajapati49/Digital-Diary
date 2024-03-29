package com.rayo.digitaldiary.ui.customer

import com.rayo.digitaldiary.ui.login.ScanQRData

interface CustomerBusinessPresenter {
    fun onQRCodeClick()
    fun onItemClick(scanData: ScanQRData)
}