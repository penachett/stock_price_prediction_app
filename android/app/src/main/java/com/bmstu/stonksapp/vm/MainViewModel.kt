package com.bmstu.stonksapp.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmstu.stonksapp.source.TinkoffSource

class MainViewModel : ViewModel() {

    private lateinit var source: TinkoffSource

    fun openSocket(token: String) {
        source = TinkoffSource(viewModelScope, token)
    }

    fun sendMessage() {
        Log.i(TAG, "sending message")
        source.sendString("kekas")
    }

    companion object {
        const val TAG = "Main ViewModel"
    }
}