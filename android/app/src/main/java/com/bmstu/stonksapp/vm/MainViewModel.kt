package com.bmstu.stonksapp.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmstu.stonksapp.model.ResultWrapper
import com.bmstu.stonksapp.model.stonks.Prediction
import com.bmstu.stonksapp.model.stonks.Success
import com.bmstu.stonksapp.model.tinkoff.http.FullStocksInfoResponse
import com.bmstu.stonksapp.model.tinkoff.http.OrderBook
import com.bmstu.stonksapp.model.tinkoff.http.StockInfo
import com.bmstu.stonksapp.repository.StonksRepository
import com.bmstu.stonksapp.repository.TinkoffRepository
import com.bmstu.stonksapp.source.HttpService
import com.bmstu.stonksapp.source.TinkoffSocketService
import com.bmstu.stonksapp.util.*
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
    private var predictions: ArrayList<Prediction>? = null

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
                stonksDataBundle.onAuthResponse(it)
            }
        }
    }

    fun sendPredictionsRequest() {
        viewModelScope.launch {
            stonksRepository.getPredictions().let {
                when (it) {
                    is ResultWrapper.Success -> {
                        predictions = it.value
                        val info = getStocksFullInfo()?.value
                        if (info != null && info is ResultWrapper.Success) {
                            stonksDataBundle.onFullPredictionsInfoResponse(
                                    ResultWrapper.Success(mergePredictionsWithInfo(predictions!!, info.value.info)))
                        }
                    }
                    is ResultWrapper.NetworkError -> {
                        stonksDataBundle.onFullPredictionsInfoResponse(it)
                    }
                    is ResultWrapper.ServerError -> {
                        stonksDataBundle.onFullPredictionsInfoResponse(it)
                    }
                }
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
                if (it is ResultWrapper.Success) {
                    tinkoffDataBundle?.fullStocksInfo?.value?.let { infoWrapper ->
                        if (infoWrapper is ResultWrapper.Success) {
                            val newPredictionWithInfo = mergePredictionWithInfo(it.value, infoWrapper.value.info)
                            newPredictionWithInfo?.let { prediction ->
                                stonksDataBundle.fullPredictionsInfo.value?.let { predictionsWrapper ->
                                    if (predictionsWrapper is ResultWrapper.Success) {
                                        val predictions = predictionsWrapper.value
                                        predictions.add(0, prediction)
                                        stonksDataBundle.onFullPredictionsInfoResponse(
                                                ResultWrapper.Success(predictions))
                                    }
                                }
                            }
                        }
                    }
                }
                stonksDataBundle.onSavePredictionResponse(it)
            }
        }
    }

    fun sendDeletePredictionRequest(id: Long) {
        viewModelScope.launch {
            stonksRepository.deletePrediction(id).let {
                if (it is ResultWrapper.Success) {
                    stonksDataBundle.fullPredictionsInfo.value?.let { predictionsWrapper ->
                        if (predictionsWrapper is ResultWrapper.Success) {
                            val predictions = predictionsWrapper.value
                            for (i in predictions.indices) {
                                if (predictions[i].prediction.id == id) {
                                    predictions.removeAt(i)
                                    break
                                }
                            }
                            stonksDataBundle.onFullPredictionsInfoResponse(
                                    ResultWrapper.Success(predictions))
                        }
                    }
                }
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

    fun getRegisterResponses() = stonksDataBundle.registerResponses

    fun getAuthResponses() = stonksDataBundle.authResponses

    fun getPredictionsWithInfoResponses() = stonksDataBundle.fullPredictionsInfo

    fun getMakePredictionResponses() = stonksDataBundle.makePredictionResponses

    fun getSavePredictionResponses() = stonksDataBundle.savePredictionResponses

    fun getDeletePredictionResponses() = stonksDataBundle.deletePredictionResponses


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
            val fullList = mergeStocksInfo(stocksList, orderBooks)
            tinkoffDataBundle?.onFullStocksInfoResponse(
                    ResultWrapper.Success(FullStocksInfoResponse(fullList)))
            predictions?.let {
                stonksDataBundle.onFullPredictionsInfoResponse(
                        ResultWrapper.Success(mergePredictionsWithInfo(predictions!!, fullList)))
            }
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