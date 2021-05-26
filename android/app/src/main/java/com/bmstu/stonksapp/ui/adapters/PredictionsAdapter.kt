package com.bmstu.stonksapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bmstu.stonksapp.R
import com.bmstu.stonksapp.model.tinkoff.http.PredictionWithInfo
import com.bmstu.stonksapp.ui.PredictionsFragment
import com.bmstu.stonksapp.util.currencySymbolByName
import com.bmstu.stonksapp.util.formPriceChangeString
import com.bmstu.stonksapp.util.timestampToDateString
import java.util.*

class PredictionsAdapter(private val list: ArrayList<PredictionWithInfo>, private val context: Context,
                    private val fragment: Fragment): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class PredictionViewHolder(view: View, clickListener: OnPredictionClickListener): RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.stock_image)
        val predictionDate: TextView = view.findViewById(R.id.prediction_date_tv)
        val name: TextView = view.findViewById(R.id.stock_name_tv)
        val predictedPrice: TextView = view.findViewById(R.id.prediction_price_tv)
        val currentPrice: TextView = view.findViewById(R.id.current_price_tv)
        val createDate: TextView = view.findViewById(R.id.prediction_create_date_tv)

        init {
            view.setOnClickListener { clickListener.onPredictionClick(adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_prediction, parent, false)
        return PredictionViewHolder(view, object : OnPredictionClickListener {
            override fun onPredictionClick(position: Int) {
                (fragment as? PredictionsFragment)?.onPredictionClicked(list[position])
            }
        })
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val stockHolder = holder as PredictionViewHolder
        val item  = list[position]
        stockHolder.name.text = item.info.info.name
        stockHolder.predictionDate.text = context.resources
                .getString(R.string.prediction_for_date, timestampToDateString(item.prediction.predictTime))
        stockHolder.predictedPrice.text = formPriceChangeString(
                item.prediction.startPrice, item.prediction.predictedPrice, item.info.info.currency)
        stockHolder.currentPrice.text = formPriceChangeString(
                item.prediction.startPrice, item.info.orderBook.lastPrice, item.info.info.currency)
        if (item.prediction.predictedPrice < item.prediction.startPrice) {
            stockHolder.predictedPrice.setTextColor(ContextCompat.getColor(context, R.color.red_status))
        } else {
            stockHolder.predictedPrice.setTextColor(ContextCompat.getColor(context, R.color.green_status))
        }
        if (item.info.orderBook.lastPrice < item.prediction.startPrice) {
            stockHolder.currentPrice.setTextColor(ContextCompat.getColor(context, R.color.red_status))
        } else {
            stockHolder.currentPrice.setTextColor(ContextCompat.getColor(context, R.color.green_status))
        }
        stockHolder.createDate.text = context.resources
                .getString(R.string.create_date, timestampToDateString(item.prediction.createTime))
        stockHolder.image.setImageResource(context.resources.getIdentifier(
                item.info.info.ticker.toLowerCase(Locale.ENGLISH), "drawable", context.packageName))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnPredictionClickListener {
        fun onPredictionClick(position: Int)
    }

    companion object {
        private const val TAG = "Predictions Adapter"
    }
}