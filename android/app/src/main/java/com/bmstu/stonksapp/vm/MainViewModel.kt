package com.bmstu.stonksapp.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmstu.stonksapp.model.tinkoff.http.StockInfo
import com.bmstu.stonksapp.repository.TinkoffRepository
import com.bmstu.stonksapp.source.HttpService
import com.bmstu.stonksapp.source.TinkoffSocketService
import com.bmstu.stonksapp.util.TinkoffLiveDataBundle
import com.bmstu.stonksapp.util.filterStocks
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private lateinit var socketSource: TinkoffSocketService
    private var tinkoffRepository: TinkoffRepository? = null
    private var tinkoffDataBundle: TinkoffLiveDataBundle? = null
    private var token: String? = null //TODO: get token in constructor & make this fields lateinit
    private var stocksList: List<StockInfo> = ArrayList()

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

    fun sendOrderBookRequest(figi: String) {
        viewModelScope.launch {
            tinkoffRepository?.getOrderBook(figi)?.let {
                tinkoffDataBundle?.onOrderBookResponse(it)
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

    fun getTinkoffRegisterResponses() = tinkoffDataBundle?.registerResponses

    fun getOrderBookResponses() = tinkoffDataBundle?.orderBookResponses

    fun getHistoryResponses() = tinkoffDataBundle?.historyInfoResponses

    fun getStockListResponses() = tinkoffDataBundle?.stockListResponses

    fun setStocksList(stocks: List<StockInfo>, availableTickers: List<String>) {
        stocksList = filterStocks(availableTickers, stocks)
        Log.i("stocks", "" + stocksList)
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