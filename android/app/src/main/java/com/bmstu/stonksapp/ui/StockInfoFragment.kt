package com.bmstu.stonksapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bmstu.stonksapp.R
import com.bmstu.stonksapp.model.PeriodItem
import com.bmstu.stonksapp.model.ResultWrapper
import com.bmstu.stonksapp.model.stonks.Prediction
import com.bmstu.stonksapp.model.tinkoff.http.FullStockInfo
import com.bmstu.stonksapp.model.tinkoff.http.HistoryInfo
import com.bmstu.stonksapp.ui.adapters.PeriodsSpinnerAdapter
import com.bmstu.stonksapp.ui.dialogs.ProgressDialog
import com.bmstu.stonksapp.util.*
import com.bmstu.stonksapp.vm.MainViewModel
import java.util.*
import kotlin.math.abs

class StockInfoFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var historyInfo: ArrayList<HistoryInfo>? = null
    private lateinit var info: FullStockInfo
    private val periods: Array<PeriodItem> = getPredictionPeriods()
    private var chosenPeriodMonths: Int = periods[0].monthCount
    private var progressDialog: ProgressDialog? = null
    private var prediction: Prediction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
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
        setLoading(true)
        sendHistoryInfoRequest()
        setupPeriodSpinner(view)
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
        historyInfo?.let { history ->
            view?.findViewById<Button>(R.id.predict_btn)?.apply {
                setOnClickListener {
                    observeMakePredictionResponses()
                    progressDialog?.dismiss()
                    progressDialog = ProgressDialog()
                    progressDialog?.show(childFragmentManager, TAG)
                    viewModel.sendMakePredictionRequest(info.info.ticker,
                            getClosePrices(history), chosenPeriodMonths * MONTH_LEN)
                }
            }
        }
    }

    private fun observeMakePredictionResponses() {
        viewModel.getMakePredictionResponses().observe(viewLifecycleOwner, { event ->
            event.getContentIfNotWatched()?.let { wrapper ->
                progressDialog?.dismiss()
                when (wrapper) {
                    is ResultWrapper.Success -> {
                        prediction = wrapper.value
                        prediction?.startPrice = info.orderBook.lastPrice
                        showPredictionInfo()
                    }
                    is ResultWrapper.NetworkError -> {
                        Log.e(TAG, "err make prediction: $wrapper")
                        DialogsWorker.showDefaultDialog(childFragmentManager,
                                resources.getString(R.string.network_error_message), TAG)
                    }
                    is ResultWrapper.ServerError -> {
                        Log.e(TAG, "err make prediction: $wrapper")
                        DialogsWorker.showDefaultDialog(childFragmentManager,
                                resources.getString(R.string.default_error_message), TAG)
                    }
                }
            }
        })
    }

    private fun showPredictionInfo() {
        view?.let { view ->
            prediction?.let { prediction ->
                val predictionCl = view.findViewById<ConstraintLayout>(R.id.prediction_info_cl)
                val predictBtn = view.findViewById<Button>(R.id.predict_btn)
                val saveBtn = view.findViewById<Button>(R.id.save_prediction_btn)
                val closeImage = view.findViewById<ImageView>(R.id.cancel_prediction_ic)
                val predictionTv = view.findViewById<TextView>(R.id.prediction_tv)
                val priceTv = view.findViewById<TextView>(R.id.predicted_price_tv)
                predictionCl.visibility = VISIBLE
                predictBtn.visibility = GONE
                saveBtn.setOnClickListener {
                    progressDialog?.dismiss()
                    progressDialog = ProgressDialog()
                    progressDialog?.show(childFragmentManager, TAG)
                    observerSavePredictionResponses()
                    viewModel.sendSavePredictionRequest(prediction)
                }
                closeImage.setOnClickListener {
                    hidePredictionInfo()
                }
                predictionTv.text = resources.getString(R.string.predicted_price_for_period,
                        timestampToDateString(prediction.predictTime))
                val curPrice = info.orderBook.lastPrice
                val predictedPrice = prediction.predictedPrice
                priceTv.text = formPriceChangeString(curPrice, predictedPrice, info.info.currency)
                priceTv.setTextColor(ContextCompat.getColor(requireActivity(),
                        if (predictedPrice < curPrice) R.color.red_status else R.color.green_status))
            }
        }
    }

    private fun observerSavePredictionResponses() {
        viewModel.getSavePredictionResponses().observe(viewLifecycleOwner, {event ->
            event.getContentIfNotWatched()?.let { wrapper ->
                progressDialog?.dismiss()
                when (wrapper) {
                    is ResultWrapper.Success -> {
                        prediction = wrapper.value
                        onPredictionSaved()
                    }
                    is ResultWrapper.NetworkError -> {
                        Log.e(TAG, "err save prediction: $wrapper")
                        DialogsWorker.showDefaultDialog(childFragmentManager,
                                resources.getString(R.string.network_error_message), TAG)
                    }
                    is ResultWrapper.ServerError -> {
                        Log.e(TAG, "err save prediction: $wrapper")
                        DialogsWorker.showDefaultDialog(childFragmentManager,
                                resources.getString(R.string.default_error_message), TAG)
                    }
                }
            }
        })
    }

    private fun onPredictionSaved() {
        hidePredictionInfo()
        DialogsWorker.showDefaultDialog(childFragmentManager,
                resources.getString(R.string.predict_saved), TAG)
    }

    private fun hidePredictionInfo() {
        view?.let { view ->
            val predictionCl = view.findViewById<ConstraintLayout>(R.id.prediction_info_cl)
            val predictBtn = view.findViewById<Button>(R.id.predict_btn)
            predictionCl.visibility = GONE
            predictBtn.visibility = VISIBLE
        }
    }

    private fun setupPeriodSpinner(view: View) {
        val adapter = PeriodsSpinnerAdapter(
                requireActivity(), R.layout.item_period_spinner,
                periods)
        adapter.setDropDownViewResource(R.layout.item_period_spinner)
        val spinner = view.findViewById<Spinner>(R.id.period_spinner)
        spinner.adapter = adapter
        spinner.prompt = "Период"
        spinner.setSelection(0)
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                chosenPeriodMonths = periods[position].monthCount
                hidePredictionInfo()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setLoading(loading: Boolean) {
        view?.let {
            val progress = it.findViewById<ProgressBar>(R.id.progress_stock_info)
            val stockInfoCl = it.findViewById<ConstraintLayout>(R.id.stock_info_cl)
            progress.visibility = if (loading) VISIBLE else GONE
            stockInfoCl.visibility = if (loading) GONE else VISIBLE
        }
    }

    companion object {
        private const val TAG = "StockInfo Fragment"
        const val STOCK_INFO_KEY = "stock_info_key"
    }
}