package com.example.motologr.ui.data

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date

object DataManager {

    private var vehicleArray = ArrayList<Vehicle>()

    fun CreateNewVehicle(modelName: String, year: Int, expiryWOF: Date, regExpiry: Date, odometer: Int) {
        val newVehicle = Vehicle(modelName, year, expiryWOF, regExpiry, odometer)
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

    fun ReturnVehicleArrayLength() : Int {
        return vehicleArray.size
    }

    fun SetVehicleInsurance(index: Int, _insurance: Insurance) {
        if (vehicleArray.lastIndex >= index) {
            vehicleArray[index].SetInsurance(_insurance)
        }
    }
}

class Vehicle (var modelName: String, var year: Int, var expiryWOF: Date, var regExpiry: Date, var odometer: Int) {

    lateinit var insurance: Insurance

    fun isInsuranceInitialised() = ::insurance.isInitialized

    lateinit var lastWOF: Date

    fun SetInsurance(_insurance: Insurance) {
        insurance = _insurance
    }
}

class FuelLog() {
    private var fuelLog = ArrayList<Fuel>()
}

class Fuel(var fuelType: Int, var price: Double, var litres: Double, var purchaseDate: Date, var odometerReading: Int) {



    // type 0 = 91, 1 = 95, 2 = 98, 3 = diesel
    fun returnFuelType() : String {
        if (fuelType == 0) {
            return "Fortnightly"
        } else if (fuelType == 1) {
            return "Monthly"
        } else if (fuelType == 2) {
            return "Annually"
        } else {
            return "Invalid"
        }
    }

}

class Insurance (var isActive: Boolean, var insurer: String, var coverage: Int,
                 var billingCycle: Int, var billing: Double, var lastBill: Date) {
    fun getNextBillingDate() : Date {
        val calendar : Calendar = Calendar.getInstance()
        calendar.set(lastBill.year, lastBill.month, lastBill.date)

        if (billingCycle == 0) {
            calendar.add(Calendar.DATE, 14)
        } else if (billingCycle == 1) {
            calendar.add(Calendar.MONTH, 1)
        } else if (billingCycle == 2) {
            calendar.add(Calendar.YEAR, 1)
        }

        return calendar.time
    }

    fun getNextBillingDateString() : String {
        val format: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

        return format.format(getNextBillingDate())
    }

    // coverage 0 = comp, 1 = 3rd+, 2 = 3rd
    fun returnCoverageType() : String {
        if (coverage == 0) {
            return "Comprehensive"
        } else if (coverage == 1) {
            return "Third Party Fire & Theft"
        } else if (coverage == 2) {
            return "Third Party"
        } else {
            return "Invalid"
        }
    }

    // billingCycle 0 = fortnightly, 1 = monthly, 2 = annually
    fun returnCycleType() : String {
        if (billingCycle == 0) {
            return "Fortnightly"
        } else if (billingCycle == 1) {
            return "Monthly"
        } else if (billingCycle == 2) {
            return "Annually"
        } else {
            return "Invalid"
        }
    }
}