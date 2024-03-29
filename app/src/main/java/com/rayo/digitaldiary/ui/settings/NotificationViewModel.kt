package com.rayo.digitaldiary.ui.settings

import androidx.lifecycle.MutableLiveData
import com.rayo.digitaldiary.api.APIRepository
import com.rayo.digitaldiary.api.NoInternetException
import com.rayo.digitaldiary.api.UnauthorizedUserException
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.baseClasses.Event
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val apiRepository: APIRepository
) : BaseViewModel() {

    val notificationList = MutableLiveData<NotificationResponce>()
    val notificationLists: MutableList<NotificationData> = ArrayList()
    var isNotificationGrantOrNot: Boolean = true
    var position: Int? = null
    var notificationData = NotificationData()

    init {
        getNotificationData(preferenceManager.getPref(Constants.prefUserId, ""))
    }

    // get the List from Notification Data From responce and add In Our ArrayList
    private fun createNotificationList(notificationDataDetails: NotificationDataDetails? = null, permissionGrantOrNot: Boolean) {
        if (!permissionGrantOrNot) {
            with(NotificationData()) {
                name = "order"
                enable = false
                notificationLists.add(this)
            }
            with(NotificationData()) {
                name = "payments"
                enable = false
                notificationLists.add(this)
            }
        } else {
            with(NotificationData()) {
                name = "order"
                enable = notificationDataDetails?.order ?: true
                notificationLists.add(this)
            }
            with(NotificationData()) {
                name = "payments"
                enable = notificationDataDetails?.payment ?: true
                notificationLists.add(this)
            }
        }
    }

    private fun getNotificationData(id: String) {
        Coroutines.ioThenMain({
            try {
                apiRepository.getNotification(
                    preferenceManager.getPref(Constants.prefToken, ""),
                    preferenceManager.getPref(
                        Constants.prefLanguageCode,
                        Constants.languageCodeDefault
                    ),
                    id
                )
            } catch (e: NoInternetException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: UnauthorizedUserException) {
                unauthorizedUserHandler.postValue(Event(e.message))
            } catch (e: Exception) {
                errorMessage.postValue(Event(e.message))
            }
        }, {
            if (it != null && it is NotificationResponce) {
                notificationList.postValue(it)
                // get the notification list And Add in Our own created list
                createNotificationList(it.data, isNotificationGrantOrNot)
            }
        })
    }

    fun updateNotification(id: String, order: Boolean, transaction: Boolean) {
        Coroutines.ioThenMain({
            try {
                apiRepository.updateNotification(
                    preferenceManager.getPref(Constants.prefToken, ""),
                    preferenceManager.getPref(
                        Constants.prefLanguageCode,
                        Constants.languageCodeDefault
                    ),
                    id, order, transaction
                )
            } catch (e: Exception) {
                errorMessage.postValue(Event(e.message))
            } catch (e: NoInternetException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: UnauthorizedUserException) {
                unauthorizedUserHandler.postValue(Event(e.message))
            }
        }, {
            if (it != null && it is NotificationResponce) {
                notificationList.postValue(it)
            }
        })
    }
}