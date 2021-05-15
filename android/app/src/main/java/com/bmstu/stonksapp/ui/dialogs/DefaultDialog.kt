package com.bmstu.stonksapp.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.bmstu.stonksapp.R

class DefaultDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity(), R.style.DialogStyle)
        var dialogText = ""
        arguments?.getString(DIALOG_TEXT_KEY)?.let { dialogText = it }
        builder.setMessage(dialogText)
        builder.setPositiveButton("Ок", null)
        return builder.create()
    }

    companion object {
        const val DIALOG_TEXT_KEY = "dialog_text"
    }
}