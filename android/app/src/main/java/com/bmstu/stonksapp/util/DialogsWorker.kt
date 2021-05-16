package com.bmstu.stonksapp.util

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.bmstu.stonksapp.ui.dialogs.DefaultDialog

class DialogsWorker {

    companion object {

        fun showDefaultDialog(manager: FragmentManager, text: String, tag: String) {
            DefaultDialog().apply {
                arguments = Bundle().apply { putString(DefaultDialog.DIALOG_TEXT_KEY, text) }
                show(manager, tag)
            }
        }
    }
}