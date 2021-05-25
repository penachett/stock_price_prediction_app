package com.bmstu.stonksapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bmstu.stonksapp.R
import com.bmstu.stonksapp.model.ResultWrapper
import com.bmstu.stonksapp.ui.dialogs.ProgressDialog
import com.bmstu.stonksapp.util.DialogsWorker
import com.bmstu.stonksapp.util.SharedPrefs
import com.bmstu.stonksapp.util.errToMessage
import com.bmstu.stonksapp.vm.MainViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegisterFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val registerBtn: Button = view.findViewById(R.id.register_btn)
        val toAuthTv: TextView = view.findViewById(R.id.to_auth_tv)
        toAuthTv.setOnClickListener {
            findNavController().navigate(R.id.action_to_auth_fragment_from_register)
        }
        val loginEt: TextInputEditText = view.findViewById(R.id.login_et)
        val passwordEt: TextInputEditText = view.findViewById(R.id.password_et)
        val passwordRepeatEt: TextInputEditText = view.findViewById(R.id.password_repeat_et)
        val passwordInput: TextInputLayout = view.findViewById(R.id.password_input)
        val passwordRepeatInput: TextInputLayout = view.findViewById(R.id.password_repeat_input)
        registerBtn.setOnClickListener {
            handleRegister(loginEt.text.toString(), passwordEt.text.toString(), passwordRepeatEt.text.toString())
        }
    }

    private fun handleRegister(login: String, password: String, passwordRepeat: String) {
        if (password != passwordRepeat) {
            DialogsWorker.showDefaultDialog(
                    childFragmentManager, resources.getString(R.string.passwords_not_match), TAG)
            return
        }
        if (login.length < AuthFragment.MIN_LOGIN_LENGTH) {
            DialogsWorker.showDefaultDialog(
                    childFragmentManager, resources.getString(R.string.login_too_short), TAG)
            return
        }
        if (password.length < AuthFragment.MIN_PASSWORD_LENGTH) {
            DialogsWorker.showDefaultDialog(
                    childFragmentManager, resources.getString(R.string.password_too_short), TAG)
            return
        }
        progressDialog = ProgressDialog()
        progressDialog?.show(childFragmentManager, TAG)
        register(login, password)
    }

    private fun register(login: String, password: String) {
        viewModel.getRegisterResponses().observe(viewLifecycleOwner, {
            val response = it.getContentIfNotWatched()
            response?.let {
                progressDialog?.dismiss()
                when (response) {
                    is ResultWrapper.Success -> {
                        Log.i(TAG, "success register response")
                        val prefs = SharedPrefs(requireActivity())
                        prefs.saveAuthData(login, password)
                        prefs.setAutoAuth(true)
                        onRegistered()
                    }
                    is ResultWrapper.NetworkError -> {
                        DialogsWorker.showDefaultDialog(
                                childFragmentManager, resources.getString(R.string.network_error_message), TAG)
                    }
                    is ResultWrapper.ServerError -> {
                        DialogsWorker.showDefaultDialog(childFragmentManager, errToMessage(response.message), TAG)
                    }
                }
            }
        })
        viewModel.sendRegisterRequest(login, password)
    }

    private fun onRegistered() {
        val args = Bundle()
        args.putBoolean(AuthFragment.IS_REGISTERED_KEY, true)
        findNavController().navigate(R.id.action_to_auth_fragment_from_register, args)
    }

    companion object {
        const val TAG = "Register Fragment"
    }
}