package com.example.motologr.ui.data

import java.util.Date

object DataManager {

    private var vehicleArray = ArrayList<Vehicle>()

    fun CreateNewVehicle(modelName: String, year: Int, lastWOF: Date, regExpiry: Date) {
        val newVehicle = Vehicle(modelName, year, lastWOF, regExpiry)
        vehicleArray.add(newVehicle)
    }

    fun CreateNewVehicle(newVehicle: Vehicle) {
        vehicleArray.add(newVehicle)
    }

    fun ReturnVehicle(index : Int): Vehicle? {
        if (vehicleArray.lastIndex >= index) {
            return vehicleArray[index]
        }

        return null
    }

}

class Vehicle (var modelName: String, var year: Int, var expiryWOF: Date, var regExpiry: Date)