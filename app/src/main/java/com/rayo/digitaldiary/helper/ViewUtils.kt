package com.rayo.digitaldiary.helper

import android.content.Context
import android.os.SystemClock
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast

/**
 * Created by Mittal Varsani on 9/2/23.
 * @author Mittal Varsani
 */
fun Context.toast(message: String) {
    Toast.makeText(this, Utils.getTranslatedValue(message), Toast.LENGTH_SHORT).show()
}

fun EditText.requestFocusWithKeyboard() {
    post {
        dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0f, 0f, 0))
        dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0f, 0f, 0))
        setSelection(length())
    }
}