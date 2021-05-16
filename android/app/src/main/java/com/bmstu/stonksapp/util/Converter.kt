package com.bmstu.stonksapp.util

fun currencySymbolByName(currency: String): Char {
    return when(currency) {
        "RUB" -> '₽'
        "USD" -> '$'
        "EUR" -> '€'
        else -> ' '
    }
}