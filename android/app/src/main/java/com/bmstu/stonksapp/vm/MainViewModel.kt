package com.bmstu.stonksapp.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmstu.stonksapp.source.TinkoffSocketService

class MainViewModel : ViewModel() {

    private lateinit var socketSource: TinkoffSocketService
    private var token: String? = null

    fun setToken(token: String) {
        this.token = token
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