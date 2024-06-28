package com.motologr.ui.data.objects.maint

import com.motologr.ui.data.logging.Loggable
import java.math.BigDecimal
import java.util.Date

class Wof(var wofDate: Date,
          var wofCompletedDate: Date,
          var price: BigDecimal,
          override var vehicleId: Int) : Loggable(wofCompletedDate, 2, price, vehicleId) {

    fun convertToWofEntity() : WofEntity {
        val wofEntity = WofEntity(id, wofDate, wofCompletedDate, price, vehicleId)
        return wofEntity
    }
}