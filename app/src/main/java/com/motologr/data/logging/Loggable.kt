package com.motologr.data.logging

import com.motologr.data.DataManager
import com.motologr.data.objects.LoggableEntity
import java.math.BigDecimal
import java.util.Date

// Repair = 0, Service = 1, WOF = 2, Reg = 3, Fuel = 100, Insurance = 200, InsuranceBill = 201
open class Loggable(var sortableDate: Date,
                    var classId: Int,
                    var unitPrice: BigDecimal,
                    open var vehicleId: Int) {
    var id : Int = -1

    init {
        id = DataManager.FetchIdForLoggable()
    }

    fun convertToLoggableEntity() : LoggableEntity {
        val loggableEntity = LoggableEntity(id, sortableDate, classId, unitPrice, vehicleId)
        return loggableEntity
    }
}