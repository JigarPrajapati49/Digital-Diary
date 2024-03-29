package com.rayo.digitaldiary.baseClasses

interface CallMessageDialogPresenter {
    fun onCallClicked(contactNo: String)
    fun onMessageClicked(contactNo: String)
    fun onCopyClicked(contactNo: String)
}