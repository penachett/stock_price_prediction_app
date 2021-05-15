package com.bmstu.stonksapp.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.bmstu.stonksapp.R


class ProgressDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(
            requireActivity(), R.style.ProgressDialogStyle
        )
        val layoutInflater = requireActivity().layoutInflater
        val dialog: View = layoutInflater.inflate(R.layout.dialog_progress, null)
        return builder
            .setView(dialog)
            .setCancelable(false)
            .create()
    }
}