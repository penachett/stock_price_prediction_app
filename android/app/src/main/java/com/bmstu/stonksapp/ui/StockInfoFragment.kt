package com.bmstu.stonksapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.activityViewModels
import com.bmstu.stonksapp.R
import com.bmstu.stonksapp.model.ResultWrapper
import com.bmstu.stonksapp.model.tinkoff.http.FullStockInfo
import com.bmstu.stonksapp.model.tinkoff.http.HistoryInfo
import com.bmstu.stonksapp.util.DEFAULT_TIME_ZONE_GMT
import com.bmstu.stonksapp.util.DialogsWorker
import com.bmstu.stonksapp.util.calendarToISO8601
import com.bmstu.stonksapp.util.subYear
import com.bmstu.stonksapp.vm.MainViewModel
import java.util.*
import kotlin.collections.ArrayList

class StockInfoFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var historyInfo: ArrayList<HistoryInfo>? = null
    private lateinit var info: FullStockInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{ args ->
            args.getParcelable<FullStockInfo>(STOCK_INFO_KEY)?.let {
                info = it
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_stock_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageView>(R.id.order_info_arrow_back).apply {
            setOnClickListener { requireActivity().onBackPressed() }
        }
        observeHistoryInfo()
        sendHistoryInfoRequest()
    }

    private fun sendHistoryInfoRequest() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIME_ZONE_GMT))
        val to = calendarToISO8601(calendar)
        val from = calendarToISO8601(calendar.subYear())
        viewModel.sendHistoryInfoRequest(info.info.figi, from, to, "day")
    }

    private fun observeHistoryInfo() {
        viewModel.getHistoryResponses()?.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotWatched()?.let { wrapper ->
                setLoading(false)
                when (wrapper) {
                    is ResultWrapper.Success -> {
                        historyInfo = wrapper.value.payload.candles
                        setUI()
                    }
                    is ResultWrapper.NetworkError -> {
                        Log.e(TAG, wrapper.toString())
                        DialogsWorker.showDefaultDialog(childFragmentManager,
                            resources.getString(R.string.network_error_message), TAG)
                    }
                    is ResultWrapper.ServerError -> {
                        Log.e(TAG, wrapper.toString())
                        DialogsWorker.showDefaultDialog(childFragmentManager,
                            resources.getString(R.string.default_error_message), TAG)
                    }
                }
            }
        })
    }

    private fun setUI() {

    }

    private fun setLoading(loading: Boolean) {
        view?.let {
            val progress = it.findViewById<ProgressBar>(R.id.progress_stock_info)
            progress.visibility = if (loading) VISIBLE else GONE
        }
    }

    companion object {
        private const val TAG = "StockInfo Fragment"
        const val STOCK_INFO_KEY = "stock_info_key"
    }
}