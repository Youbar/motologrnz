package com.motologr.data.objects.maint

import com.motologr.data.logging.Loggable
import java.math.BigDecimal
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
}