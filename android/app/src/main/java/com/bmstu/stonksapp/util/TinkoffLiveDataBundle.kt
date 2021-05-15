package com.bmstu.stonksapp.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bmstu.stonksapp.model.ResultWrapper
import com.bmstu.stonksapp.model.tinkoff.http.HistoryInfoResponse
import com.bmstu.stonksapp.model.tinkoff.http.OrderBookResponse
import com.bmstu.stonksapp.model.tinkoff.http.RegisterResponse
import com.bmstu.stonksapp.model.tinkoff.http.StocksInfoResponse

class TinkoffLiveDataBundle {
    private val _registerResponses: MutableLiveData<Event<ResultWrapper<RegisterResponse>>> = MutableLiveData()
    val registerResponses: LiveData<Event<ResultWrapper<RegisterResponse>>>
        get() = _registerResponses

    private val _orderBookResponses: MutableLiveData<Event<ResultWrapper<OrderBookResponse>>> = MutableLiveData()
    val orderBookResponses: LiveData<Event<ResultWrapper<OrderBookResponse>>>
        get() = _orderBookResponses

    private val _historyInfoResponses: MutableLiveData<Event<ResultWrapper<HistoryInfoResponse>>> = MutableLiveData()
    val historyInfoResponses: LiveData<Event<ResultWrapper<HistoryInfoResponse>>>
        get() = _historyInfoResponses

    private val _stockListResponses: MutableLiveData<Event<ResultWrapper<StocksInfoResponse>>> = MutableLiveData()
    val stockListResponses: LiveData<Event<ResultWrapper<StocksInfoResponse>>>
        get() = _stockListResponses


    fun onRegisterResponse(response: ResultWrapper<RegisterResponse>) {
        _registerResponses.value = Event(response)
    }

    fun onOrderBookResponse(response: ResultWrapper<OrderBookResponse>) {
        _orderBookResponses.value = Event(response)
    }

    fun onHistoryInfoResponse(response: ResultWrapper<HistoryInfoResponse>) {
        _historyInfoResponses.value = Event(response)
    }

    fun onStockListResponse(response: ResultWrapper<StocksInfoResponse>) {
        _stockListResponses.value = Event(response)
    }
}