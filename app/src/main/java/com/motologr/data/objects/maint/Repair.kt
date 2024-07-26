package com.motologr.data.objects.maint

import com.motologr.data.logging.Loggable
import java.math.BigDecimal
import java.util.Date

class Repair(var repairType: Int,
             var price: BigDecimal,
             var repairDate: Date,
             var repairProvider: String,
             var comment: String,
             override var vehicleId: Int) : Loggable(repairDate, 0, price, vehicleId) {
    fun returnRepairType() : String {
        return when (repairType) {
            0 -> {
                "Minor"
            }
            1 -> {
                "Major"
            }
            2 -> {
                "Critical"
            }
            else -> {
                "Invalid"
            }
        }
    }

    fun convertToRepairEntity() : RepairEntity {
        val repairEntity = RepairEntity(id, repairType, price, repairDate, repairProvider, comment, vehicleId)
        return repairEntity
    }
}