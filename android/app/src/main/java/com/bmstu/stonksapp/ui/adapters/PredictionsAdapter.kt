package com.bmstu.stonksapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bmstu.stonksapp.R
import com.bmstu.stonksapp.model.tinkoff.http.PredictionWithInfo
import com.bmstu.stonksapp.ui.PredictionsFragment
import com.bmstu.stonksapp.util.currencySymbolByName
import java.util.*

class PredictionsAdapter(private val list: ArrayList<PredictionWithInfo>, private val context: Context,
                    private val fragment: Fragment): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class PredictionViewHolder(view: View, clickListener: OnPredictionClickListener): RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.stock_image)
        val name: TextView = view.findViewById(R.id.stock_name_tv)
        val ticker: TextView = view.findViewById(R.id.stock_ticker_tv)
        val price: TextView = view.findViewById(R.id.stock_price_tv)

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
        stockHolder.ticker.text = item.info.info.ticker
        val priceString = "${item.info.orderBook.lastPrice} ${currencySymbolByName(item.info.info.currency)}"
        stockHolder.price.text = priceString
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