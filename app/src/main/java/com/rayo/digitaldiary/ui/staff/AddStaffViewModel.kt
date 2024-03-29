package com.rayo.digitaldiary.ui.staff

import androidx.lifecycle.MutableLiveData
import com.rayo.digitaldiary.api.APIRepository
import com.rayo.digitaldiary.api.ApiException
import com.rayo.digitaldiary.api.NoInternetException
import com.rayo.digitaldiary.api.UnauthorizedUserException
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.baseClasses.Event
import com.rayo.digitaldiary.database.StaffDao
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Mittal Varsani on 12/04/23.
 *
 * @author Mittal Varsani
 */

@HiltViewModel
class AddStaffViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val apiRepository: APIRepository,
    private val staffDao: StaffDao
) :
    BaseViewModel() {

    val addStaffSuccess = MutableLiveData<AddStaffResponse>()

    fun callAddOrUpdateStaffAPI(staffData: StaffData) {
        if (staffData.id.isEmpty()) {
            callAddStaff(staffData)
        } else {
            callUpdateStaffProfile(staffData)
        }
    }

    private fun callAddStaff(
        staffData: StaffData
    ) {
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.createOrUpdateStaff(
                        preferenceManager.getPref(Constants.prefToken, ""),
                        preferenceManager.getPref(Constants.prefLanguageCode, Constants.languageCodeDefault),
                        staffData
                    )
                )
            } catch (e: ApiException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: NoInternetException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: UnauthorizedUserException) {
                unauthorizedUserHandler.postValue(Event(e.message))
            }
        }, {
            if (it != null && it is AddStaffResponse) {
                addStaffToDatabase(it)
            }
        })
    }

    fun callUpdateStaffProfile(
        staffData: StaffData,
        isStaffUpdateProfile: Boolean = false
    ) {
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.createOrUpdateStaff(
                        preferenceManager.getPref(Constants.prefToken, ""),
                        preferenceManager.getPref(
                            Constants.prefLanguageCode,
                            Constants.languageCodeDefault
                        ),
                        staffData
                    )
                )
            } catch (e: ApiException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: NoInternetException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: UnauthorizedUserException) {
                unauthorizedUserHandler.postValue(Event(e.message))
            }
        }, {
            if (it != null && it is AddStaffResponse) {
                if (isStaffUpdateProfile) {
                    with(preferenceManager) {
                        savePref(Constants.prefEmail, it.staffData?.email)
                        savePref(Constants.prefName, it.staffData?.name)
                        savePref(Constants.prefContactNumber, it.staffData?.contactNumber)
                    }
                    addStaffSuccess.postValue(it)
                } else {
                    addStaffToDatabase(it)
                }
            }
        })
    }

    private fun addStaffToDatabase(addStaffResponse: AddStaffResponse) {
        Coroutines.ioThenMain({
            addStaffResponse.staffData?.let { staffDao.insertStaff(it) }
        }, {
            addStaffSuccess.postValue(addStaffResponse)
        })
    }
}