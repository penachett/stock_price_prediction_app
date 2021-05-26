@file:JvmName("StocksWorker")
package com.bmstu.stonksapp.util

import com.bmstu.stonksapp.model.stonks.Prediction
import com.bmstu.stonksapp.model.tinkoff.http.FullStockInfo
import com.bmstu.stonksapp.model.tinkoff.http.OrderBook
import com.bmstu.stonksapp.model.tinkoff.http.PredictionWithInfo
import com.bmstu.stonksapp.model.tinkoff.http.StockInfo

fun filterStocks(availableTickers: List<String>, allStocks: List<StockInfo>): ArrayList<StockInfo> {
    val res = ArrayList<StockInfo>()
    for (stock in allStocks) {
        if (availableTickers.contains(stock.ticker)) {
            res.add(stock)
        }
        if (availableTickers.size == res.size) {
            break
        }
    }
    return res
}

fun mergeStocksInfo(stocks: ArrayList<StockInfo>, orderBooks: ArrayList<OrderBook>): ArrayList<FullStockInfo> {
    val res = ArrayList<FullStockInfo>()
    for (stock in stocks) {
        for (orderBook in orderBooks) {
            if (orderBook.figi == stock.figi) {
                res.add(FullStockInfo(stock, orderBook))
                break
            }
        }
    }
    return res
}

fun mergePredictionsWithInfo(predictions: ArrayList<Prediction>, infoList: ArrayList<FullStockInfo>): ArrayList<PredictionWithInfo> {
    val res = ArrayList<PredictionWithInfo>()
    for (prediction in predictions) {
        for (info in infoList) {
            if (prediction.ticker == info.info.ticker) {
                res.add(PredictionWithInfo(prediction, info))
                break
            }
        }
    }
    return res
}