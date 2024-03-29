package com.rayo.digitaldiary.ui.payment

import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.database.CustomerDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentFilterViewModel @Inject constructor(private val customerDao: CustomerDao) :
    BaseViewModel() {
    var selectedStartDate: Long = 0L
    var selectedEndDate: Long = 0L

}