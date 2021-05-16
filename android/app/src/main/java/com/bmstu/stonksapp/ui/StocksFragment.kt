package com.bmstu.stonksapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bmstu.stonksapp.R
import com.bmstu.stonksapp.model.ResultWrapper
import com.bmstu.stonksapp.model.tinkoff.http.FullStockInfo
import com.bmstu.stonksapp.ui.adapters.StocksAdapter
import com.bmstu.stonksapp.util.DialogsWorker
import com.bmstu.stonksapp.vm.MainViewModel

class StocksFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var stocksInfo: ArrayList<FullStockInfo>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        getOrLoadStocksInfo()
        return inflater.inflate(R.layout.fragment_stocks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getOrLoadStocksInfo()
    }

    private fun getOrLoadStocksInfo() {
        val info = viewModel.getStocksFullInfo()?.value
        if (info != null && info is ResultWrapper.Success) {
            stocksInfo = info.value.info
            setUI()
        } else {
            viewModel.getStocksFullInfo()?.observe(viewLifecycleOwner, {
                when (it) {
                    is ResultWrapper.Success -> {
                        stocksInfo = it.value.info
                        setUI()
                    }
                    is ResultWrapper.NetworkError -> {
                        setLoading(false)
                        DialogsWorker.showDefaultDialog(
                                childFragmentManager, resources.getString(R.string.network_error_message), TAG)
                    }
                    is ResultWrapper.ServerError -> {
                        setLoading(false)
                        DialogsWorker.showDefaultDialog(
                                childFragmentManager, resources.getString(R.string.default_error_message), TAG)
                    }
                }
            })
            setLoading(true)
            viewModel.loadOrderBooks()
        }
    }

    private fun setUI() {
        view?.let { view ->
            stocksInfo?.let { stocks ->
                setLoading(false)
                val list = view.findViewById<RecyclerView>(R.id.stocks_list)
                val adapter = StocksAdapter(stocks, requireContext(), this)
                Log.i(TAG, "item count: " + adapter.itemCount)
                list.adapter = adapter
            }
        }
    }

    fun onStockClicked(info: FullStockInfo) {
        parentFragment?.parentFragment?.findNavController()?.navigate(R.id.action_to_stock_info_fragment)
    }

    private fun setLoading(isLoading: Boolean) {
        view?.let {
            val list = it.findViewById<RecyclerView>(R.id.stocks_list)
            val progress = it.findViewById<ProgressBar>(R.id.progress_stocks)
            list.visibility = if (isLoading) GONE else VISIBLE
            progress.visibility = if (isLoading) VISIBLE else GONE
        }
    }

    companion object {
        const val TAG = "Stocks Fragment"
    }
}