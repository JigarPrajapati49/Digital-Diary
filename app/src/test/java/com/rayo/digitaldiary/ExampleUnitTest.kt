package com.rayo.digitaldiary

import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun dateDiff() {
        val lastDate: String = "2023-09-24T05:13:36.111Z"
        lastDate.let {
            val currentDateTime = Utils.getCurrentDateTime()
            val dateDifference = lastDate.let {
                Utils.getDateDiff(
                    SimpleDateFormat(Constants.UTCTimeFormat), it, currentDateTime
                )
            }

            val result = dateDifference.toFloat() / 365

            if (result > 0.9) {
                val res = result.roundToInt()
                if (res > 1) {
                    println("$res years Ago")
                } else {
                    println("$res year Ago")
                }
            } else if (result < 0.9 && result > 0.1) {
                val res = (result * 10).roundToInt()
                if (res > 1) {
                    println("$res months ago")
                } else {
                    println("$res month ago")
                }
            } else if (result == 0.0.toFloat()) {
                println("Today")
            }
            when (dateDifference) {
                1L -> {
                    println("Yesterday")
                }

                in 2L..6L -> {
                    println("$dateDifference days ago")
                }

                in 7L..14L -> {
                    println("two week ago")
                }

                in 15L..21L -> {
                    println("three weeks ago")
                }

                in 22L..31L -> {
                    println("four weeks ago")
                }

                in 32L..36L -> {
                    println("one_month_ago")
                }
            }
        }
    }

    @Test
    fun getPageCount() {
        val listSize = 11
        val recordPerPage = 10
        val page = listSize / recordPerPage.toDouble()
        println("page count ${ceil(page).toInt()}")
    }
}