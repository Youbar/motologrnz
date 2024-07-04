package com.motologr.ui.data.objects.insurance

import com.motologr.MainActivity
import com.motologr.ui.data.logging.Log
import com.motologr.ui.data.logging.Loggable
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class Insurance (var id : Int,
                 var insurer: String,
                 var insurancePolicyStartDate: Date,
                 var coverage: Int,
                 var billingCycle: Int,
                 var billing: BigDecimal,
                 var lastBill: Date,
                 var vehicleId: Int
) {

    var insuranceBillLog : InsuranceBillLog = InsuranceBillLog()

    fun generateInsuranceBills() {
        val calendar : Calendar = Calendar.getInstance()

        val policyStartDate = insurancePolicyStartDate

        // Work our way backwards from last billing date
        var firstBillingDate = lastBill

        calendar.set(firstBillingDate.year + 1900, firstBillingDate.month, firstBillingDate.date)

        while (firstBillingDate > policyStartDate) {
            if (billingCycle == 0) {
                calendar.add(Calendar.DATE, -14)
            } else if (billingCycle == 1) {
                calendar.add(Calendar.MONTH, -1)
            } else if (billingCycle == 2) {
                calendar.add(Calendar.YEAR, -1)
            }

            firstBillingDate = calendar.time
        }

        // Then forwards
        calendar.set(policyStartDate.year + 1900 + 1, policyStartDate.month, policyStartDate.date, 0, 0, 0)
        val policyEndDate = calendar.time

        calendar.set(firstBillingDate.year + 1900, firstBillingDate.month, firstBillingDate.date)

        if (billingCycle == 2) {
            val insuranceBill = InsuranceBill(lastBill, billing, id, vehicleId)
            insuranceBillLog.addInsuranceBillToInsuranceBillLog(insuranceBill)
            return
        }

        var billingMultiplier = 1
        var billingDate = firstBillingDate

        while (billingDate < policyEndDate) {
            calendar.set(firstBillingDate.year + 1900, firstBillingDate.month, firstBillingDate.date)

            if (billingCycle == 0) {
                calendar.add(Calendar.DATE, 14 * billingMultiplier)
            } else if (billingCycle == 1) {
                calendar.add(Calendar.MONTH, 1 * billingMultiplier)
            } else if (billingCycle == 2) {
                calendar.add(Calendar.YEAR, 1 * billingMultiplier)
            }

            if (billingDate < policyEndDate) {
                val insuranceBill = InsuranceBill(billingDate, billing, id, vehicleId)

                if (billingCycle != 0 || billingMultiplier < 27)
                    insuranceBillLog.addInsuranceBillToInsuranceBillLog(insuranceBill)
            }

            billingDate = calendar.time
            billingMultiplier += 1
        }
    }

    fun generateInsuranceBills(insuranceBillEntities: List<InsuranceBillEntity>) {
        for (insuranceBillEntity in insuranceBillEntities) {
            if (insuranceBillEntity.insuranceId == id)
                insuranceBillLog.addInsuranceBillToInsuranceBillLog(insuranceBillEntity)
        }
    }

    fun returnInsuranceBillingLogs() : InsuranceBillLog {
        return insuranceBillLog
    }

    fun getNextBillingDate() : Date {

        for (insuranceBilling in insuranceBillLog.returnInsuranceBillLog()) {
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

    fun convertToInsuranceEntity(): InsuranceEntity {
        val fuelEntity = InsuranceEntity(insurer, insurancePolicyStartDate, coverage, billingCycle, billing, lastBill, vehicleId)
        return fuelEntity
    }
}

class InsuranceBillLog : Log() {
    private var insuranceBillLog = ArrayList<InsuranceBill>()

    fun addInsuranceBillToInsuranceBillLog(insuranceBill: InsuranceBill) {
        insuranceBillLog.add(insuranceBill)

        Thread {
            MainActivity.getDatabase()
                ?.insuranceBillDao()
                ?.insert(insuranceBill.convertToInsuranceBillEntity())
        }.start()
    }

    fun addInsuranceBillToInsuranceBillLog(insuranceBillEntity: InsuranceBillEntity) {
        insuranceBillLog.add(insuranceBillEntity.convertToInsuranceBillObject())
    }

    fun returnInsuranceBillLog(): ArrayList<InsuranceBill> {
        return insuranceBillLog
    }

    fun returnInsurance(index: Int) : InsuranceBill {
        return insuranceBillLog[index]
    }

    companion object {
        fun castInsuranceBillLoggableEntities(insuranceBillEntities : List<InsuranceBillEntity>?) : InsuranceBillLog {
            val insuranceBillLog = InsuranceBillLog()

            if (insuranceBillEntities == null)
                return insuranceBillLog

            for (insuranceBillEntity in insuranceBillEntities){
                insuranceBillLog.addInsuranceBillToInsuranceBillLog(insuranceBillEntity)
            }

            return insuranceBillLog
        }
    }
}

class InsuranceBill(var billingDate: Date,
                    var price: BigDecimal,
                    var insuranceId: Int,
                    override var vehicleId: Int) : Loggable(billingDate, 201, price, vehicleId) {
    fun convertToInsuranceBillEntity() : InsuranceBillEntity {
        val fuelEntity = InsuranceBillEntity(billingDate, price, insuranceId, vehicleId)
        return fuelEntity
    }
}