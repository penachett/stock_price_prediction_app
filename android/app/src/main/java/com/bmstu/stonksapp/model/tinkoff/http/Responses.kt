package com.bmstu.stonksapp.model.tinkoff.http

import android.os.Parcelable
import com.bmstu.stonksapp.model.stonks.Prediction
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

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
    val candles: ArrayList<HistoryInfo>
)

@Parcelize
data class HistoryInfo(
    val figi: String,
    val interval: String,
    val time: String,
    @SerializedName("o") val open: Double,
    @SerializedName("c") val close: Double,
    @SerializedName("h") val high: Double,
    @SerializedName("l") val low: Double,
    @SerializedName("v") val volume: Double
): Parcelable

data class StocksInfoResponse(
    val trackingId: String,
    val status: String,
    val payload: StocksInfoResponsePayload
)

data class StocksInfoResponsePayload(
    val instruments: List<StockInfo>
)

@Parcelize
data class StockInfo(
    val figi: String,
    val ticker: String,
    val isin: String,
    val minPriceIncrement: Double,
    val currency: String,
    val name: String,
    val type: String
): Parcelable

data class OrderBookResponse(
    val trackingId: String,
    val status: String,
    val payload: OrderBook
)

@Parcelize
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
): Parcelable

@Parcelize
data class OrderBookItem(
    val price: Double,
    val quantity: Int
): Parcelable

@Parcelize
data class FullStockInfo(
    val info: StockInfo,
    val orderBook: OrderBook
): Parcelable

@Parcelize
data class PredictionWithInfo(
    val prediction: Prediction,
    val info: FullStockInfo
): Parcelable

data class FullStocksInfoResponse(
    val info: ArrayList<FullStockInfo>
)