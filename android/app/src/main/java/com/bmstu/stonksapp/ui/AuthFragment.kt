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
import com.bmstu.stonksapp.ui.dialogs.DefaultDialog
import com.bmstu.stonksapp.ui.dialogs.DefaultDialog.Companion.DIALOG_TEXT_KEY
import com.bmstu.stonksapp.ui.dialogs.ProgressDialog
import com.bmstu.stonksapp.util.DialogsWorker
import com.bmstu.stonksapp.vm.MainViewModel

class AuthFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var progressDialog: ProgressDialog? = null

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
                val response = it.getContentIfNotHandled()
                response?.let {
                    progressDialog?.dismiss()
                    when (response) {
                        is ResultWrapper.Success -> {
                            Log.i(TAG, "success  register response: " + response.value)
                            findNavController().navigate(R.id.action_to_main_fragment)
                        }
                        is ResultWrapper.NetworkError -> DialogsWorker.showDefaultDialog(
                            childFragmentManager, resources.getString(R.string.network_error_message), TAG)
                        is ResultWrapper.ServerError -> DialogsWorker.showDefaultDialog(
                            childFragmentManager, resources.getString(R.string.default_error_message), TAG)
                    }
                }
            })
            viewModel.sendTinkoffRegisterRequest()
        }
    }

    companion object {
        const val TAG = "Auth Fragment"
    }
}