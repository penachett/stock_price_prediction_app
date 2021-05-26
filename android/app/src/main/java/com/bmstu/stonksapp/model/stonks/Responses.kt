package com.bmstu.stonksapp.model.stonks

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class Success (
    val success: Boolean
)

@Parcelize
data class Prediction(
    val id: Long,
    val ticker: String,
    @SerializedName("create_time") val createTime: Long,
    @SerializedName("predict_time") val predictTime: Long,
    @SerializedName("predicted_price") val predictedPrice: Double,
    @SerializedName("start_price") val startPrice: Double
): Parcelable
