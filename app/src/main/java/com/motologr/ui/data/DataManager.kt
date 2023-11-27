package com.motologr.ui.data

import java.text.SimpleDateFormat
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
            vehicleArray[index].setVehicleInsurance(_insurance)
        }
    }
}

class Vehicle (var modelName: String, var year: Int, var expiryWOF: Date, var regExpiry: Date, var odometer: Int) {

    var fuelLog: FuelLog = FuelLog()
    var serviceLog: ServiceLog = ServiceLog()
    var repairLog: RepairLog = RepairLog()
    var wofLog: WofLog = WofLog()
    var regLog: RegLog = RegLog()

    fun logFuel(fuel: Fuel) {
        fuelLog.addFuelToFuelLog(fuel)
    }

    fun logService(service: Service) {
        serviceLog.addServiceToServiceLog(service)
    }

    fun logRepair(repair: Repair) {
        repairLog.addRepairToRepairLog(repair)
    }

    fun logWof(wof: Wof) {
        wofLog.addWofToWofLog(wof)
    }

    fun logReg(reg: Reg) {
        regLog.addRegToRegLog(reg)
    }

    lateinit var insurance: Insurance

    fun isInsuranceInitialised() = ::insurance.isInitialized

    fun setVehicleInsurance(_insurance: Insurance) {
        insurance = _insurance
    }

    private val format: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

    fun returnWofExpiry(): String {
        return format.format(expiryWOF)
    }

    fun updateWofExpiry(newDate: String) {
        expiryWOF = format.parse(newDate)
    }

    fun returnRegExpiry(): String {
        return format.format(regExpiry)
    }

    fun updateRegExpiry(newDate: String) {
        regExpiry = format.parse(newDate)
    }
}

class ServiceLog() {
    private var serviceLog = ArrayList<Service>()

    fun addServiceToServiceLog(service: Service) {
        serviceLog.add(service)
    }

    fun returnServiceLog(): ArrayList<Service> {
        return serviceLog
    }

    fun returnService(index: Int) : Service {
        return serviceLog[index]
    }
}

class Service(var serviceType: Int, var price: Double, var serviceDate: Date, var serviceProvider: String, var comment: String) {
    fun returnServiceType() : String {
        return when (serviceType) {
            0 -> {
                "Oil Change"
            }
            1 -> {
                "General"
            }
            2 -> {
                "Full"
            }
            else -> {
                "Invalid"
            }
        }
    }
}

class RepairLog() {
    private var repairLog = ArrayList<Repair>()

    fun addRepairToRepairLog(repair: Repair) {
        repairLog.add(repair)
    }

    fun returnRepairLog(): ArrayList<Repair> {
        return repairLog
    }

    fun returnRepair(index: Int) : Repair {
        return repairLog[index]
    }
}

class Repair(var repairType: Int, var price: Double, var repairDate: Date, var repairProvider: String, var comment: String) {
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

class WofLog() {
    private var wofLog = ArrayList<Wof>()

    fun addWofToWofLog(wof: Wof) {
        wofLog.add(wof)
    }

    fun returnWofLog(): ArrayList<Wof> {
        return wofLog
    }

    fun returnWof(index: Int) : Wof {
        return wofLog[index]
    }
}

class Wof(var wofDate: Date, var wofCompletedDate: Date, var price: Double)

class RegLog() {
    private var regLog = ArrayList<Reg>()

    fun addRegToRegLog(reg: Reg) {
        regLog.add(reg)
    }

    fun returnRegLog(): ArrayList<Reg> {
        return regLog
    }

    fun returnReg(index: Int) : Reg {
        return regLog[index]
    }
}

class Reg(var newRegExpiryDate: Date, var regExpiryDate: Date, var monthsExtended: Int, var price: Double)

class FuelLog() {
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

class Fuel(var fuelType: Int, var price: Double, var litres: Double, var purchaseDate: Date, var odometerReading: Int) {
    // type 0 = 91, 1 = 95, 2 = 98, 3 = diesel
    fun returnFuelType() : String {
        return when (fuelType) {
            0 -> {
                "91 Unleaded"
            }
            1 -> {
                "95 Unleaded"
            }
            2 -> {
                "98 Unleaded"
            }
            3 -> {
                "Diesel"
            }
            else -> {
                "Invalid"
            }
        }
    }

}

class Insurance (var isActive: Boolean, var insurer: String, var coverage: Int,
                 var billingCycle: Int, var billing: Double, var lastBill: Date) {
    fun getNextBillingDate() : Date {
        val calendar : Calendar = Calendar.getInstance()
        calendar.set(lastBill.year + 1900, lastBill.month, lastBill.date)

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
        return when (coverage) {
            0 -> {
                "Comprehensive"
            }
            1 -> {
                "Third Party Fire & Theft"
            }
            2 -> {
                "Third Party"
            }
            else -> {
                "Invalid"
            }
        }
    }

    // billingCycle 0 = fortnightly, 1 = monthly, 2 = annually
    fun returnCycleType() : String {
        return when (billingCycle) {
            0 -> {
                "Fortnightly"
            }
            1 -> {
                "Monthly"
            }
            2 -> {
                "Annually"
            }
            else -> {
                "Invalid"
            }
        }
    }
}