package com.example.motologr.ui.data

import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date

object DataManager {

    private var vehicleArray = ArrayList<Vehicle>()

    fun CreateNewVehicle(modelName: String, year: Int, expiryWOF: Date, regExpiry: Date, odometer: Int) {
        val newVehicle = Vehicle(modelName, year, expiryWOF, regExpiry, odometer)
        newVehicle.CalculateLastWOF();
        vehicleArray.add(newVehicle)
    }

    fun CreateNewVehicle(newVehicle: Vehicle) {
        newVehicle.CalculateLastWOF();
        vehicleArray.add(newVehicle)
    }

    fun ReturnVehicle(index : Int): Vehicle? {
        if (vehicleArray.lastIndex >= index) {
            return vehicleArray[index]
        }

        return null
    }

    fun ReturnVehicleArrayLength() : Int {
        return vehicleArray.size
    }

}

class Vehicle (var modelName: String, var year: Int, var expiryWOF: Date, var regExpiry: Date, var odometer: Int) {

    lateinit var lastWOF: Date;

    fun CalculateLastWOF() {
        val calendar : Calendar = Calendar.getInstance()
        calendar.set(expiryWOF.year, expiryWOF.month, expiryWOF.date)
        calendar.add(Calendar.MONTH, -6)
        lastWOF = calendar.time
    }

}