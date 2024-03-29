package com.rayo.digitaldiary.ui.settings

import com.rayo.digitaldiary.baseClasses.BaseInterface
import com.rayo.digitaldiary.ui.LanguageData

interface SettingsPresenter : BaseInterface {

    fun logOutClick() {}

    fun onNotificationClick() {}

    fun languageChangeClick() {}

    fun onLanguageSelected(title: String) {}

    fun onResetPasswordClick() {}

    fun onProfileClick(){}

    fun onTroubleshootingClick(){}
}

interface LanguageSelectionPresenter: BaseInterface {

    fun onLanguageSelect(languageData: LanguageData) {}

    fun onSelectClick(languageData: LanguageData?)

}

interface NotificationPresenter {

    fun onNotificationItemClick(position: Int, notificationData: NotificationData)
}