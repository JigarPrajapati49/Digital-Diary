package com.rayo.digitaldiary.ui.report

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.database.CustomerWithHistory
import com.rayo.digitaldiary.database.OrderDao
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class OrderReportViewModel @Inject constructor(private val orderDao: OrderDao) : BaseViewModel() {

    val calendar: Calendar = Calendar.getInstance()
    private var month = calendar.get(Calendar.MONTH)
    private var year = calendar.get(Calendar.YEAR)
    private var startTime = ""
    private var endTime = ""
    private val dateFormat =
        SimpleDateFormat(Constants.yearFormat, Locale.ENGLISH)
    val orderHistoryList = MutableLiveData<List<CustomerWithHistory>>()

    fun initStartEndDate(fetchOrderType: Int) {
        when (fetchOrderType) {
            Constants.MONTHLY -> {
                setMonthlyStartEndDate()
            }

            Constants.YEARLY -> {
                setYearlyStartEndDate()
            }

            else -> {
                setDailyStartEndDate()
            }
        }
        Log.d("orderReport", "getOrderHistoryFromDB: startTime $startTime endTime $endTime")
        getAllOrderHistoryFromDB()
    }

    private fun getAllOrderHistoryFromDB() {
        Coroutines.ioThenMain({
            orderDao.getCompleteOrders(startTime, endTime)
        }, {
            if (it != null) {
                orderHistoryList.postValue(it)
            }
        })
    }

    fun getDayText(): String {
        return SimpleDateFormat(Constants.orderDisplayDateFormat, Locale.ENGLISH).format(
            calendar.time
        )
    }

    fun getMonthText(): String {
        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH)
            ?.toString() + " " +
                calendar.get(Calendar.YEAR).toString()
    }

    fun getYearText(): String {
        return calendar.get(Calendar.YEAR).toString()
    }

    fun nextDayClick() {
        calendar.add(Calendar.DATE, 1)
        initStartEndDate(Constants.DAILY)
    }

    fun previousDayClick() {
        calendar.add(Calendar.DATE, -1)
        initStartEndDate(Constants.DAILY)
    }

    fun nextMonthClick() {
        month++
        if (month == 12) {
            month = 0
            year++
            calendar.roll(Calendar.YEAR, true)
        }
        calendar.roll(Calendar.MONTH, true)
        initStartEndDate(Constants.MONTHLY)
    }

    fun previousMonthClick() {
        month--
        if (month == -1) {
            month = 11
            year--
            calendar.roll(Calendar.YEAR, false)
        }
        calendar.roll(Calendar.MONTH, false)
        initStartEndDate(Constants.MONTHLY)
    }

    fun nextYearClick() {
        year++
        calendar.roll(Calendar.YEAR, true)
        initStartEndDate(Constants.YEARLY)
    }

    fun previousYearClick() {
        year--
        calendar.roll(Calendar.YEAR, false)
        initStartEndDate(Constants.YEARLY)
    }

    private fun setDailyStartEndDate() {
        startTime = dateFormat.format(calendar.time) + "T00:00:00.000Z"
        endTime = dateFormat.format(calendar.time) + "T23:59:00.000Z"
    }

    private fun setMonthlyStartEndDate() {
        val monthCalendar = Calendar.getInstance()
        monthCalendar.time = calendar.time
        monthCalendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
        startTime = dateFormat.format(monthCalendar.time) + "T00:00:00.000Z"
        monthCalendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        endTime = dateFormat.format(monthCalendar.time) + "T23:59:00.000Z"
    }

    private fun setYearlyStartEndDate() {
        val yearCalendar = Calendar.getInstance()
        yearCalendar.time = calendar.time
        yearCalendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMinimum(Calendar.DAY_OF_YEAR))
        startTime = dateFormat.format(yearCalendar.time) + "T00:00:00.000Z"
        yearCalendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR))
        endTime = dateFormat.format(yearCalendar.time) + "T23:59:00.000Z"
    }
}