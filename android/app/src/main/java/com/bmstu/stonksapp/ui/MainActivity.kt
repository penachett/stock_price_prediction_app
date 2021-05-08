package com.bmstu.stonksapp.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bmstu.stonksapp.R

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