package com.rayo.digitaldiary.ui.staff

import androidx.lifecycle.MutableLiveData
import com.rayo.digitaldiary.api.*
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.baseClasses.Event
import com.rayo.digitaldiary.database.StaffDao
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Mittal Varsani on 10/04/23.
 *
 * @author Mittal Varsani
 */

@HiltViewModel
class StaffViewModel @Inject constructor(
    private val apiRepository: APIRepository,
    private val preferenceManager: PreferenceManager,
    private val staffDao: StaffDao
) : BaseViewModel() {

    val staffList = MutableLiveData<MutableList<StaffData>>()

    init {
        getAllStaffFromDatabase()
    }

    private fun getAllStaffFromDatabase() {
        Coroutines.ioThenMain({
            staffDao.getStaff()
        }, {
            staffList.postValue(it as MutableList<StaffData>?)
        })
    }
}