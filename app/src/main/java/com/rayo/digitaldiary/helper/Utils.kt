package com.rayo.digitaldiary.helper

import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import com.google.gson.GsonBuilder
import com.rayo.digitaldiary.DigitalDiaryApplication
import com.rayo.digitaldiary.ui.languageTranslation.LanguageTranslationData
import com.rayo.digitaldiary.ui.product.Product
import com.rayo.digitaldiary.ui.product.ProductJsonExclusion
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Created by Mittal Varsani on 19/04/22.
 *
 * @author Mittal Varsani
 */
class Utils {
    companion object {
        var mLastClickTime: Long = 0
        fun isValidEmail(target: CharSequence?): Boolean {
            return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }

        fun isValidPhoneNumber(target: CharSequence?): Boolean {
            return !TextUtils.isEmpty(target) && Patterns.PHONE.matcher(target).matches()
        }

        fun isValidPhoneNumbers(target: CharSequence?): Boolean {
            val patterns = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$"
            return !TextUtils.isEmpty(target) && Pattern.compile(patterns).matcher(target).matches()
        }

        fun isValidPassword(password: CharSequence?): Boolean {
            val pattern: Pattern
            val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
            pattern = Pattern.compile(PASSWORD_PATTERN)
            val matcher: Matcher = pattern.matcher(password)
            return matcher.matches()
        }

        fun generateUUIDForDeviceId(preferenceManager: PreferenceManager): String {
            if (preferenceManager.getPref(Constants.prefDeviceId, "").isEmpty()) {
                preferenceManager.savePref(Constants.prefDeviceId, generateUUID())
            }
            return preferenceManager.getPref(Constants.prefDeviceId, "")
        }

        fun generateUUID(): String {
            return UUID.randomUUID().toString()
        }

        fun getDeviceName(): String {
            return android.os.Build.MODEL
        }

        fun formatDateToUTC(selectedDate: Long): String {
            val format = SimpleDateFormat(Constants.UTCTimeFormat, Locale.ENGLISH).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val strDate = Date(selectedDate)
            return format.format(strDate)
        }

        fun getDateFromString(date: String): Date? {
            val format = SimpleDateFormat(Constants.UTCTimeFormat, Locale.ENGLISH).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            return format.parse(date)
        }

        fun getOrderDateLong(date: String): Long {
            getDateFromString(date)?.time?.let {
                return it
            }
            return 0
        }

        fun getCurrentDateTime(): String {
            val format = SimpleDateFormat(Constants.UTCTimeFormat, Locale.ENGLISH).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val strDate = Date(System.currentTimeMillis())
            return format.format(strDate)
        }

        fun getFormattedDate(time: Long): String {
            val date = Date(time)
            val format = SimpleDateFormat(Constants.orderDisplayDateFormat, Locale.ENGLISH)
            return format.format(date)
        }

        fun getFormattedDateTime(time: Long): String {
            val date = Date(time)
            val format = SimpleDateFormat(Constants.dateTimeFormat, Locale.ENGLISH)
            return format.format(date)
        }

        fun getStringFromDate(dateFormat: String, date: Date): String {
            val dateFormat = SimpleDateFormat(dateFormat, Locale.ENGLISH)
            return dateFormat.format(date)
        }

        fun getFormattedTime(dateTime: String): String {
            getDateFromString(dateTime)?.apply {
                return getStringFromDate(Constants.orderTimeFormat, this)
            }
            return ""
        }

        fun getOrderJson(productList: MutableList<Product>): String {
            val gson = GsonBuilder().setExclusionStrategies(ProductJsonExclusion()).create()
            Log.d("TAG", "callSyncOrderAPI: product: ${gson.toJson(productList)}")
            return gson.toJson(productList)
        }

        fun getCurrentDateTimeForOrder(): String {
            return SimpleDateFormat(
                Constants.dateTimeSecondFormat, Locale.ENGLISH
            ).format(Date().time)
        }

        fun newDateDifference(currentDate: String, oldDate: String): Int {
            var difference = 0
            try {
                difference = currentDate.toInt() - oldDate.toInt()
                Log.d("newDateDifference", "newDateDifference: $difference")
            } catch (e: Exception) {
                Log.e("newDateDifferenceError", "newDateDifference: ${e.message}")
            }
            return difference
        }

        fun getDateDiff(format: SimpleDateFormat, oldDate: String, newDate: String): Long {
            return try {
                TimeUnit.DAYS.convert(
                    format.parse(newDate)!!.time - format.parse(oldDate)!!.time,
                    TimeUnit.MILLISECONDS
                )
            } catch (e: Exception) {
                e.printStackTrace()
                0
            }
        }


        fun getTranslatedValue(term: String): String {
            var translatedValue = ""
            var defaultValue = ""
            for (languageItem in DigitalDiaryApplication.languageTranslationList) {
                if (term.equals(languageItem.term, ignoreCase = true)) {
                    translatedValue = languageItem.translatedValue
                    defaultValue = languageItem.englishTranslation
                }
            }
            return translatedValue.ifEmpty {
                when (term) {
                    "select_language" -> {
                        "Select Language"
                    }

                    "no_language_found" -> {
                        "No Language Found"
                    }

                    "no_internet" -> {
                        "No Internet"
                    }

                    else -> {
                        defaultValue.ifEmpty {
                            term
                        }
                    }
                }

            }
        }

        fun getMonthAndYear(date: String): String {
            val formattedDate = getDateFromString(date)
            return formattedDate?.let {
                SimpleDateFormat(Constants.monthYearFormat, Locale.ENGLISH).format(
                    it
                )
            } ?: ""
        }

        fun saveLanguageTranslation(it: List<LanguageTranslationData>) {
            DigitalDiaryApplication.languageTranslationList.clear()
            DigitalDiaryApplication.languageTranslationList.addAll(it)
            Log.d("TAG", "getLanguageTranslation: languageTranslationList ${it.size}")
        }

        fun isSingleClick(): Boolean {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                return false
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            return true
        }
    }
}