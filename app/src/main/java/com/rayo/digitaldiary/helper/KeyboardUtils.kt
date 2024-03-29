package com.rayo.digitaldiary.helper

import android.graphics.Rect
import android.util.Log
import android.view.ViewGroup

open class KeyboardUtils(val view: ViewGroup) {

    private var isKeyboardShowing = false
    open fun onKeyboardVisibilityChanged(opened: Boolean) {
        print("keyboard $opened")
    }

    init {

        view.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            view.getWindowVisibleDisplayFrame(r)
            val screenHeight = view.rootView.height

            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            val keypadHeight = screenHeight - r.bottom

            Log.d("KeyBoardUtils", "keypadHeight = $keypadHeight")

            if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                // keyboard is opened
                if (!isKeyboardShowing) {
                    isKeyboardShowing = true
                    onKeyboardVisibilityChanged(true)
                }
            } else {
                // keyboard is closed
                if (isKeyboardShowing) {
                    isKeyboardShowing = false
                    onKeyboardVisibilityChanged(false)
                }
            }
        }
    }
}