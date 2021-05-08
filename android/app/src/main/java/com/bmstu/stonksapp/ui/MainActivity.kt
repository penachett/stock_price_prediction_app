package com.bmstu.stonksapp.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bmstu.stonksapp.R
import com.bmstu.stonksapp.vm.MainViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(TAG, resources.getString(R.string.tinkoff_api_key))
        val model: MainViewModel by viewModels()
        model.test()
    }

    companion object {
        const val TAG = "Main Activity"
    }
}