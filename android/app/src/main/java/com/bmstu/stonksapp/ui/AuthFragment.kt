package com.bmstu.stonksapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bmstu.stonksapp.R
import com.bmstu.stonksapp.model.ResultWrapper
import com.bmstu.stonksapp.ui.dialogs.ProgressDialog
import com.bmstu.stonksapp.util.DialogsWorker
import com.bmstu.stonksapp.vm.MainViewModel

class AuthFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var progressDialog: ProgressDialog? = null
    private var registered = false
    private var stocksLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_auth, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toRegisterTv: TextView = view.findViewById(R.id.to_register_tv)
        val btnMain: Button = view.findViewById(R.id.auth_btn)
        toRegisterTv.setOnClickListener {
            findNavController().navigate(R.id.action_to_register_fragment)
        }
        btnMain.setOnClickListener {
            progressDialog = ProgressDialog()
            progressDialog?.show(childFragmentManager, TAG)
            viewModel.getTinkoffRegisterResponses()?.observe(viewLifecycleOwner, {
                val response = it.getContentIfNotWatched()
                response?.let {
                    when (response) {
                        is ResultWrapper.Success -> {
                            Log.i(TAG, "success  register response: " + response.value)
                            registered = true
                            if (stocksLoaded) {
                                findNavController().navigate(R.id.action_to_main_fragment)
                            }
                        }
                        is ResultWrapper.NetworkError -> {
                            progressDialog?.dismiss()
                            DialogsWorker.showDefaultDialog(
                                    childFragmentManager, resources.getString(R.string.network_error_message), TAG)
                        }
                        is ResultWrapper.ServerError -> {
                            progressDialog?.dismiss()
                            DialogsWorker.showDefaultDialog(
                                    childFragmentManager, resources.getString(R.string.default_error_message), TAG)
                        }
                    }
                }
            })
            viewModel.getStockListResponses()?.observe(viewLifecycleOwner, {
                val response = it.peekContent()
                response.let {
                    when (response) {
                        is ResultWrapper.Success -> {
                            Log.i(TAG, "success  stocks response")
                            stocksLoaded = true
                            viewModel.setStocksList(response.value.payload.instruments,
                                    resources.getStringArray(R.array.available_tickers).asList())
                            if (registered) {
                                progressDialog?.dismiss()
                                findNavController().navigate(R.id.action_to_main_fragment)
                            }
                        }
                        is ResultWrapper.NetworkError -> {
                            progressDialog?.dismiss()
                            DialogsWorker.showDefaultDialog(
                                    childFragmentManager, resources.getString(R.string.network_error_message), TAG)
                        }
                        is ResultWrapper.ServerError -> {
                            progressDialog?.dismiss()
                            DialogsWorker.showDefaultDialog(
                                    childFragmentManager, resources.getString(R.string.default_error_message), TAG)
                        }
                    }
                }
            })
            viewModel.sendTinkoffRegisterRequest()
            viewModel.sendStockListRequest()
        }
    }

    companion object {
        const val TAG = "Auth Fragment"
    }
}