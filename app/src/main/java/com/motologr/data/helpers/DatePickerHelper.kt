package com.motologr.data.helpers

import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.NumberPicker

object DatePickerHelper {

    // Ripped from https://stackoverflow.com/questions/68395799/spinner-datepicker-with-numeric-months-instead-of-named
    fun setNumericMonth(datePicker: DatePicker) {
        val monthNumbers = arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
        val monthPicker = getMonthNumberPicker(datePicker)
        if (monthPicker != null) {
            monthPicker.displayedValues = monthNumbers
        }
    }

    private fun getMonthNumberPicker(datePicker: DatePicker?): NumberPicker? {
        try {
            if (datePicker != null && datePicker.childCount > 0 && datePicker.getChildAt(0) is ViewGroup) {
                val vg = datePicker.getChildAt(0) as ViewGroup
                if (vg.childCount > 0 && vg.getChildAt(0) is ViewGroup) {
                    val vgPickers = vg.getChildAt(0) as ViewGroup
                    for (i in 0 until vgPickers.childCount) {
                        if (vgPickers.getChildAt(i) is NumberPicker && i == 1) {
                            return vgPickers.getChildAt(i) as NumberPicker
                        }
                    }
                }
            }
        } catch (e: Exception) {
        }
        return null
    }
}