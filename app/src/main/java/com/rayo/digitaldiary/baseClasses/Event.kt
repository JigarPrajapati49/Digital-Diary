package com.rayo.digitaldiary.baseClasses

/**
 * Created by Mittal Varsani on 25/8/20.
 * @author Mittal Varsani
 */
open class Event<out T>(private val content: T?) {
    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

}