package com.bmstu.stonksapp.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bmstu.stonksapp.model.ResultWrapper
import com.bmstu.stonksapp.model.tinkoff.http.*

class TinkoffLiveDataBundle {
    private val _registerResponses: MutableLiveData<Event<ResultWrapper<RegisterResponse>>> = MutableLiveData()
    val registerResponses: LiveData<Event<ResultWrapper<RegisterResponse>>>
        get() = _registerResponses

    private val _historyInfoResponses: MutableLiveData<Event<ResultWrapper<HistoryInfoResponse>>> = MutableLiveData()
    val historyInfoResponses: LiveData<Event<ResultWrapper<HistoryInfoResponse>>>
        get() = _historyInfoResponses

    private val _stockListResponses: MutableLiveData<Event<ResultWrapper<StocksInfoResponse>>> = MutableLiveData()
    val stockListResponses: LiveData<Event<ResultWrapper<StocksInfoResponse>>>
        get() = _stockListResponses

    private val _fullStocksInfo: MutableLiveData<ResultWrapper<FullStocksInfoResponse>> = MutableLiveData()
    val fullStocksInfo: LiveData<ResultWrapper<FullStocksInfoResponse>>
        get() = _fullStocksInfo

    fun onRegisterResponse(response: ResultWrapper<RegisterResponse>) {
        _registerResponses.value = Event(response)
    }

    fun onHistoryInfoResponse(response: ResultWrapper<HistoryInfoResponse>) {
        _historyInfoResponses.value = Event(response)
    }

    fun onStockListResponse(response: ResultWrapper<StocksInfoResponse>) {
        _stockListResponses.value = Event(response)
    }

    fun onFullStocksInfoResponse(info: ResultWrapper<FullStocksInfoResponse>) {
        _fullStocksInfo.value = info
    }
}