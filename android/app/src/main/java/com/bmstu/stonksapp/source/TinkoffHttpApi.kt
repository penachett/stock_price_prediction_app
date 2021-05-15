package com.bmstu.stonksapp.source

import com.bmstu.stonksapp.model.tinkoff.http.HistoryInfoResponse
import com.bmstu.stonksapp.model.tinkoff.http.OrderBookResponse
import com.bmstu.stonksapp.model.tinkoff.http.RegisterResponse
import com.bmstu.stonksapp.model.tinkoff.http.StocksInfoResponse
import retrofit2.http.*

interface TinkoffHttpApi {
    @POST("register")
    suspend fun register(): RegisterResponse

    @GET("market/stocks")
    suspend fun getStocks(): StocksInfoResponse

    @GET("market/orderbook")
    suspend fun getOrderBook(
        @Query("figi") figi: String,
        @Query("depth") depth: Int = 1
    ): OrderBookResponse

    @GET("market/candles")
    suspend fun getHistoryInfo(
        @Query("figi") figi: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("interval") interval: String
    ): HistoryInfoResponse
}