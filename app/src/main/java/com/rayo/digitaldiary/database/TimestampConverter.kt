package com.rayo.digitaldiary.database

import androidx.room.TypeConverter
import com.rayo.digitaldiary.helper.Utils
import java.util.*

/**
 * Created by Mittal Varsani on 29/05/23.
 *
 * @author Mittal Varsani
 */
class TimestampConverter {

    @TypeConverter
    fun fromStringToDate(value: String?): Date? {
        return if (value == null) null else Utils.getDateFromString(value)
    }

    @TypeConverter
    fun fromDateToString(value: Date?): String? {
        return value?.toString()
    }
}

class OrderDateTimeStampConverter {

    @TypeConverter
    fun fromStringToLong(value: String?): Long? {
        return if(value == null) null else Utils.getOrderDateLong(value)
    }

    @TypeConverter
    fun fromLongToString(value: Long?): String? {
        return value?.let { Utils.formatDateToUTC(it) }
    }
}

class OrderAmountConverter {
    @TypeConverter
    fun fromString(value: String): Float {
        return value.toFloat()
    }

    @TypeConverter
    fun toString(value: Float): String {
        return value.toString()
    }
}