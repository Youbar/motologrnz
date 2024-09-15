package com.motologr.data.objects.ruc

import com.motologr.data.logging.Loggable
import java.math.BigDecimal
import java.util.Date

class Ruc(var transactionDate: Date,
          var unitsPurchased: Int,
          var unitsHeldAfterTransaction: Int,
          var price: BigDecimal,
          override var vehicleId: Int) : Loggable(transactionDate, 4, price, vehicleId) {

    fun convertToRucEntity() : RucEntity {
        val rucEntity = RucEntity(id, transactionDate, unitsPurchased, unitsHeldAfterTransaction, price, vehicleId)
        return rucEntity
    }
}