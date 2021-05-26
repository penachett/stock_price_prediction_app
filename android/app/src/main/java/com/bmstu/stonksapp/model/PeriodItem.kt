package com.bmstu.stonksapp.model

import com.bmstu.stonksapp.util.monthStringByCount

class PeriodItem(val monthCount: Int) {
    override fun toString(): String {
        return "$monthCount ${monthStringByCount(monthCount)}"
    }
}