package com.bmstu.stonksapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(TAG, resources.getString(R.string.tinkoff_api_key))
    }

    companion object {
        const val TAG = "Main Activity"
    }
}