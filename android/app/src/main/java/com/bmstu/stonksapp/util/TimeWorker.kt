package com.bmstu.stonksapp.util

import com.bmstu.stonksapp.model.PeriodItem
import java.util.*

private const val DEFAULT_TIME_ZONE = "+03:00"
const val DEFAULT_TIME_ZONE_GMT = "GMT+3"
private val PREDICTION_PERIOD_MONTHS = arrayOf(1, 3, 6)
const val MONTH_LEN = 30
//2021-02-19T12:00+03:00
fun calendarToISO8601(calendar: Calendar): String {
    val year = calendar[Calendar.YEAR]
    val month = numberToTwoCharString(calendar[Calendar.MONTH]+1)
    val day = numberToTwoCharString(calendar[Calendar.DAY_OF_MONTH])
    val hour = numberToTwoCharString(calendar[Calendar.HOUR_OF_DAY])
    val minute = numberToTwoCharString(calendar[Calendar.MINUTE])
    return "$year-$month-${day}T$hour:$minute$DEFAULT_TIME_ZONE"
}

fun Calendar.subYear(): Calendar {
    this.add(Calendar.DAY_OF_YEAR, -365)
    return this
}

private fun numberToTwoCharString(num: Int): String {
    return if (num < 10) "0$num" else "$num"
}

fun getPredictionPeriods(): Array<PeriodItem> {
    val res = Array(PREDICTION_PERIOD_MONTHS.size) { PeriodItem(0) }
    for (i in PREDICTION_PERIOD_MONTHS.indices) {
        res[i] = PeriodItem(PREDICTION_PERIOD_MONTHS[i])
    }
    return res
}

fun timestampToDateString(time: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = time * 1000
    val day = calendar[Calendar.DAY_OF_MONTH]
    val month = calendar[Calendar.MONTH] + 1
    val year = calendar[Calendar.YEAR]
    val dayStr = if (day < 10) "0$day" else day.toString()
    val monthStr = if (month+1 < 10) "0$month" else month.toString()
    return "$dayStr.$monthStr.$year"
}