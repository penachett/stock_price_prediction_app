package com.bmstu.stonksapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.bmstu.stonksapp.R
import com.bmstu.stonksapp.model.PeriodItem


class PeriodsSpinnerAdapter(context: Context, textViewResourceId: Int,
                            private val periods: Array<PeriodItem>) : ArrayAdapter<PeriodItem>(context, textViewResourceId, periods) {

    override fun getDropDownView(position: Int, convertView: View?,
                                 parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    @Suppress("NAME_SHADOWING")
    private fun getCustomView(position: Int, convertView: View?,
                              parent: ViewGroup?): View {
        var convertView: View? = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_period_spinner, null)
        }
        val periodTv = convertView?.findViewById<TextView>(R.id.spinner_dropdown)
        periodTv?.text = periods[position].toString()
        return convertView!!
    }

}