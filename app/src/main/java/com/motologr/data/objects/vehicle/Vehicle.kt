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

    fun returnComplianceLogs() : ArrayList<Loggable> {
        val logs = ArrayList<Loggable>()

        logs.addAll(wofLog.returnWofLog())
        logs.addAll(regLog.returnRegLog())
        logs.addAll(rucLog)
        logs.sortByDescending { loggable -> loggable.sortableDate.time }

        return logs
    }

    fun returnMechanicalLogs() : ArrayList<Loggable> {
        val logs = ArrayList<Loggable>()

        logs.addAll(serviceLog.returnServiceLog())
        logs.addAll(repairLog.returnRepairLog())
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

    fun returnExpensesLogsWithinFinancialYear(financialYear : Int) : ArrayList<Loggable> {
        val expensesLogs : ArrayList<Loggable> = returnExpensesLogs()
        return ArrayList(expensesLogs.filter { loggable -> isWithinFinancialYear(loggable.sortableDate, financialYear) })
    }

    fun returnExpensesLogsWithinCurrentFinancialYear() : ArrayList<Loggable> {
        val expensesLogs : ArrayList<Loggable> = returnExpensesLogs()
        return ArrayList(expensesLogs.filter { loggable -> isWithinCurrentFinancialYear(loggable.sortableDate) })
    }

    fun returnExpensesWithinCurrentFinancialYear() : BigDecimal {
        var total : BigDecimal = 0.0.toBigDecimal()

        val calendar = Calendar.getInstance()
        for (expenseLog in returnExpensesLogsWithinCurrentFinancialYear()) {
            if (expenseLog.sortableDate <= calendar.time)
                total += expenseLog.unitPrice;
        }

        return total
    }

    fun returnAllExpensesWithinCurrentFinancialYear() : BigDecimal {
        var total : BigDecimal = 0.0.toBigDecimal()
        for (expenseLog in returnExpensesLogsWithinCurrentFinancialYear()) {
            total += expenseLog.unitPrice;
        }

        return total
    }

    private fun isWithinFinancialYear(loggableDate: Date, financialYear : Int) : Boolean {
        val format = SimpleDateFormat("dd/MM/yyyy")

        val maxDate = format.parse("01/04/$financialYear")
        val minDate = format.parse("31/03/${financialYear - 1}")

        return loggableDate.before(maxDate) && loggableDate.after(minDate)
    }

    private fun isWithinCurrentFinancialYear(loggableDate : Date) : Boolean {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val maxDate: Date
        val minDate: Date

        val format = SimpleDateFormat("dd/MM/yyyy")

        if (month < 3) {
            maxDate = format.parse("01/04/" + year)
            minDate = format.parse("31/03/" + (year - 1))
        } else {
            maxDate = format.parse("01/04/" + (year + 1))
            minDate = format.parse("31/03/" + year)
        }

        return loggableDate.before(maxDate) && loggableDate.after(minDate)
    }

    fun returnLoggableById(id: Int) : Loggable? {
        val loggable: Loggable? = returnComplianceLogs().find { loggable -> loggable.id == id }

        return loggable
    }

    fun returnLoggableByPosition(position : Int) : Loggable? {
        val loggable: Loggable? = returnMechanicalLogs()[position]

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

    fun updateFuel(fuel: Fuel) {
        fuelLog.returnFuelLog().removeIf { log -> log.id == fuel.id }
        fuelLog.addFuelToFuelLog(fuel)

        Thread {
            MainActivity.getDatabase()
                ?.fuelDao()
                ?.updateFuel(fuel.convertToFuelEntity())
            MainActivity.getDatabase()
                ?.loggableDao()
                ?.updateLoggable(fuel.convertToLoggableEntity())
        }.start()
    }

    fun deleteFuel(fuelId : Int) {
        fuelLog.returnFuelLog().removeIf { log -> log.id == fuelId }

        Thread {
            MainActivity.getDatabase()
                ?.fuelDao()
                ?.delete(fuelId)
            MainActivity.getDatabase()
                ?.loggableDao()
                ?.delete(fuelId)
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

    fun updateService(service: Service) {
        serviceLog.returnServiceLog().removeIf { log -> log.id == service.id }
        serviceLog.addServiceToServiceLog(service)

        Thread {
            MainActivity.getDatabase()
                ?.serviceDao()
                ?.updateService(service.convertToServiceEntity())
            MainActivity.getDatabase()
                ?.loggableDao()
                ?.updateLoggable(service.convertToLoggableEntity())
        }.start()
    }

    fun deleteService(serviceId : Int) {
        serviceLog.returnServiceLog().removeIf { log -> log.id == serviceId }

        Thread {
            MainActivity.getDatabase()
                ?.serviceDao()
                ?.delete(serviceId)
            MainActivity.getDatabase()
                ?.loggableDao()
                ?.delete(serviceId)
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

    fun updateRepair(repair: Repair) {
        repairLog.returnRepairLog().removeIf { log -> log.id == repair.id }
        repairLog.addRepairToRepairLog(repair)

        Thread {
            MainActivity.getDatabase()
                ?.repairDao()
                ?.updateRepair(repair.convertToRepairEntity())
            MainActivity.getDatabase()
                ?.loggableDao()
                ?.updateLoggable(repair.convertToLoggableEntity())
        }.start()
    }

    fun deleteRepair(repairId : Int) {
        repairLog.returnRepairLog().removeIf { log -> log.id == repairId }

        Thread {
            MainActivity.getDatabase()
                ?.repairDao()
                ?.delete(repairId)
            MainActivity.getDatabase()
                ?.loggableDao()
                ?.delete(repairId)
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

    fun updateWof(wof: Wof) {
        wofLog.returnWofLog().removeIf { log -> log.id == wof.id }
        wofLog.addWofToWofLog(wof)

        Thread {
            MainActivity.getDatabase()
                ?.wofDao()
                ?.updateWof(wof.convertToWofEntity())
            MainActivity.getDatabase()
                ?.loggableDao()
                ?.updateLoggable(wof.convertToLoggableEntity())
        }.start()
    }

    fun deleteWof(wofId : Int) {
        wofLog.returnWofLog().removeIf { log -> log.id == wofId }

        Thread {
            MainActivity.getDatabase()
                ?.wofDao()
                ?.delete(wofId)
            MainActivity.getDatabase()
                ?.loggableDao()
                ?.delete(wofId)
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

    fun updateReg(reg: Reg) {
        regLog.returnRegLog().removeIf { log -> log.id == reg.id }
        regLog.addRegToRegLog(reg)

        Thread {
            MainActivity.getDatabase()
                ?.regDao()
                ?.updateReg(reg.convertToRegEntity())
            MainActivity.getDatabase()
                ?.loggableDao()
                ?.updateLoggable(reg.convertToLoggableEntity())
        }.start()
    }

    fun deleteReg(regId : Int) {
        regLog.returnRegLog().removeIf { log -> log.id == regId }

        Thread {
            MainActivity.getDatabase()
                ?.regDao()
                ?.delete(regId)
            MainActivity.getDatabase()
                ?.loggableDao()
                ?.delete(regId)
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

    fun updateRuc(ruc: Ruc) {
        rucLog.removeIf { log -> log.id == ruc.id }
        rucLog.add(ruc)

        Thread {
            MainActivity.getDatabase()
                ?.rucDao()
                ?.updateRuc(ruc.convertToRucEntity())
            MainActivity.getDatabase()
                ?.loggableDao()
                ?.updateLoggable(ruc.convertToLoggableEntity())
        }.start()
    }

    fun deleteRuc(rucId : Int) {
        rucLog.removeIf { log -> log.id == rucId }

        Thread {
            MainActivity.getDatabase()
                ?.rucDao()
                ?.delete(rucId)
            MainActivity.getDatabase()
                ?.loggableDao()
                ?.delete(rucId)
        }.start()
    }

    fun hasCurrentInsurance() : Boolean {
        val calendar = Calendar.getInstance()

        val insuranceLog = insuranceLog.returnInsuranceLog()

        if (insuranceLog.size < 1)
            return false

        insuranceLog.sortByDescending {x -> x.endDt.time }

        if (returnLatestInsurancePolicy() != null)
            return calendar.time <= insuranceLog.first().endDt

        return false
    }

    fun returnLatestInsurancePolicy() : Insurance? {
        return insuranceLog.returnInsuranceLog().firstOrNull {x -> x.insurancePolicyStartDate <= DataHelper.getCurrentDate()
                && x.endDt >= DataHelper.getCurrentDate()}
    }

    private val ddMMyyyy : SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    private val ddMMMyyyy: SimpleDateFormat = SimpleDateFormat("dd/MMM/yyyy")

    fun returnWofExpiry(): String {
        if (!isMeetingCompliance())
            return "N/A"

        val wofLogItems = wofLog.returnWofLog().filter { wof -> !wof.isHistorical }

        if (wofLogItems.isEmpty())
            return ddMMMyyyy.format(expiryWOF)

        val sortedWofs = wofLogItems.sortedByDescending { wof -> wof.wofDate }
        return ddMMMyyyy.format(sortedWofs.first().wofDate)
    }

    fun returnWofExpiryDate(): Date {
        val wofLogItems = wofLog.returnWofLog().filter { wof -> !wof.isHistorical }

        if (wofLogItems.isEmpty())
            return expiryWOF

        val sortedWofs = wofLogItems.sortedByDescending { wof -> wof.wofDate.time }
        return sortedWofs.first().wofDate
    }

    fun returnRegExpiry(): String {
        if (!isMeetingCompliance())
            return "N/A"

        val regLogItems = regLog.returnRegLog().filter { reg -> !reg.isHistorical}

        if (regLogItems.isEmpty())
            return ddMMMyyyy.format(regExpiry)

        val sortedRegs = regLogItems.sortedByDescending { reg -> reg.newRegExpiryDate.time }
        return ddMMMyyyy.format(sortedRegs.first().newRegExpiryDate)
    }

    fun returnRegExpiryDate(): Date {
        val regLogItems = regLog.returnRegLog().filter { reg -> !reg.isHistorical}

        if (regLogItems.isEmpty())
            return regExpiry

        val sortedRegs = regLogItems.sortedByDescending { reg -> reg.newRegExpiryDate.time }
        return sortedRegs.first().newRegExpiryDate
    }

    fun returnLatestRucUnits(): String {
        if (!isMeetingCompliance())
            return "N/A"

        val rucLogItems = rucLog.filter { ruc -> !ruc.isHistorical }

        if (rucLogItems.isEmpty())
            return roadUserChargesHeld.toString()

        val sortedRucs = rucLogItems.sortedByDescending { ruc -> ruc.unitsHeldAfterTransaction }
        return sortedRucs.first().unitsHeldAfterTransaction.toString()
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