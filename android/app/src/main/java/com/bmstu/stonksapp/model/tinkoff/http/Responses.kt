package com.bmstu.stonksapp.model.tinkoff.http

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    val trackingId: String,
    val status: String,
    val payload: RegisterResponsePayload
)

data class RegisterResponsePayload(
    val brokerAccountId: String,
    val brokerAccountType: String
)

data class HistoryInfoResponse(
    val trackingId: String,
    val status: String,
    val payload: HistoryInfoResponsePayload
)

data class HistoryInfoResponsePayload(
    val candles: List<HistoryInfo>
)

data class HistoryInfo(
    val figi: String,
    val interval: String,
    val time: String,
    @SerializedName("o") val open: Double,
    @SerializedName("c") val close: Double,
    @SerializedName("h") val high: Double,
    @SerializedName("l") val low: Double,
    @SerializedName("v") val volume: Double
)

data class StocksInfoResponse(
    val trackingId: String,
    val status: String,
    val payload: StocksInfoResponsePayload
)

data class StocksInfoResponsePayload(
    val instruments: List<StockInfo>
)

data class StockInfo(
    val figi: String,
    val ticker: String,
    val isin: String,
    val minPriceIncrement: Double,
    val currency: String,
    val name: String,
    val type: String
)

data class OrderBookResponse(
    val trackingId: String,
    val status: String,
    val payload: OrderBook
)

data class OrderBook(
    val figi: String,
    val depth: Int,
    val tradeStatus: String,
    val minPriceIncrement: Double,
    val lastPrice: Double,
    val closePrice: Double,
    val limitUp: Double,
    val limitDown: Double,
    val bids: List<OrderBookItem>,
    val asks: List<OrderBookItem>,
)

data class OrderBookItem(
    val price: Double,
    val quantity: Int
)


