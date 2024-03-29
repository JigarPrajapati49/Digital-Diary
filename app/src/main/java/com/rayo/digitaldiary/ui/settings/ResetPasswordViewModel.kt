package com.rayo.digitaldiary.ui.settings

import androidx.lifecycle.MutableLiveData
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.api.*
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.baseClasses.Event
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.PreferenceManager
import com.rayo.digitaldiary.helper.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val apiRepository: APIRepository,
    private val preferenceManager: PreferenceManager
) : BaseViewModel() {

    val resetPasswordSuccess = MutableLiveData<CommonResponse>()

    fun changePassword(oldPassword: String, newPassword: String) {
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.changePassword(
                        preferenceManager.getPref(Constants.prefToken, ""),
                        preferenceManager.getPref(
                            Constants.prefLanguageCode,
                            Constants.languageCodeDefault
                        ), oldPassword, newPassword
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
            if (it != null && it is CommonResponse) {
                resetPasswordSuccess.postValue(it)
            }
        })
    }
}