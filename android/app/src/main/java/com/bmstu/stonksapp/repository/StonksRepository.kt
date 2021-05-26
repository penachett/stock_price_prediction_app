package com.bmstu.stonksapp.repository

import android.util.Log
import com.bmstu.stonksapp.model.ResultWrapper
import com.bmstu.stonksapp.model.stonks.Prediction
import com.bmstu.stonksapp.model.stonks.Success
import com.bmstu.stonksapp.source.HttpHandler
import com.bmstu.stonksapp.source.StonksAppApi
import com.google.gson.Gson

class StonksRepository(private val service: StonksAppApi) {

    suspend fun register(login: String, password: String): ResultWrapper<Success> {
        return HttpHandler.apiCall { service.register(login, password) }
    }

    suspend fun login(login: String, password: String): ResultWrapper<Success> {
        return HttpHandler.apiCall { service.login(login, password) }
    }

    suspend fun getPredictions(): ResultWrapper<ArrayList<Prediction>> {
        return HttpHandler.apiCall { service.getPredictions() }
    }

    suspend fun makePrediction(ticker: String, prices: List<Double>, predictDays: Int): ResultWrapper<Prediction> {
        return HttpHandler.apiCall { service.makePrediction(Gson().toJson(prices), ticker, predictDays) }
    }

    suspend fun savePrediction(ticker: String, createTime: Long, predictTime: Long,
                               predictedPrice: Double, startPrice: Double): ResultWrapper<Prediction> {
        return HttpHandler.apiCall {
            service.savePrediction(ticker, createTime, predictTime, predictedPrice, startPrice)
        }
    }

    suspend fun deletePrediction(id: Long): ResultWrapper<Success> {
        return HttpHandler.apiCall { service.deletePrediction(id) }
    }

    companion object {
        private const val TAG = "Stonks Repository"
    }
}