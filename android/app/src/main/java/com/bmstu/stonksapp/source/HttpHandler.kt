package com.bmstu.stonksapp.source

import com.bmstu.stonksapp.model.ResultWrapper
import com.bmstu.stonksapp.model.tinkoff.http.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class HttpHandler {
    companion object {
        suspend fun <T> apiCall(apiCall: suspend () -> T): ResultWrapper<T> {
            return withContext(Dispatchers.IO) {
                try {
                    ResultWrapper.Success(apiCall.invoke())
                } catch (throwable: Throwable) {
                    when (throwable) {
                        is IOException -> ResultWrapper.NetworkError
                        is HttpException -> {
                            val code = throwable.code()
                            val errorResponse = parseErrorBody(throwable)
                            ResultWrapper.ServerError(code, errorResponse)
                        }
                        else -> {
                            ResultWrapper.ServerError(null, null)
                        }
                    }
                }
            }
        }

        private fun parseErrorBody(throwable: HttpException): ErrorResponse? {
            return try {
                throwable.response()?.errorBody()?.string()?.let {
                    Gson().fromJson(it, ErrorResponse::class.java)
                }
            } catch (exception: Exception) {
                null
            }
        }
    }
}