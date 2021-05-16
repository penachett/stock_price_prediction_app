package com.bmstu.stonksapp.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bmstu.stonksapp.R
import com.bmstu.stonksapp.vm.MainViewModel


class MainActivity : AppCompatActivity() {

    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setStatusBarColor(R.color.black)
        vm.setToken(resources.getString(R.string.tinkoff_api_key))
    }

    private fun openSocket() {
        vm.openSocket()
    }

    fun setStatusBarColor(colorResource: Int) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, colorResource)
    }

    companion object {
        private const val TAG = "Main Activity"
    }
}