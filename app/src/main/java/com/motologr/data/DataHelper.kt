package com.motologr.data

import android.content.Context
import android.widget.Toast
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DataHelper {

    private fun returnHalfUpDecimalFormat() : DecimalFormat {
        val df = DecimalFormat("0.00")
        df.roundingMode = RoundingMode.HALF_UP
        return df
    }

    fun roundToTwoDecimalPlaces(bigDecimal : BigDecimal) : String {
        val df = returnHalfUpDecimalFormat()
        return df.format(bigDecimal)
    }

    private val numericalDateFormat by lazy { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    fun parseNumericalDateFormat(dateString : String): Date {
        return numericalDateFormat.parse(dateString)
    }

    fun formatNumericalDateFormat(date : Date): String {
        return numericalDateFormat.format(date)
    }

    fun getCurrentDateString() : String {
        val currentDt = Calendar.getInstance().time

        return numericalDateFormat.format(currentDt.time)
    }

    fun getCurrentDate() : Date {
        val currentDt = Calendar.getInstance().time

        return currentDt
    }

    fun getMinDt(): Date {
        val minDt = numericalDateFormat.parse("01/01/1900")

        return minDt
    }

    fun isMinDt(date : Date) : Boolean {
        val compareDt = numericalDateFormat.format(date)

        if (compareDt == "01/01/1900")
            return true

        return false
    }

    fun isValidStringInput(input : String, inputName : String, makeToast : (String) -> Unit) : Boolean {
        if (input.isEmpty()) {
            makeToast("You must enter a value for $inputName.")
            return false
        }

        return true
    }

    fun isValidIntegerInput(input : String, inputName : String, makeToast : (String) -> Unit) : Boolean {
        if (input.isEmpty()) {
            makeToast("You must enter a value for $inputName.")
            return false
        }

        if (input.toIntOrNull() == null) {
            makeToast("$inputName is too large or not a number.")
            return false
        }

        if (input.toInt() == 0) {
            makeToast("$inputName cannot be 0.")
            return false
        }

        if (input.toInt() < 0) {
            makeToast("$inputName cannot be less than 0.")
            return false
        }

        if (input.contains(',') || input.contains('.')) {
            makeToast("$inputName cannot contain decimal points.")
            return false
        }

        return true
    }

    fun isValidCurrencyInput(input : String, inputName : String, makeToast : (String) -> Unit) : Boolean {
        if (input.isEmpty()) {
            makeToast("You must enter a value for $inputName.")
            return false
        }

        if (input.toBigDecimalOrNull() == null) {
            makeToast("$inputName is too large or not a valid number.")
            return false
        }

        if (input.toDouble() < 0) {
            makeToast("$inputName cannot be less than 0.")
            return false
        }

        if (input.contains(',')) {
            makeToast("$inputName cannot contain parse ',' as a decimal.")
            return false
        }

        if (input.count { char -> char == '.'} > 1) {
            makeToast("$inputName contains too many decimal points.")
            return false
        }

        return true
    }
}