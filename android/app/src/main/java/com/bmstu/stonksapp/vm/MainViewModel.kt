package com.bmstu.stonksapp.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmstu.stonksapp.model.ResultWrapper
import com.bmstu.stonksapp.model.stonks.Prediction
import com.bmstu.stonksapp.model.tinkoff.http.FullStocksInfoResponse
import com.bmstu.stonksapp.model.tinkoff.http.OrderBook
import com.bmstu.stonksapp.model.tinkoff.http.StockInfo
import com.bmstu.stonksapp.repository.StonksRepository
import com.bmstu.stonksapp.repository.TinkoffRepository
import com.bmstu.stonksapp.source.HttpService
import com.bmstu.stonksapp.source.TinkoffSocketService
import com.bmstu.stonksapp.util.StonksLiveDataBundle
import com.bmstu.stonksapp.util.TinkoffLiveDataBundle
import com.bmstu.stonksapp.util.filterStocks
import com.bmstu.stonksapp.util.mergeStocksInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    //Tinkoff open api fields
    private lateinit var socketSource: TinkoffSocketService
    private var tinkoffRepository: TinkoffRepository? = null
    private var tinkoffDataBundle: TinkoffLiveDataBundle? = null
    private var token: String? = null //TODO: get token in constructor & make this fields lateinit
    private var stocksList: ArrayList<StockInfo> = ArrayList()
    private var orderBooks: ArrayList<OrderBook> = ArrayList()
    private var orderBooksJob: Job? = null

    //Stonks api fields
    private val stonksRepository: StonksRepository = StonksRepository(HttpService.getStonksApi())
    private val stonksDataBundle: StonksLiveDataBundle = StonksLiveDataBundle()

    fun setToken(token: String) {
        this.token = token
        tinkoffRepository = TinkoffRepository(HttpService.getTinkoffApi(token))
        tinkoffDataBundle = TinkoffLiveDataBundle()
    }

    fun sendRegisterRequest(login: String, password: String) {
        viewModelScope.launch {
            stonksRepository.register(login, password).let {
                stonksDataBundle.onRegisterResponse(it)
            }
        }
    }

    fun sendAuthRequest(login: String, password: String) {
        viewModelScope.launch {
            stonksRepository.login(login, password).let {
                stonksDataBundle.onRegisterResponse(it)
            }
        }
    }

    fun sendPredictionsRequest() {
        viewModelScope.launch {
            stonksRepository.getPredictions().let {
                stonksDataBundle.onPredictionsResponse(it)
            }
        }
    }

    fun sendMakePredictionRequest(ticker: String, prices: List<Double>, days: Int) {
        viewModelScope.launch {
            stonksRepository.makePrediction(ticker, prices, days).let {
                stonksDataBundle.onMakePredictionResponse(it)
            }
        }
    }

    fun sendSavePredictionRequest(prediction: Prediction) {
        viewModelScope.launch {
            stonksRepository.savePrediction(
                    prediction.ticker,
                    prediction.createTime,
                    prediction.predictTime,
                    prediction.predictedPrice,
                    prediction.startPrice).let {
                stonksDataBundle.onSavePredictionResponse(it)
            }
        }
    }

    fun sendDeletePredictionRequest(id: Long) {
        viewModelScope.launch {
            stonksRepository.deletePrediction(id).let {
                stonksDataBundle.onDeletePredictionResponse(it)
            }
        }
    }


    fun sendTinkoffRegisterRequest() {
        viewModelScope.launch {
            tinkoffRepository?.register()?.let {
                tinkoffDataBundle?.onRegisterResponse(it)
            }
        }
    }

    fun sendHistoryInfoRequest(figi: String, from: String, to: String, interval: String = "day") {
        viewModelScope.launch {
            tinkoffRepository?.getHistoryInfo(figi, from, to, interval)?.let {
                tinkoffDataBundle?.onHistoryInfoResponse(it)
            }
        }
    }

    fun loadOrderBooks() {
        orderBooksJob?.cancel()
        orderBooks = ArrayList()
        orderBooksJob = viewModelScope.launch {
            for (stock in stocksList) {
                sendOrderBookRequest(stock.figi)
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
        private const val TAG = "Main ViewModel"
    }
}