package com.motologr.data.objects.vehicle

import com.motologr.MainActivity
import com.motologr.data.DataManager
import com.motologr.data.logging.Loggable
import com.motologr.data.objects.reg.Reg
import com.motologr.data.logging.reg.RegLog
import com.motologr.data.objects.maint.Repair
import com.motologr.data.logging.maint.RepairLog
import com.motologr.data.objects.maint.Service
import com.motologr.data.logging.maint.ServiceLog
import com.motologr.data.objects.maint.Wof
import com.motologr.data.logging.maint.WofLog
import com.motologr.data.logging.fuel.FuelLog
import com.motologr.data.logging.insurance.InsuranceLog
import com.motologr.data.objects.fuel.Fuel
import com.motologr.data.objects.insurance.Insurance
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class Vehicle (val id: Int, var brandName: String, var modelName: String, var year: Int, private val expiryWOF: Date, private val regExpiry: Date, var odometer: Int) {

    var insuranceLog: InsuranceLog = InsuranceLog()
    var fuelLog: FuelLog = FuelLog()
    var serviceLog: ServiceLog = ServiceLog()
    var repairLog: RepairLog = RepairLog()
    var wofLog: WofLog = WofLog()
    var regLog: RegLog = RegLog()

    fun getLatestOdometerReading() : Int {
        var odometerReadings = fuelLog.returnFuelLog()
        odometerReadings = ArrayList(odometerReadings.filter { log -> log.odometerReading > 0 })
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
        logs.addAll(insuranceLog.returnInsuranceBillLogs())
        logs.sortByDescending { loggable -> loggable.sortableDate.time }

        return logs
    }

    fun returnExpensesLogsWithinFinancialYear() : ArrayList<Loggable> {
        val expensesLogs : ArrayList<Loggable> = returnExpensesLogs()
        return ArrayList(expensesLogs.filter { loggable -> isWithinFinancialYear(loggable.sortableDate) })
    }

    fun returnExpensesWithinFinancialYear() : BigDecimal {
        var total : BigDecimal = 0.0.toBigDecimal()
        for (expenseLog in returnExpensesLogsWithinFinancialYear()) {
            total += expenseLog.unitPrice;
        }

        return total
    }

    private fun isWithinFinancialYear(loggableDate : Date) : Boolean {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        var maxDate: Date
        var minDate: Date

        val format: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

        if (month < 3) {
            maxDate = format.parse("01/04/" + year)
            minDate = format.parse("31/03/" + (year - 1))
        } else {
            maxDate = format.parse("01/04/" + (year + 1))
            minDate = format.parse("31/03/" + year)
        }

        return loggableDate.before(maxDate) and loggableDate.after(minDate)
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

        Thread {
            MainActivity.getDatabase()
                ?.insuranceDao()
                ?.insert(insurance.convertToInsuranceEntity())
        }.start()
    }

    fun logFuel(fuel: Fuel) {
        fuelLog.addFuelToFuelLog(fuel)

        Thread {
            MainActivity.getDatabase()
                ?.fuelDao()
                ?.insert(fuel.convertToFuelEntity())
            MainActivity.getDatabase()
                ?.loggableDao()
                ?.insert(fuel.convertToLoggableEntity())
        }.start()
    }

    fun returnFuelLog() : ArrayList<Fuel> {
        return fuelLog.returnFuelLog()
    }

    fun logService(service: Service) {
        serviceLog.addServiceToServiceLog(service)

        Thread {
            MainActivity.getDatabase()
                ?.serviceDao()
                ?.insert(service.convertToServiceEntity())
            MainActivity.getDatabase()
                ?.loggableDao()
                ?.insert(service.convertToLoggableEntity())
        }.start()
    }

    fun logRepair(repair: Repair) {
        repairLog.addRepairToRepairLog(repair)

        Thread {
            MainActivity.getDatabase()
                ?.repairDao()
                ?.insert(repair.convertToRepairEntity())
            MainActivity.getDatabase()
                ?.loggableDao()
                ?.insert(repair.convertToLoggableEntity())
        }.start()
    }

    fun logWof(wof: Wof) {
        wofLog.addWofToWofLog(wof)

        Thread {
            MainActivity.getDatabase()
                ?.wofDao()
                ?.insert(wof.convertToWofEntity())
            MainActivity.getDatabase()
                ?.loggableDao()
                ?.insert(wof.convertToLoggableEntity())
        }.start()
    }

    fun logReg(reg: Reg) {
        regLog.addRegToRegLog(reg)

        Thread {
            MainActivity.getDatabase()
                ?.regDao()
                ?.insert(reg.convertToRegEntity())
            MainActivity.getDatabase()
                ?.loggableDao()
                ?.insert(reg.convertToLoggableEntity())
        }.start()
    }

    fun hasInsurance() : Boolean {
        return insuranceLog.returnInsuranceLog().isNotEmpty()
    }

    fun returnLatestInsurancePolicy() : Insurance {
        insuranceLog.returnInsuranceLog().sortByDescending {x -> x.insurancePolicyStartDate}
        return insuranceLog.returnInsuranceLog().first()
    }

    private val format: SimpleDateFormat = SimpleDateFormat("dd/MMM/yyyy")

    fun returnWofExpiry(): String {
        val wofLogItems = wofLog.returnWofLog()

        if (wofLogItems.isEmpty())
            return format.format(expiryWOF)

        wofLogItems.sortByDescending { wof -> wof.wofDate }
        return format.format(wofLogItems.first().wofDate)
    }

    fun returnRegExpiry(): String {
        val regLogItems = regLog.returnRegLog()

        if (regLogItems.isEmpty())
            return format.format(regExpiry)

        regLogItems.sortByDescending { reg -> reg.newRegExpiryDate }
        return format.format(regLogItems.first().newRegExpiryDate)
    }

    fun convertToVehicleEntity() : VehicleEntity {
        val vehicleEntity = VehicleEntity(id, brandName, modelName, year, expiryWOF, regExpiry, odometer)
        return vehicleEntity
    }

    companion object {
        fun castVehicleEntities(vehicleEntities : List<VehicleEntity>?) : List<Vehicle> {
            val vehicleList = ArrayList<Vehicle>(0)

            if (vehicleEntities == null)
                return vehicleList.toList()

            for (vehicleEntity in vehicleEntities){
                vehicleList.add(vehicleEntity.convertToVehicleObject())
            }

            return vehicleList
        }
    }
}