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

fun formPriceChangeString(curPrice: Double, predictedPrice: Double, currency: String): String {
    val priceStr = "$predictedPrice ${currencySymbolByName(currency)}"
    val diffSign = if (predictedPrice < curPrice) "-" else "+"
    val diffValue = String.format("%.2f", abs(predictedPrice-curPrice) /curPrice*100)
    return "$priceStr ($diffSign$diffValue%)"
}
//                ↑↓