@file:JvmName("StocksWorker")
package com.bmstu.stonksapp.util

import com.bmstu.stonksapp.model.tinkoff.http.StockInfo

fun filterStocks(availableTickers: List<String>, allStocks: List<StockInfo>): ArrayList<StockInfo> {
    val res = ArrayList<StockInfo>()
    for (stock in allStocks) {
        if (availableTickers.contains(stock.ticker)) {
            res.add(stock)
        }
        if (availableTickers.size == res.size) {
            break;
        }
    }
    return res
}