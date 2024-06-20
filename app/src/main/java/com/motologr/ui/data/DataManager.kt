package com.motologr.ui.data

import android.widget.DatePicker
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import com.motologr.R
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar
import java.util.Date

fun DatePicker.getDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, dayOfMonth)
    return calendar.time
}

fun Calendar.toCalendar(date: Date) : Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar
}

object DataManager {

    fun updateTitle(activity: FragmentActivity?, newTitle: String) {
        val toolbar = activity?.findViewById(R.id.toolbar) as Toolbar
        toolbar.title = newTitle
    }

    fun roundOffDecimal(number: Double): String {
        val df = DecimalFormat("0.00")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number)
    }

    private var vehicleArray = ArrayList<Vehicle>()
    fun CreateNewVehicle(brandName: String, modelName: String, year: Int, expiryWOF: Date, regExpiry: Date, odometer: Int) {
        val newVehicle = Vehicle(brandName, modelName, year, expiryWOF, regExpiry, odometer)
        vehicleArray.add(newVehicle)
    }

    fun CreateNewVehicle(newVehicle: Vehicle) {
        vehicleArray.add(newVehicle)
        SetActiveVehicle(vehicleArray.lastIndex)
    }

    fun ReturnVehicle(index : Int): Vehicle? {
        if (vehicleArray.lastIndex >= index) {
            return vehicleArray[index]
        }

        return null
    }

    fun isVehicles(): Boolean {
        return vehicleArray.isNotEmpty()
    }

    fun ReturnVehicleById(id : Int): Vehicle? {
        val vehicle: Vehicle? = vehicleArray.find { v -> v.getId() == id }

        return vehicle
    }

    private var activeVehicle : Int = -1

    fun SetActiveVehicle(int: Int) {
        activeVehicle = int;
    }

    fun ReturnActiveVehicle(): Vehicle? {
        if (vehicleArray.lastIndex >= activeVehicle && activeVehicle >= 0) {
            return vehicleArray[activeVehicle]
        }

        return null
    }

    fun ReturnVehicleArrayLength() : Int {
        return vehicleArray.size
    }

    private var idCounterLoggable: Int = 0

    fun FetchIdForLoggable() : Int {
        idCounterLoggable += 1

        return (idCounterLoggable - 1)
    }

    private var idCounterVehicle: Int = 0

    fun FetchIdForVehicle() : Int {
        idCounterVehicle += 1

        return (idCounterVehicle - 1)
    }
}

class Vehicle (var brandName: String, var modelName: String, var year: Int, var expiryWOF: Date, var regExpiry: Date, var odometer: Int) {

    var insuranceLog: InsuranceLog = InsuranceLog()
    var fuelLog: FuelLog = FuelLog()
    var serviceLog: ServiceLog = ServiceLog()
    var repairLog: RepairLog = RepairLog()
    var wofLog: WofLog = WofLog()
    var regLog: RegLog = RegLog()

    private var id: Int = -1

    init {
        this.id = DataManager.FetchIdForVehicle()
    }

    fun getId() : Int {
        return id
    }

    fun getLatestOdometerReading() : Int {
        var odometerReadings = fuelLog.returnFuelLog()
        if (odometerReadings.isEmpty())
            return odometer

        odometerReadings.sortByDescending { log -> log.odometerReading }
        return odometerReadings.first().odometerReading
    }

    fun returnMaintLogs() : ArrayList<Loggable> {
        var logs = ArrayList<Loggable>()

        logs.addAll(serviceLog.returnServiceLog())
        logs.addAll(repairLog.returnRepairLog())
        logs.addAll(wofLog.returnWofLog())
        logs.sortByDescending { loggable -> loggable.sortableDate.time }

        return logs
    }

    fun returnExpensesLogs() : ArrayList<Loggable> {
        var logs = ArrayList<Loggable>()

        logs.addAll(serviceLog.returnServiceLog())
        logs.addAll(repairLog.returnRepairLog())
        logs.addAll(fuelLog.returnFuelLog())
        logs.addAll(wofLog.returnWofLog())
        logs.addAll(regLog.returnRegLog())
        logs.sortByDescending { loggable -> loggable.sortableDate.time }

        return logs
    }

    fun returnLoggable(id: Int) : Loggable? {
        var loggable: Loggable? = returnMaintLogs().find { loggable -> loggable.Id == id }

        return loggable
    }

    fun returnLoggableByPosition(position : Int) : Loggable? {
        var loggable: Loggable? = returnMaintLogs()[position]

        return loggable
    }

