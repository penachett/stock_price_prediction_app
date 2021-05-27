package com.bmstu.stonksapp.util

import kotlin.math.abs

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

fun formPriceChangeString(startPrice: Double, endPrice: Double, currency: String): String {
    val priceStr = String.format("%.2f", endPrice) + " ${currencySymbolByName(currency)}"
    val diffSign = if (endPrice < startPrice) "-" else "+"
    val diffValue = String.format("%.2f", abs(endPrice-startPrice) /startPrice*100)
    return "$priceStr ($diffSign$diffValue%)"
}
//                ↑↓