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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.bmstu.stonksapp.R
import com.bmstu.stonksapp.model.ResultWrapper
import com.bmstu.stonksapp.model.tinkoff.http.PredictionWithInfo
import com.bmstu.stonksapp.ui.adapters.PredictionsAdapter
import com.bmstu.stonksapp.util.DialogsWorker
import com.bmstu.stonksapp.vm.MainViewModel

class PredictionsFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var predictions: ArrayList<PredictionWithInfo>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_predictions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        watchPredictionsResponses()
        setLoading(true)
        val list = viewModel.getPredictionsWithInfoResponses().value
        if (list != null && list is ResultWrapper.Success) {
            Log.i(TAG, "predictions list not null")
            predictions = list.value
            setUI()
        } else {
            viewModel.sendPredictionsRequest()
        }
    }

    private fun watchPredictionsResponses() {
        viewModel.getPredictionsWithInfoResponses().observe(viewLifecycleOwner, { response ->
            when (response) {
                is ResultWrapper.Success -> {
                    Log.i(TAG, "success predictions with info response")
                    predictions = response.value
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
    }

    private fun setUI() {
        view?.let { view ->
            predictions?.let { predictions ->
                setLoading(false)
                val emptyCl: ConstraintLayout = view.findViewById(R.id.empty_predictions_cl)
                val list: RecyclerView = view.findViewById(R.id.predictions_list)
                if (predictions.size == 0) {
                    emptyCl.visibility = VISIBLE
                    list.visibility = GONE
                } else {
                    emptyCl.visibility = GONE
                    list.visibility = VISIBLE
                    val adapter = PredictionsAdapter(predictions, requireActivity(), this)
                    list.adapter = adapter
                }
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        view?.let {
            val progressBar: ProgressBar = it.findViewById(R.id.progress_predictions)
            val list: RecyclerView = it.findViewById(R.id.predictions_list)
            progressBar.visibility = if (loading) VISIBLE else GONE
            list.visibility = if (loading) GONE else VISIBLE
        }
    }

    fun onPredictionClicked(prediction: PredictionWithInfo) {

    }

    companion object {
        const val TAG = "Predictions Fragment"
    }
}