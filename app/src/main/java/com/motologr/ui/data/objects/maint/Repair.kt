package com.motologr.ui.data.objects.maint

import com.motologr.ui.data.logging.Loggable
import java.util.Date

class Repair(var repairType: Int, var price: Double, var repairDate: Date, var repairProvider: String, var comment: String) : Loggable(repairDate, 0, price) {
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
}