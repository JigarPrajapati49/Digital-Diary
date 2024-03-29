package com.rayo.digitaldiary.helper

import android.content.SharedPreferences
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Mittal Varsani on 21/12/21.
 */

@Singleton
class PreferenceManager @Inject constructor(private val pref : SharedPreferences, private val editor: SharedPreferences.Editor) {

    fun delete(key: String) {
        if (pref.contains(key)) {
            editor.remove(key).apply()
        }
    }

    fun <T> getPref(key: String): T? {
        return pref.all[key] as T?
    }

    fun <T> getPref(key: String, defValue: T): T {
        val returnValue = pref.all[key] as T?
        return returnValue ?: defValue
    }

    fun savePref(key: String, value: Any?) {
        val editor = editor

        if (value is Boolean) {
            editor.putBoolean(key, (value as Boolean?)!!)
        } else if (value is Int) {
            editor.putInt(key, (value as Int?)!!)
        } else if (value is Float) {
            editor.putFloat(key, (value as Float?)!!)
        } else if (value is Long) {
            editor.putLong(key, (value as Long?)!!)
        } else if (value is String) {
            editor.putString(key, value as String?)
        } else if (value is Enum<*>) {
            editor.putString(key, value.toString())
        } else if (value != null) {
            throw RuntimeException("Attempting to save non-primitive preference")
        }

        editor.apply()
    }
    fun clearAll(keyList: List<String>) {
        for (keyPref in pref.all.keys) {
            var check = false
            for (keyMy in keyList) {
                if (keyPref == keyMy) {
                    check = true
                    break
                }
            }
            if (!check) {
                Log.e("TAG", "Clear Preference: ------------$keyPref")
                delete(keyPref)
            }
        }
    }
}
