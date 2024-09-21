package com.motologr.data.objects.ruc

import com.motologr.data.logging.Loggable
import java.math.BigDecimal
import java.util.Date

class Ruc(var purchaseDate: Date,
          var unitsPurchased: Int,
          var unitsHeldAfterTransaction: Int,
          var price: BigDecimal,
          override var vehicleId: Int,
          var isHistorical : Boolean = false) : Loggable(purchaseDate, 4, price, vehicleId) {

    fun convertToRucEntity() : RucEntity {
        val rucEntity = RucEntity(id, purchaseDate, unitsPurchased, unitsHeldAfterTransaction, price, vehicleId, isHistorical)
        return rucEntity
    }
}