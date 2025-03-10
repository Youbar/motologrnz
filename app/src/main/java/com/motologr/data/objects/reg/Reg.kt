package com.motologr.data.objects.reg

import com.motologr.data.DataHelper
import com.motologr.data.logging.Loggable
import com.motologr.data.toCalendar
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Calendar
import java.util.Date

class Reg(var newRegExpiryDate: Date,
          var regExpiryDate: Date,
          var monthsExtended: Int,
          var price: BigDecimal,
          override var vehicleId: Int,
          var purchaseDate : Date,
          var isHistorical : Boolean = false) : Loggable(purchaseDate, 3, price, vehicleId) {

    fun convertToRegEntity() : RegEntity {
        val regEntity = RegEntity(id, newRegExpiryDate, regExpiryDate, monthsExtended, price, vehicleId, purchaseDate, isHistorical)
        return regEntity
    }

    companion object {
        var calculateRegistrationLambda : (Int) -> BigDecimal = { x -> calculateRegistration(x) }

        fun calculateRegistration(months : Int) : BigDecimal {
            val df = DecimalFormat("0.00")
            df.roundingMode = RoundingMode.HALF_UP

            val licenceFee = BigDecimal(43.50).divide(12.0.toBigDecimal())
            val accFee = BigDecimal(41.27).divide(12.0.toBigDecimal())
            val adminFee = BigDecimal(7.53)
            val gstMultiplier = BigDecimal(1.15)
            val registrationEstimate = ((licenceFee + accFee)
                .multiply(months.toBigDecimal()) + adminFee)
                .multiply(gstMultiplier)

            return when (months) {
                0 -> 0.0.toBigDecimal()
                3 -> 33.05.toBigDecimal()
                6 -> 57.41.toBigDecimal()
                12 -> 106.15.toBigDecimal()
                else -> registrationEstimate
            }
        }

        var calculateRegistrationDateLambda : (String, Int) -> String = { oldExpiryDate, monthsAdded ->
            calculateRegistrationDate(oldExpiryDate, monthsAdded)
        }

        fun calculateRegistrationDate(oldExpiryDateString : String, monthsAdded : Int) : String {
            val oldExpiryDate = DataHelper.parseNumericalDateFormat(oldExpiryDateString)

            val calendar: Calendar = Calendar.getInstance().toCalendar(oldExpiryDate)
            calendar.add(Calendar.MONTH, monthsAdded)
            return DataHelper.formatNumericalDateFormat(calendar.time)
        }
    }
}