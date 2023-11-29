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

    var idCounter: Int = 0

    fun FetchIdForLoggable() : Int {
        idCounter += 1

        return (idCounter - 1)
    }
}

class Vehicle (var modelName: String, var year: Int, var expiryWOF: Date, var regExpiry: Date, var odometer: Int) {

    var fuelLog: FuelLog = FuelLog()
    var serviceLog: ServiceLog = ServiceLog()
    var repairLog: RepairLog = RepairLog()
    var wofLog: WofLog = WofLog()
    var regLog: RegLog = RegLog()

    fun returnLogs() : ArrayList<Loggable> {
        var logs = ArrayList<Loggable>()

        logs.addAll(serviceLog.returnServiceLog())
        logs.addAll(repairLog.returnRepairLog())
        logs.addAll(wofLog.returnWofLog())
        logs.sortedBy { loggable -> loggable.sortableDate }

        return logs
    }

    fun returnLoggable(id: Int) : Loggable? {
        var loggable: Loggable? = returnLogs().find { loggable -> loggable.Id == id }

        return loggable
    }

    fun returnLoggableByPosition(position : Int) : Loggable? {
        var loggable: Loggable? = returnLogs()[position]

        return loggable
    }

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

open class Log


// Repair = 0, Service = 1, WOF = 2
open class Loggable(var sortableDate: Date, val classId: Int) {
    var Id : Int = -1

    init {
        Id = DataManager.FetchIdForLoggable()
    }
}

class ServiceLog : Log() {
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

class Service(var serviceType: Int, var price: Double, var serviceDate: Date, var serviceProvider: String, var comment: String) : Loggable(serviceDate, 1) {
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

class RepairLog : Log() {
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

class Repair(var repairType: Int, var price: Double, var repairDate: Date, var repairProvider: String, var comment: String) : Loggable(repairDate, 0) {
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

class WofLog : Log() {
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

class Wof(var wofDate: Date, var wofCompletedDate: Date, var price: Double) : Loggable(wofCompletedDate, 2)

class RegLog : Log() {
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

class Reg(var newRegExpiryDate: Date, var regExpiryDate: Date, var monthsExtended: Int, var price: Double) : Loggable(regExpiryDate, 3)

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

class Fuel(var fuelType: Int, var price: Double, var litres: Double, var purchaseDate: Date, var odometerReading: Int) : Loggable(purchaseDate, 100) {
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