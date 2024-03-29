package com.rayo.digitaldiary.ui.customer

import androidx.lifecycle.MutableLiveData
import com.rayo.digitaldiary.api.APIRepository
import com.rayo.digitaldiary.api.ApiException
import com.rayo.digitaldiary.api.NoInternetException
import com.rayo.digitaldiary.api.UnauthorizedUserException
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.baseClasses.Event
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.PreferenceManager
import com.rayo.digitaldiary.ui.staff.GenerateQRCodeResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Mittal Varsani on 12/04/23.
 *
 * @author Mittal Varsani
 */

@HiltViewModel
class CustomerDetailViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val apiRepository: APIRepository
) :
    BaseViewModel() {

    val generateQRSuccess = MutableLiveData<GenerateQRCodeResponse>()
    val getCustomerProfileData = MutableLiveData<CustomerProfileResponce>()
    var QRCode = ""

    fun generateQRCode(
        id: String
    ) {
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.generateQRCode(
                        preferenceManager.getPref(Constants.prefToken, ""),
                        preferenceManager.getPref(
                            Constants.prefLanguageCode,
                            Constants.languageCodeDefault
                        ),
                        id
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
            if (it != null && it is GenerateQRCodeResponse) {
                QRCode = it.generateQRData?.qrCode ?: ""
                generateQRSuccess.postValue(it)
            }
        })
    }
}