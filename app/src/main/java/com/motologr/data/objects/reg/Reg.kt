package com.motologr.data.objects.reg

import com.motologr.data.logging.Loggable
import java.math.BigDecimal
import java.util.Date

class Reg(var newRegExpiryDate: Date,
          var regExpiryDate: Date,
          var monthsExtended: Int,
          var price: BigDecimal,
          override var vehicleId: Int) : Loggable(regExpiryDate, 3, price, vehicleId) {

    fun convertToRegEntity() : RegEntity {
        val regEntity = RegEntity(id, newRegExpiryDate, regExpiryDate,monthsExtended, price, vehicleId)
        return regEntity
    }
}