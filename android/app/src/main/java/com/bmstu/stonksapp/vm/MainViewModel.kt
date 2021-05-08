package com.bmstu.stonksapp.vm

import android.util.Log
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    fun test() {
        Log.i(TAG, "test")
    }

    companion object {
        const val TAG = "Main ViewModel"
    }
}