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
import com.bmstu.stonksapp.model.stonks.Prediction
import com.bmstu.stonksapp.util.DialogsWorker
import com.bmstu.stonksapp.vm.MainViewModel

class PredictionsFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var predictions: ArrayList<Prediction>? = null

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
        val event = viewModel.getPredictionsResponses().value
        if (event != null) {
            Log.i(TAG, "predictions list not null")
            val response = event.peekContent()
            if (response is ResultWrapper.Success) {
                predictions = response.value
                setUI()
            }
        } else {
            viewModel.sendPredictionsRequest()
        }
    }

    private fun watchPredictionsResponses() {
        viewModel.getPredictionsResponses().observe(viewLifecycleOwner, { event ->
            when (val response = event.getContentIfNotWatched()) {
                is ResultWrapper.Success -> {
                    Log.i(TAG, "success predictions response")
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

    companion object {
        const val TAG = "Predictions Fragment"
    }
}