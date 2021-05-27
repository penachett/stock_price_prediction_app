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
    suspend fun login(
        @Field("login") login: String,
        @Field("password") password: String
    ): Success

    @FormUrlEncoded
    @POST("api/make_prediction")
    suspend fun makePrediction(
        @Field("prices") prices: String, // json string of prices list
        @Field("ticker") ticker: String,
        @Field("predict_months") months: Int
    ): Prediction

    @FormUrlEncoded
    @POST("api/save_prediction")
    suspend fun savePrediction(
        @Field("ticker") ticker: String,
        @Field("create_time") createTime: Long,
        @Field("predict_time") predictTime: Long,
        @Field("predicted_price") predictedPrice: Double,
        @Field("start_price") startPrice: Double
    ): Prediction

    @FormUrlEncoded
    @POST("api/delete_prediction")
    suspend fun deletePrediction(
        @Field("prediction_id") id: Long
    ): Success

    @GET("api/get_predictions")
    suspend fun getPredictions() : ArrayList<Prediction>
}