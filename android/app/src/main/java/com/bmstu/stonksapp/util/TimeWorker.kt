package com.bmstu.stonksapp.util

import java.util.*

private const val DEFAULT_TIME_ZONE = "+03:00"
const val DEFAULT_TIME_ZONE_GMT = "GMT+3"
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