package com.motologr.ui.data.logging.fuel

import com.motologr.ui.data.objects.fuel.Fuel
import com.motologr.ui.data.logging.Log

class FuelLog : Log() {
    private var fuelLog = ArrayList<Fuel>()

    fun addFuelToFuelLog(fuel: Fuel) {
        fuelLog.add(fuel)
    }

    fun returnFuelLog(): ArrayList<Fuel> {
        return fuelLog
    }

    fun returnFuel(index: Int) : Fuel {
        return fuelLog[index]
    }
}