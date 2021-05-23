package com.bmstu.stonksapp.source

import com.bmstu.stonksapp.model.stonks.Prediction
import com.bmstu.stonksapp.model.stonks.Success
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST


interface StonksAppApi {

    @FormUrlEncoded
    @POST("api/register")
    suspend fun register(
        @Field("login") login: String,
        @Field("password") password: String
    ): Success

    @FormUrlEncoded
    @POST("api/login")
    fun login(
        @Field("login") login: String,
        @Field("password") password: String
    ): Success

    @FormUrlEncoded
    @POST("api/make_prediction")
    fun makePrediction(
        @Field("prices") prices: List<Double>,
        @Field("ticker") ticker: String,
        @Field("predict_days") days: Int
    ): Prediction

    @FormUrlEncoded
    @POST("api/save_prediction")
    fun savePrediction(
        @Field("ticker") ticker: String,
        @Field("create_time") createTime: Long,
        @Field("predict_time") predictTime: Long,
        @Field("predicted_price") predictedPrice: Double,
        @Field("start_price") startPrice: Double
    ): Prediction

    @FormUrlEncoded
    @POST("api/delete_prediction")
    fun deletePrediction(
        @Field("prediction_id") id: Long
    ): Success

    @GET("api/get_predictions")
    fun getPredictions() : ArrayList<Prediction>
}