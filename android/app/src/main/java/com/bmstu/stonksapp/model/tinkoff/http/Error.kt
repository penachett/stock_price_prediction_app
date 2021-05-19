package com.bmstu.stonksapp.model.tinkoff.http

data class ErrorResponse(
    val trackingId: String,
    val status: String,
    val payload: ErrorPayload
)

data class ErrorPayload(
    val message: String,
    val code: String
)