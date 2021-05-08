package com.bmstu.stonksapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.bmstu.stonksapp.R

class PredictionsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_predictions, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnPredictions: Button = view.findViewById(R.id.btn_predictions)
        btnPredictions.setOnClickListener {
            parentFragment?.parentFragment?.findNavController()?.navigate(R.id.action_to_prediction_info_fragment)
        }
        val btnLogout: Button = view.findViewById(R.id.btn_auth)
        btnLogout.setOnClickListener {
            parentFragment?.parentFragment?.findNavController()?.navigate(R.id.action_to_auth_fragment)
        }
    }

    companion object {
        const val TAG = "Predictions Fragment"
    }
}