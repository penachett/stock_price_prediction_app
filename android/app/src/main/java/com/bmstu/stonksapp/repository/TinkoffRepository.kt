package com.bmstu.stonksapp.repository

import com.bmstu.stonksapp.model.ResultWrapper
import com.bmstu.stonksapp.model.tinkoff.http.HistoryInfoResponse
import com.bmstu.stonksapp.model.tinkoff.http.OrderBookResponse
import com.bmstu.stonksapp.model.tinkoff.http.RegisterResponse
import com.bmstu.stonksapp.model.tinkoff.http.StocksInfoResponse
import com.bmstu.stonksapp.source.HttpHandler
import com.bmstu.stonksapp.source.TinkoffHttpApi

class TinkoffRepository(private val service: TinkoffHttpApi) {

    suspend fun register(): ResultWrapper<RegisterResponse> {
        return HttpHandler.apiCall { service.register() }
    }

    suspend fun getStocksInfo(): ResultWrapper<StocksInfoResponse> {
        return HttpHandler.apiCall { service.getStocks() }
    }

    suspend fun getHistoryInfo(figi: String, from: String, to: String, interval: String = "day"):
            ResultWrapper<HistoryInfoResponse> {
        return HttpHandler.apiCall { service.getHistoryInfo(figi, from, to, interval) }
    }

    suspend fun getOrderBook(figi: String): ResultWrapper<OrderBookResponse> {
        return HttpHandler.apiCall { service.getOrderBook(figi) }
    }
}