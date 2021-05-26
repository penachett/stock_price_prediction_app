package com.bmstu.stonksapp.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bmstu.stonksapp.model.ResultWrapper
import com.bmstu.stonksapp.model.stonks.Prediction
import com.bmstu.stonksapp.model.stonks.Success
import com.bmstu.stonksapp.model.tinkoff.http.FullStocksInfoResponse
import com.bmstu.stonksapp.model.tinkoff.http.PredictionWithInfo

class StonksLiveDataBundle {

    private val _registerResponses: MutableLiveData<Event<ResultWrapper<Success>>> = MutableLiveData()
    val registerResponses: LiveData<Event<ResultWrapper<Success>>>
        get() = _registerResponses

    private val _authResponses: MutableLiveData<Event<ResultWrapper<Success>>> = MutableLiveData()
    val authResponses: LiveData<Event<ResultWrapper<Success>>>
        get() = _authResponses

    private val _makePredictionResponses: MutableLiveData<Event<ResultWrapper<Prediction>>> = MutableLiveData()
    val makePredictionResponses: LiveData<Event<ResultWrapper<Prediction>>>
        get() = _makePredictionResponses

    private val _savePredictionResponses: MutableLiveData<Event<ResultWrapper<Prediction>>> = MutableLiveData()
    val savePredictionResponses: LiveData<Event<ResultWrapper<Prediction>>>
        get() = _savePredictionResponses

    private val _deletePredictionResponses: MutableLiveData<Event<ResultWrapper<Success>>> = MutableLiveData()
    val deletePredictionResponses: LiveData<Event<ResultWrapper<Success>>>
        get() = _deletePredictionResponses

    private val _fullPredictionsInfo: MutableLiveData<ResultWrapper<ArrayList<PredictionWithInfo>>> = MutableLiveData()
    val fullPredictionsInfo: LiveData<ResultWrapper<ArrayList<PredictionWithInfo>>>
        get() = _fullPredictionsInfo

    fun onRegisterResponse(response: ResultWrapper<Success>) {
        _registerResponses.value = Event(response)
    }

    fun onAuthResponse(response: ResultWrapper<Success>) {
        _authResponses.value = Event(response)
    }

    fun onMakePredictionResponse(response: ResultWrapper<Prediction>) {
        _makePredictionResponses.value = Event(response)
    }

    fun onSavePredictionResponse(response: ResultWrapper<Prediction>) {
        _savePredictionResponses.value = Event(response)
    }

    fun onDeletePredictionResponse(response: ResultWrapper<Success>) {
        _deletePredictionResponses.value = Event(response)
    }

    fun onFullPredictionsInfoResponse(info: ResultWrapper<ArrayList<PredictionWithInfo>>) {
        _fullPredictionsInfo.value = info
    }
}