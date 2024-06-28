package com.motologr.ui.data.objects.vehicle

import com.motologr.MainActivity
import com.motologr.ui.data.DataManager
import com.motologr.ui.data.logging.Loggable
import com.motologr.ui.data.objects.reg.Reg
import com.motologr.ui.data.logging.reg.RegLog
import com.motologr.ui.data.objects.maint.Repair
import com.motologr.ui.data.logging.maint.RepairLog
import com.motologr.ui.data.objects.maint.Service
import com.motologr.ui.data.logging.maint.ServiceLog
import com.motologr.ui.data.objects.maint.Wof
import com.motologr.ui.data.logging.maint.WofLog
import com.motologr.ui.data.logging.fuel.FuelLog
import com.motologr.ui.data.logging.insurance.InsuranceLog
import com.motologr.ui.data.objects.fuel.Fuel
import com.motologr.ui.data.objects.insurance.Insurance
import java.text.SimpleDateFormat
import java.util.Date

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
        var loggable: Loggable? = returnMaintLogs().find { loggable -> loggable.id == id }

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

        Thread {
            MainActivity.getDatabase()
                ?.fuelLoggableDao()
                ?.insert(fuel.convertToFuelEntity())
            MainActivity.getDatabase()
                ?.loggableDao()
                ?.insert(fuel.convertToLoggableEntity())
        }.start()
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