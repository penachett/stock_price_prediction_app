package com.bmstu.stonksapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.bmstu.stonksapp.R

class RegisterFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_register, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val registerBtn: Button = view.findViewById(R.id.register_btn)
        val toAuthTv: TextView = view.findViewById(R.id.to_auth_tv)
        toAuthTv.setOnClickListener { findNavController().navigate(R.id.action_to_auth_fragment_from_register) }
    }

    companion object {
        const val TAG = "Register Fragment"
    }
}