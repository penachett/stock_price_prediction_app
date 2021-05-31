package com.bmstu.stonksapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bmstu.stonksapp.R
import com.bmstu.stonksapp.model.ResultWrapper
import com.bmstu.stonksapp.model.tinkoff.http.HistoryInfo
import com.bmstu.stonksapp.model.tinkoff.http.PredictionWithInfo
import com.bmstu.stonksapp.ui.dialogs.ProgressDialog
import com.bmstu.stonksapp.util.*
import com.bmstu.stonksapp.vm.MainViewModel
import java.util.*

class PredictionInfoFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var historyInfo: ArrayList<HistoryInfo>? = null
    private lateinit var prediction: PredictionWithInfo
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            args.getParcelable<PredictionWithInfo>(PREDICTION_INFO_KEY)?.let {
                prediction = it
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_prediction_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageView>(R.id.prediction_info_arrow_back).apply {
            setOnClickListener { requireActivity().onBackPressed() }
        }
        observeHistoryInfo()
        setLoading(true)
        sendHistoryInfoRequest()
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
                        Log.e(TAG, "err history info: $wrapper")
                        DialogsWorker.showDefaultDialog(childFragmentManager,
                                resources.getString(R.string.network_error_message), TAG)
                    }
                    is ResultWrapper.ServerError -> {
                        Log.e(TAG, "err history info: $wrapper")
                        DialogsWorker.showDefaultDialog(childFragmentManager,
                                resources.getString(R.string.default_error_message), TAG)
                    }
                }
            }
        })
    }

    private fun setUI() {
        view?.let {
            val stockImage = it.findViewById<ImageView>(R.id.prediction_stock_image)
            val nameTv = it.findViewById<TextView>(R.id.prediction_stock_name_tv)
            val predictedInfoTv = it.findViewById<TextView>(R.id.predicted_price_with_date_tv)
            val startInfoTv = it.findViewById<TextView>(R.id.start_price_with_date_tv)
            val predictedTv = it.findViewById<TextView>(R.id.predicted_price_tv)
            val currentTv = it.findViewById<TextView>(R.id.current_price_tv)
            val startTv = it.findViewById<TextView>(R.id.start_price_tv)
            val startPrice = prediction.prediction.startPrice
            val predictedPrice = prediction.prediction.predictedPrice
            val lastPrice = prediction.info.orderBook.lastPrice
            startTv.text = formPriceString(startPrice, prediction.info.info.currency)
            predictedInfoTv.text = resources.getString(
                    R.string.prediction_for_date, timestampToDateString(prediction.prediction.predictTime))
            startInfoTv.text = resources.getString(
                    R.string.price_for_date, timestampToDateString(prediction.prediction.createTime))
            predictedTv.text = formPriceChangeString(
                    startPrice, predictedPrice, prediction.info.info.currency)
            currentTv.text = formPriceChangeString(
                    startPrice, lastPrice, prediction.info.info.currency)
            if (predictedPrice < startPrice) {
                predictedTv.setTextColor(ContextCompat.getColor(requireActivity(), R.color.red_status))
            } else {
                predictedTv.setTextColor(ContextCompat.getColor(requireActivity(), R.color.green_status))
            }
            if (lastPrice < startPrice) {
                currentTv.setTextColor(ContextCompat.getColor(requireActivity(), R.color.red_status))
            } else {
                currentTv.setTextColor(ContextCompat.getColor(requireActivity(), R.color.green_status))
            }
            stockImage.setImageResource(resources.getIdentifier(
                    prediction.info.info.ticker.toLowerCase(Locale.ENGLISH), "drawable", requireActivity().packageName))
            nameTv.text = prediction.info.info.name

            it.findViewById<Button>(R.id.delete_btn).setOnClickListener {
                observeDeleteResponses()
                progressDialog?.dismiss()
                progressDialog = ProgressDialog()
                progressDialog?.show(childFragmentManager, TAG)
                viewModel.sendDeletePredictionRequest(prediction.prediction.id)
            }
        }
    }

    private fun observeDeleteResponses() {
        viewModel.getDeletePredictionResponses().observe(viewLifecycleOwner, {
            it.getContentIfNotWatched()?.let { wrapper ->
                progressDialog?.dismiss()
                when (wrapper) {
                    is ResultWrapper.Success -> {
                        requireActivity().onBackPressed()
                    }
                    is ResultWrapper.NetworkError -> {
                        Log.e(TAG, "err delete prediction: $wrapper")
                        DialogsWorker.showDefaultDialog(childFragmentManager,
                                resources.getString(R.string.network_error_message), TAG)
                    }
                    is ResultWrapper.ServerError -> {
                        Log.e(TAG, "err delete prediction: $wrapper")
                        DialogsWorker.showDefaultDialog(childFragmentManager,
                                resources.getString(R.string.default_error_message), TAG)
                    }
                }
            }
        })
    }

    private fun sendHistoryInfoRequest() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIME_ZONE_GMT))
        val to = calendarToISO8601(calendar)
        val from = calendarToISO8601(calendar.subYear())
        viewModel.sendHistoryInfoRequest(prediction.info.info.figi, from, to, "day")
    }

    private fun setLoading(loading: Boolean) {
        view?.let {
            val progress = it.findViewById<ProgressBar>(R.id.progress_prediction_info)
            val stockInfoCl = it.findViewById<ConstraintLayout>(R.id.prediction_info_cl)
            progress.visibility = if (loading) View.VISIBLE else View.GONE
            stockInfoCl.visibility = if (loading) View.GONE else View.VISIBLE
        }
    }

    companion object {
        const val TAG = "PredictionInfo Fragment"
        const val PREDICTION_INFO_KEY = "prediction_info"
    }
}