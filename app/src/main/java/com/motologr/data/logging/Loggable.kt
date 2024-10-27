package com.motologr.data.logging

import com.motologr.data.DataManager
import com.motologr.data.EnumConstants
import com.motologr.data.objects.LoggableEntity
import java.math.BigDecimal
import java.util.Date

// Repair = 0, Service = 1, WOF = 2, Reg = 3, Ruc = 4, Fuel = 100, Insurance = 200, InsuranceBill = 201
open class Loggable(var sortableDate: Date,
                    var classId: Int,
                    var unitPrice: BigDecimal,
                    open var vehicleId: Int,
                    open var id : Int = -1) {
    init {
        if (id == -1)
            id = DataManager.fetchIdForLoggable()
    }

    fun convertToLoggableEntity() : LoggableEntity {
        val loggableEntity = LoggableEntity(id, sortableDate, classId, unitPrice, vehicleId)
        return loggableEntity
    }

    fun returnNameByClassId() : String {
        if (classId == EnumConstants.LoggableType.Fuel.id)
            return "Fuel"
        else if (classId == EnumConstants.LoggableType.WOF.id)
            return "WOF"
        else if (classId == EnumConstants.LoggableType.Reg.id)
            return "Registration"
        else if (classId == EnumConstants.LoggableType.Ruc.id)
            return "RUCs"
        else if (classId == EnumConstants.LoggableType.Repair.id)
            return "Repair"
        else if (classId == EnumConstants.LoggableType.Service.id)
            return "Service"
        else if (classId == EnumConstants.LoggableType.Insurance.id)
            return "Insurance"
        else if (classId == EnumConstants.LoggableType.InsuranceBill.id)
            return "Insurance Bill"

        return ""
    }
}