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
import com.bmstu.stonksapp.util.ERR_USER_NOT_EXIST
import com.bmstu.stonksapp.util.SharedPrefs
import com.bmstu.stonksapp.util.errToMessage
import com.bmstu.stonksapp.vm.MainViewModel
import com.google.android.material.textfield.TextInputEditText

class AuthFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var progressDialog: ProgressDialog? = null
    private var authorized = false
    private var stocksLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getBoolean(IS_REGISTERED_KEY, false)?.let { authorized = it }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toRegisterTv: TextView = view.findViewById(R.id.to_register_tv)
        val btnMain: Button = view.findViewById(R.id.auth_btn)
        toRegisterTv.setOnClickListener {
            findNavController().navigate(R.id.action_to_register_fragment)
        }
        val loginEt: TextInputEditText = view.findViewById(R.id.login_et)
        val passwordEt: TextInputEditText = view.findViewById(R.id.password_et)
        val prefs = SharedPrefs(requireActivity())
        if (!prefs.isEmpty()) {
            loginEt.setText(prefs.login)
            passwordEt.setText(prefs.password)
        }
        if (prefs.isAutoAuthEnabled) {
            auth(prefs.login, prefs.password)
        }
        btnMain.setOnClickListener {
            auth(loginEt.text.toString(), passwordEt.text.toString())
        }
    }

    private fun auth(login: String, password: String) {
        if (login.length < MIN_LOGIN_LENGTH) {
            DialogsWorker.showDefaultDialog(
                    childFragmentManager, resources.getString(R.string.login_too_short), TAG)
            return
        }
        if (password.length < MIN_PASSWORD_LENGTH) {
            DialogsWorker.showDefaultDialog(
                    childFragmentManager, resources.getString(R.string.password_too_short), TAG)
            return
        }
        progressDialog = ProgressDialog()
        progressDialog?.show(childFragmentManager, TAG)
        viewModel.getStockListResponses()?.value?.let {
            Log.i(TAG, "stocks list not null")
            val response = it.peekContent()
            if (response is ResultWrapper.Success) {
                Log.i(TAG, "already have success stocks response")
                stocksLoaded = true
                viewModel.setStocksList(response.value.payload.instruments,
                        resources.getStringArray(R.array.available_tickers).asList())
                viewModel.loadOrderBooks()
                if (authorized) {
                    toMainFragment()
                }
            }
        }
        if (!stocksLoaded) {
            loadStocksList()
        }
        if (!authorized) {
            login(login, password)
        }
    }

    private fun login(login: String, password: String) {
        viewModel.getAuthResponses().observe(viewLifecycleOwner, {
            val response = it.getContentIfNotWatched()
            response?.let {
                when (response) {
                    is ResultWrapper.Success -> {
                        Log.i(TAG, "success auth response")
                        authorized = true
                        val prefs = SharedPrefs(requireActivity())
                        prefs.saveAuthData(login, password)
                        prefs.setAutoAuth(true)
                        if (stocksLoaded) {
                            progressDialog?.dismiss()
                            toMainFragment()
                        }
                    }
                    is ResultWrapper.NetworkError -> {
                        progressDialog?.dismiss()
                        DialogsWorker.showDefaultDialog(
                                childFragmentManager, resources.getString(R.string.network_error_message), TAG)
                    }
                    is ResultWrapper.ServerError -> {
                        progressDialog?.dismiss()
                        DialogsWorker.showDefaultDialog(childFragmentManager, errToMessage(response.message), TAG)
                    }
                }
            }
        })
        viewModel.sendAuthRequest(login, password)
    }

    private fun loadStocksList() {
        viewModel.getStockListResponses()?.observe(viewLifecycleOwner, {
            val response = it.peekContent()
            response.let {
                when (response) {
                    is ResultWrapper.Success -> {
                        Log.i(TAG, "success stocks response")
                        stocksLoaded = true
                        viewModel.setStocksList(response.value.payload.instruments,
                                resources.getStringArray(R.array.available_tickers).asList())
                        viewModel.loadOrderBooks()
                        if (authorized) {
                            toMainFragment()
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

    private fun toMainFragment() {
        progressDialog?.dismiss()
        findNavController().navigate(R.id.action_to_main_fragment)
    }

    companion object {
        private const val TAG = "Auth Fragment"
        const val IS_REGISTERED_KEY = "is_registered"
        const val MIN_LOGIN_LENGTH = 4
        const val MIN_PASSWORD_LENGTH = 8
    }
}