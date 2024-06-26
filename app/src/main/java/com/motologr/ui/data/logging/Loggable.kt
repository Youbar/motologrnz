package com.motologr.ui.data.logging

import com.motologr.ui.data.DataManager
import java.util.Date

// Repair = 0, Service = 1, WOF = 2, Reg = 3, Fuel = 100, Insurance = 200, InsuranceBill = 201
open class Loggable(var sortableDate: Date, val classId: Int, var unitPrice: Double) {
    var Id : Int = -1

    init {
        Id = DataManager.FetchIdForLoggable()
    }
}