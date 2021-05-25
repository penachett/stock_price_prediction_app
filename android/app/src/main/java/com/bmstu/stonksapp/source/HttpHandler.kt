package com.bmstu.stonksapp.source

import android.util.Log
import com.bmstu.stonksapp.model.ResultWrapper
import com.bmstu.stonksapp.model.tinkoff.http.ErrorPayload
import com.bmstu.stonksapp.model.tinkoff.http.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class HttpHandler {
    companion object {

        const val TAG = "Http Handler"

        suspend fun <T> apiCall(apiCall: suspend () -> T): ResultWrapper<T> {
            return withContext(Dispatchers.IO) {
                try {
                    ResultWrapper.Success(apiCall.invoke())
                } catch (throwable: Throwable) {
                    Log.e(TAG, "" + throwable.message)
                    when (throwable) {
                        is IOException -> ResultWrapper.NetworkError
                        is HttpException -> {
                            val code = throwable.code()
                            val errorMessage = parseErrorBody(throwable)
                            ResultWrapper.ServerError(code, errorMessage)
                        }
                        else -> {
                            ResultWrapper.ServerError(null, null)
                        }
                    }
                }
            }
        }

        private fun parseErrorBody(throwable: HttpException): String? {
            return try {
                throwable.response()?.errorBody()?.string()?.let {
                    Gson().fromJson(it, ErrorResponse::class.java).payload.message
                }
            } catch (exception: Exception) {
                try {
                    throwable.response()?.errorBody()?.string()?.let {
                        Gson().fromJson(it, ErrorPayload::class.java).message
                    }
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
}