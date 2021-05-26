package com.bmstu.stonksapp.util

fun currencySymbolByName(currency: String): Char {
    return when(currency) {
        "RUB" -> '₽'
        "USD" -> '$'
        "EUR" -> '€'
        else -> ' '
    }
}

fun monthStringByCount(month: Int): String {
    return when (month) {
        1 -> "месяц"
        2,3,4 -> "месяца"
        else -> "месяцев"
    }
}