    fun logInsurance(insurance: Insurance) {
        insuranceLog.addInsuranceToInsuranceLog(insurance)
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

    private val format: SimpleDateFormat = SimpleDateFormat("dd/MMM/yyyy")

    fun returnWofExpiry(): String {
        return format.format(expiryWOF)
    }

    fun updateWofExpiry(newDate: Date) {
        expiryWOF = newDate
    }

    fun returnRegExpiry(): String {
        return format.format(regExpiry)
    }

    fun updateRegExpiry(newRegExpiryDate: Date) {
        regExpiry = newRegExpiryDate
    }
}

open class Log


// Repair = 0, Service = 1, WOF = 2, Reg = 3, Fuel = 100, Insurance = 200, InsuranceBill = 201
open class Loggable(var sortableDate: Date, val classId: Int, var unitPrice: Double) {
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

class Service(var serviceType: Int, var price: Double, var serviceDate: Date, var serviceProvider: String, var comment: String) : Loggable(serviceDate, 1, price) {
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

class Wof(var wofDate: Date, var wofCompletedDate: Date, var price: Double) : Loggable(wofCompletedDate, 2, price)

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

class Reg(var newRegExpiryDate: Date, var regExpiryDate: Date, var monthsExtended: Int, var price: Double) : Loggable(regExpiryDate, 3, price)

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

class Fuel(var fuelType: Int, var price: Double, var litres: Double, var purchaseDate: Date, var odometerReading: Int) : Loggable(purchaseDate, 100, price) {
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

class InsuranceLog : Log() {
    private var insuranceLog = ArrayList<Insurance>()

    fun addInsuranceToInsuranceLog(insurance: Insurance) {
        insuranceLog.add(insurance)
    }

    fun returnInsuranceLog(): ArrayList<Insurance> {
        return insuranceLog
    }

    fun returnInsurance(index: Int) : Insurance {
        return insuranceLog[index]
    }
}

class Insurance (var insurer: String, var insurancePolicyStartDate: Date, var coverage: Int,
                 var billingCycle: Int, var billing: Double, var lastBill: Date) : Loggable(insurancePolicyStartDate, 200, billing) {

    var insuranceBillingLog : InsuranceBillingLog = InsuranceBillingLog()

    init {
        generateInsuranceBills()
    }

    private fun generateInsuranceBills() {
        val calendar : Calendar = Calendar.getInstance()

        val policyStartDate = insurancePolicyStartDate

        // Work our way backwards from last billing date
        var lastBillingDate = lastBill

        calendar.set(lastBillingDate.year + 1900, lastBillingDate.month, lastBillingDate.date)

        while (lastBillingDate > policyStartDate) {
            if (billingCycle == 0) {
                calendar.add(Calendar.DATE, -14)
            } else if (billingCycle == 1) {
                calendar.add(Calendar.MONTH, -1)
            } else if (billingCycle == 2) {
                calendar.add(Calendar.YEAR, -1)
            }

            lastBillingDate = calendar.time
        }

        // Then forwards
        calendar.set(policyStartDate.year + 1900 + 1, policyStartDate.month, policyStartDate.date)
        val policyEndDate = calendar.time

        calendar.set(lastBillingDate.year + 1900, lastBillingDate.month, lastBillingDate.date)

        while (lastBillingDate < policyEndDate) {
            if (billingCycle == 0) {
                calendar.add(Calendar.DATE, 14)
            } else if (billingCycle == 1) {
                calendar.add(Calendar.MONTH, 1)
            } else if (billingCycle == 2) {
                calendar.add(Calendar.YEAR, 1)
            }

            lastBillingDate = calendar.time

            var insuranceBilling = InsuranceBilling(lastBillingDate, billing)
            insuranceBillingLog.addInsuranceBillingToInsuranceBillingLog(insuranceBilling)
        }
    }

    fun returnInsuranceBillingLogs() : InsuranceBillingLog {
        return insuranceBillingLog
    }

    fun getNextBillingDate() : Date {

        for (insuranceBilling in insuranceBillingLog.returnInsuranceLog()) {
            if (insuranceBilling.billingDate > Calendar.getInstance().time) {
                return insuranceBilling.billingDate
            }
        }

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

    fun returnIsActive() : Boolean {
        return true
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

class InsuranceBillingLog : Log() {
    private var insuranceBillingLog = ArrayList<InsuranceBilling>()

    fun addInsuranceBillingToInsuranceBillingLog(insuranceBilling: InsuranceBilling) {
        insuranceBillingLog.add(insuranceBilling)
    }

    fun returnInsuranceLog(): ArrayList<InsuranceBilling> {
        return insuranceBillingLog
    }

    fun returnInsurance(index: Int) : InsuranceBilling {
        return insuranceBillingLog[index]
    }
}

class InsuranceBilling(var billingDate: Date, var price: Double) : Loggable(billingDate, 201, price)