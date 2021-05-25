package com.bmstu.stonksapp.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bmstu.stonksapp.R
import com.bmstu.stonksapp.model.tinkoff.http.FullStockInfo
import com.bmstu.stonksapp.ui.StocksFragment
import com.bmstu.stonksapp.util.currencySymbolByName
import com.google.android.material.tabs.TabLayout
import java.util.*


class StocksAdapter(private val list: ArrayList<FullStockInfo>, private val context: Context,
                    private val fragment: Fragment): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class StockViewHolder(view: View, clickListener: OnStockClickListener): RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.stock_image)
        val name: TextView = view.findViewById(R.id.stock_name_tv)
        val ticker: TextView = view.findViewById(R.id.stock_ticker_tv)
        val price: TextView = view.findViewById(R.id.stock_price_tv)

        init {
            view.setOnClickListener { clickListener.onStockClick(adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_stock, parent, false)
        return StockViewHolder(view, object : OnStockClickListener {
            override fun onStockClick(position: Int) {
                (fragment as? StocksFragment)?.onStockClicked(list[position])
            }
        })
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val stockHolder = holder as StockViewHolder
        val item  = list[position]
        stockHolder.name.text = item.info.name
        stockHolder.ticker.text = item.info.ticker
        val priceString = "${item.orderBook.lastPrice} ${currencySymbolByName(item.info.currency)}"
        stockHolder.price.text = priceString
        stockHolder.image.setImageResource(context.resources.getIdentifier(
                item.info.ticker.toLowerCase(Locale.ENGLISH), "drawable", context.packageName))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnStockClickListener {
        fun onStockClick(position: Int)
    }

    companion object {
        private const val TAG = "Stocks Adapter"
    }
}