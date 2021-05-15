package com.bmstu.stonksapp

import android.app.Application

class StonksApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private lateinit var instance: StonksApp

        fun getInstance(): StonksApp {
            return instance
        }
    }
}