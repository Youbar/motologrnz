package com.motologr.data

import android.content.Context
import android.widget.Toast
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date

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

    private val numericalDateFormat = SimpleDateFormat("dd/MM/yyyy")

    fun convertStringToNumericalDate(dateString : String) : Date {
        return numericalDateFormat.parse(dateString)
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

    fun isValidIntegerInput(input : String, inputName : String, context : Context) : Boolean {
        if (input.isEmpty()) {
            Toast.makeText(context, "You must enter a value for ${inputName}.", Toast.LENGTH_LONG).show()
            return false
        }

        if (input.toIntOrNull() == null) {
            Toast.makeText(context, "${inputName} is too large or not a number.", Toast.LENGTH_LONG).show()
            return false
        }

        if (input.toInt() == 0) {
            Toast.makeText(context, "${inputName} cannot be 0.", Toast.LENGTH_LONG).show()
            return false
        }

        if (input.toInt() < 0) {
            Toast.makeText(context, "${inputName} cannot be less than 0.", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }
}