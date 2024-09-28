package com.motologr.data.objects.vehicle

import com.motologr.MainActivity
import com.motologr.data.DataHelper
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
import com.motologr.data.objects.ruc.Ruc
import com.motologr.data.objects.ruc.RucEntity
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class Vehicle (val id: Int, brandName: String, modelName: String, modelYear: Int,
               private var expiryWOF: Date, private var regExpiry: Date, private var odometer: Int) {

    private val vehicleSettings : VehicleSettings = VehicleSettings(brandName, modelName, modelYear, id)

    val brandName : String
        get() {
            return vehicleSettings.brandName
        }

    val modelName : String
        get() {
            return vehicleSettings.modelName
        }

    val modelYear : Int
        get() {
            return vehicleSettings.modelYear
        }

    val isUseRoadUserCharges : Boolean
        get() {
            return vehicleSettings.isUseRoadUserCharges
        }

    val roadUserChargesHeld : Int
        get() {
            return vehicleSettings.roadUserChargesHeld
        }

    fun convertVehicleEntityToVehicleSettings(vehicleEntity: VehicleEntity) {
        vehicleSettings.isUseRoadUserCharges = vehicleEntity.isUseRoadUserCharges
        vehicleSettings.roadUserChargesHeld = vehicleEntity.roadUserChargesHeld
    }

    fun updateVehicleName(brandName: String, modelName : String, modelYear : Int) {
        vehicleSettings.brandName = brandName
        vehicleSettings.modelName = modelName
        vehicleSettings.modelYear = modelYear

        Thread {
            MainActivity.getDatabase()
                ?.vehicleDao()
                ?.updateVehicleName(brandName, modelName, modelYear, this.id)
        }.start()
    }

    fun updateRucs(isUseRoadUserCharges: Boolean, roadUserChargesHeld : Int) {
        vehicleSettings.isUseRoadUserCharges = isUseRoadUserCharges
        vehicleSettings.roadUserChargesHeld = roadUserChargesHeld

        Thread {
            MainActivity.getDatabase()
                ?.vehicleDao()
                ?.updateVehicleRUCs(isUseRoadUserCharges, roadUserChargesHeld, this.id)
        }.start()
    }

    var vehicleImage: Int = 0

    var insuranceLog: InsuranceLog = InsuranceLog()
    var fuelLog: FuelLog = FuelLog()
    var serviceLog: ServiceLog = ServiceLog()
    var repairLog: RepairLog = RepairLog()
    var wofLog: WofLog = WofLog()
    var regLog: RegLog = RegLog()
    var rucLog : ArrayList<Ruc> = arrayListOf()

    fun isMeetingCompliance() : Boolean {
        return !(DataHelper.isMinDt(expiryWOF) || DataHelper.isMinDt(regExpiry))
    }

    fun submitCompliance(expiryWOF: Date, expiryReg: Date) {
        this.expiryWOF = expiryWOF
        this.regExpiry = expiryReg

        Thread {
            MainActivity.getDatabase()
                ?.vehicleDao()
                ?.updateVehicleCompliance(expiryWOF, expiryReg, this.id)
        }.start()
    }

    fun getLatestOdometerReading() : Int {
        var odometerReadings = fuelLog.returnFuelLog()
        odometerReadings = ArrayList(odometerReadings.filter { log -> log.odometerReading > 0 })
        if (odometerReadings.isEmpty())
            return odometer

        odometerReadings.sortByDescending { log -> log.odometerReading }
        val firstOdometerReading = odometerReadings.first().odometerReading

        if (firstOdometerReading > odometer)
            return firstOdometerReading

        return odometer
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
        logs.addAll(rucLog)
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

    fun returnCurrentExpensesWithinFinancialYear() : BigDecimal {
        var total : BigDecimal = 0.0.toBigDecimal()

        val calendar = Calendar.getInstance()
        for (expenseLog in returnExpensesLogsWithinFinancialYear()) {
            if (expenseLog.sortableDate <= calendar.time)
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
        Thread {
            MainActivity.getDatabase()
                ?.insuranceDao()
                ?.insert(insurance.convertToInsuranceEntity())

            insurance.generateInsuranceBills()
            insuranceLog.addInsuranceToInsuranceLog(insurance)
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

    fun logRuc(ruc : Ruc) {
        rucLog.add(ruc)

        Thread {
            MainActivity.getDatabase()
                ?.rucDao()
                ?.insert(ruc.convertToRucEntity())
            MainActivity.getDatabase()
                ?.loggableDao()
                ?.insert(ruc.convertToLoggableEntity())
        }.start()
    }

    fun hasCurrentInsurance() : Boolean {
        val calendar = Calendar.getInstance()

        val insuranceLog = insuranceLog.returnInsuranceLog()

        if (insuranceLog.size < 1)
            return false

        insuranceLog.sortByDescending {x -> x.endDt.time }

        return calendar.time <= insuranceLog.first().endDt
    }

    fun returnLatestInsurancePolicy() : Insurance {
        insuranceLog.returnInsuranceLog()
            .sortByDescending {x -> x.insurancePolicyStartDate.time }
        return insuranceLog.returnInsuranceLog().first()
    }

    private val format: SimpleDateFormat = SimpleDateFormat("dd/MMM/yyyy")

    fun returnWofExpiry(): String {
        if (!isMeetingCompliance())
            return "N/A"

        val wofLogItems = wofLog.returnWofLog().filter { wof -> !wof.isHistorical }

        if (wofLogItems.isEmpty())
            return format.format(expiryWOF)

        wofLogItems.sortedByDescending { wof -> wof.wofDate.time }
        return format.format(wofLogItems.first().wofDate)
    }

    fun returnRegExpiry(): String {
        if (!isMeetingCompliance())
            return "N/A"

        val regLogItems = regLog.returnRegLog().filter { reg -> !reg.isHistorical}

        if (regLogItems.isEmpty())
            return format.format(regExpiry)

        regLogItems.sortedByDescending { reg -> reg.newRegExpiryDate.time }
        return format.format(regLogItems.first().newRegExpiryDate)
    }

    fun returnLatestRucUnits(): String {
        if (!isMeetingCompliance())
            return "N/A"

        var rucLogItems = rucLog.filter { ruc -> !ruc.isHistorical }

        if (rucLogItems.isEmpty())
            return roadUserChargesHeld.toString()

        rucLogItems.sortedByDescending { ruc -> ruc.unitsHeldAfterTransaction }
        return rucLogItems.first().unitsHeldAfterTransaction.toString()
    }

    fun convertToVehicleEntity() : VehicleEntity {
        val vehicleEntity = VehicleEntity(id, brandName, modelName, modelYear, expiryWOF, regExpiry, odometer, vehicleImage, isUseRoadUserCharges, roadUserChargesHeld)
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

        fun castRucLoggableEntities(rucEntities : List<RucEntity>?) : ArrayList<Ruc> {
            val rucLog = ArrayList<Ruc>()

            if (rucEntities == null)
                return rucLog

            for (rucEntity in rucEntities){
                rucLog.add(rucEntity.convertToRucObject())
            }

            return rucLog
        }
    }
}