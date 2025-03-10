package com.motologr.data.logging.fuel

import com.motologr.data.objects.fuel.Fuel
import com.motologr.data.logging.Log
import com.motologr.data.objects.fuel.FuelEntity

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

    companion object {
        fun castFuelLoggableEntities(fuelEntities : List<FuelEntity>?) : FuelLog {
            val fuelLog = FuelLog()

            if (fuelEntities == null)
                return fuelLog

            for (fuelEntity in fuelEntities){
                fuelLog.addFuelToFuelLog(fuelEntity.convertToFuelObject())
            }

            return fuelLog
        }
    }
}