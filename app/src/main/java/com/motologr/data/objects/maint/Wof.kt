package com.motologr.data.objects.maint

import com.motologr.data.logging.Loggable
import com.motologr.data.toCalendar
import java.math.BigDecimal
import java.util.Calendar
import java.util.Date

class Wof(var wofDate: Date,
          var wofCompletedDate: Date,
          var price: BigDecimal,
          override var vehicleId: Int,
          var wofProvider: String,
          var purchaseDate : Date,
          var isHistorical : Boolean = false) : Loggable(purchaseDate, 2, price, vehicleId) {
    fun convertToWofEntity() : WofEntity {
        val wofEntity = WofEntity(id, wofDate, wofCompletedDate, price, vehicleId, wofProvider, purchaseDate, isHistorical)
        return wofEntity
    }

    companion object {
        fun applyWofYearRule(wofExpiryDt : Date, vehicleYear : Int) : Date {
            var monthsToAdd = 12
            if (vehicleYear < 2000)
                monthsToAdd = 6

            val calendar: Calendar = Calendar.getInstance().toCalendar(wofExpiryDt)
            calendar.add(Calendar.MONTH, monthsToAdd)

            return calendar.time
        }
    }
}