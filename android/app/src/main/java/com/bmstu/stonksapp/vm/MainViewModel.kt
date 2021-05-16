package com.bmstu.stonksapp.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmstu.stonksapp.model.ResultWrapper
import com.bmstu.stonksapp.model.tinkoff.http.FullStocksInfoResponse
import com.bmstu.stonksapp.model.tinkoff.http.OrderBook
import com.bmstu.stonksapp.model.tinkoff.http.StockInfo
import com.bmstu.stonksapp.repository.TinkoffRepository
import com.bmstu.stonksapp.source.HttpService
import com.bmstu.stonksapp.source.TinkoffSocketService
import com.bmstu.stonksapp.util.TinkoffLiveDataBundle
import com.bmstu.stonksapp.util.filterStocks
import com.bmstu.stonksapp.util.mergeStocksInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    private lateinit var socketSource: TinkoffSocketService
    private var tinkoffRepository: TinkoffRepository? = null
    private var tinkoffDataBundle: TinkoffLiveDataBundle? = null
    private var token: String? = null //TODO: get token in constructor & make this fields lateinit
    private var stocksList: ArrayList<StockInfo> = ArrayList()
    private var orderBooks: ArrayList<OrderBook> = ArrayList()
    private var orderBooksJob: Job? = null

    fun setToken(token: String) {
        this.token = token
        tinkoffRepository = TinkoffRepository(HttpService.getTinkoffApi(token))
        tinkoffDataBundle = TinkoffLiveDataBundle()
    }

    fun sendTinkoffRegisterRequest() {
        viewModelScope.launch {
            tinkoffRepository?.register()?.let {
                tinkoffDataBundle?.onRegisterResponse(it)
            }
        }
    }

    fun sendHistoryInfoRequest(figi: String, from: String, to: String, interval: String) {
        viewModelScope.launch {
            tinkoffRepository?.getHistoryInfo(figi, from, to, interval)?.let {
                tinkoffDataBundle?.onHistoryInfoResponse(it)
            }
        }
    }

    private fun sendOrderBookRequest(figi: String) {
        viewModelScope.launch {
            tinkoffRepository?.getOrderBook(figi)?.let {
                when (it) {
                    is ResultWrapper.Success -> {
                        addOrderBook(it.value.payload)
                    }
                    is ResultWrapper.NetworkError -> {
                        orderBooksJob?.cancel()
                        tinkoffDataBundle?.onFullStocksInfoResponse(it)
                    }
                    is ResultWrapper.ServerError -> {
                        orderBooksJob?.cancel()
                        tinkoffDataBundle?.onFullStocksInfoResponse(it)
                    }
                }
            }
        }
    }

    fun sendStockListRequest() {
        viewModelScope.launch {
            tinkoffRepository?.getStocksInfo()?.let {
                tinkoffDataBundle?.onStockListResponse(it)
            }
        }
    }

    fun loadOrderBooks() {
        orderBooks = ArrayList()
        orderBooksJob = viewModelScope.launch {
            for (stock in stocksList) {
                sendOrderBookRequest(stock.figi)
            }
        }
    }

    fun getTinkoffRegisterResponses() = tinkoffDataBundle?.registerResponses

    fun getHistoryResponses() = tinkoffDataBundle?.historyInfoResponses

    fun getStockListResponses() = tinkoffDataBundle?.stockListResponses

    fun getStocksFullInfo() = tinkoffDataBundle?.fullStocksInfo

    fun setStocksList(stocks: List<StockInfo>, availableTickers: List<String>) {
        stocksList = filterStocks(availableTickers, stocks)
    }

    private fun addOrderBook(orderBook: OrderBook) {
        orderBooks.add(orderBook)
        if (orderBooks.size == stocksList.size) {
            tinkoffDataBundle?.onFullStocksInfoResponse(
                    ResultWrapper.Success(FullStocksInfoResponse(mergeStocksInfo(stocksList, orderBooks))))
        }
    }

    fun openSocket() {
        token?.let {
            socketSource = TinkoffSocketService(viewModelScope, it)
        }
    }

    fun sendSocketMessage() {
        Log.i(TAG, "sending message")
//        source.sendString("{\"event\": \"orderbook:subscribe\", \"figi\": \"BBG004S68758\", \"depth\": 10}")
        socketSource.sendString("{\"event\": \"instrument_info:subscribe\",\"figi\": \"BBG004S68758\"}")
    }

    companion object {
        const val TAG = "Main ViewModel"
    }
}