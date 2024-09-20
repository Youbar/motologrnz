package com.motologr.data

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

    fun getMinDt(): Date {
        val format = SimpleDateFormat("dd/MM/yyyy")
        val minDt = format.parse("01/01/1900")

        return minDt
    }

    fun isMinDt(date : Date) : Boolean {
        val format = SimpleDateFormat("dd/MM/yyyy")
        val compareDt = format.format(date)

        if (compareDt == "01/01/1900")
            return true

        return false
    }
}