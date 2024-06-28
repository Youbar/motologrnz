package com.motologr.ui.data.logging

import com.motologr.ui.data.DataManager
import com.motologr.ui.data.objects.LoggableEntity
import java.math.BigDecimal
import java.util.Date

// Repair = 0, Service = 1, WOF = 2, Reg = 3, Fuel = 100, Insurance = 200, InsuranceBill = 201
open class Loggable(var sortableDate: Date,
                    var classId: Int,
                    var unitPrice: BigDecimal) {
    var id : Int = -1

    init {
        id = DataManager.FetchIdForLoggable()
    }

    fun convertToLoggableEntity() : LoggableEntity {
        val loggableEntity = LoggableEntity(id, sortableDate, classId, unitPrice)
        return loggableEntity
    }
}