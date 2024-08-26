package com.motologr.data.objects.insurance

import com.motologr.MainActivity
import com.motologr.data.logging.Log
import com.motologr.data.logging.Loggable
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
    var endDt : Date
        get() {
            val calendar = Calendar.getInstance()
            calendar.set(insurancePolicyStartDate.year + 1900 + 1, insurancePolicyStartDate.month, insurancePolicyStartDate.date, 0, 0, 0)
            return calendar.time
        }
        set(value){
            endDt = value
        }

    init {
        val calendar = Calendar.getInstance()
        calendar.set(insurancePolicyStartDate.year + 1900, insurancePolicyStartDate.month, insurancePolicyStartDate.date, 0, 0, 0)
        insurancePolicyStartDate = calendar.time

        calendar.set(lastBill.year + 1900, lastBill.month, lastBill.date, 0, 0, 0)
        lastBill = calendar.time
    }

    var insuranceBillLog : InsuranceBillLog = InsuranceBillLog()

    fun generateInsuranceBills() {
        val calendar : Calendar = Calendar.getInstance()

        val policyStartDate = insurancePolicyStartDate

        // Work our way backwards from last billing date
        var firstBillingDate = lastBill

        calendar.set(firstBillingDate.year + 1900, firstBillingDate.month, firstBillingDate.date, 0, 0, 0)

        while (firstBillingDate.time > policyStartDate.time) {
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

        calendar.set(firstBillingDate.year + 1900, firstBillingDate.month, firstBillingDate.date, 0, 0, 0)

        if (billingCycle == 2) {
            val insuranceBill = InsuranceBill(lastBill, billing, id, vehicleId)
            insuranceBillLog.addInsuranceBillToInsuranceBillLog(insuranceBill)
            return
        }

        var billingDate = firstBillingDate

        calendar.set(firstBillingDate.year + 1900, firstBillingDate.month, firstBillingDate.date, 0, 0, 0)

        var numberBills = 0
        while (billingDate < policyEndDate) {
            if (billingCycle == 0) {
                calendar.add(Calendar.DATE, 14)
            } else if (billingCycle == 1) {
                calendar.add(Calendar.MONTH, 1)
            } else if (billingCycle == 2) {
                calendar.add(Calendar.YEAR, 1)
            }

            if (billingDate < policyEndDate && billingDate >= policyStartDate) {
                val insuranceBill = InsuranceBill(billingDate, billing, id, vehicleId)

                if ((numberBills < 26 && billingCycle == 0) || (numberBills < 12 && billingCycle == 1)) {
                    insuranceBillLog.addInsuranceBillToInsuranceBillLog(insuranceBill)
                    numberBills += 1
                }
            }

            billingDate = calendar.time
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

        val insuranceBills = insuranceBillLog.returnInsuranceBillLog()
        insuranceBills.sortBy{ x -> x.billingDate.time }

        for (insuranceBilling in insuranceBills) {
            if (insuranceBilling.billingDate > Calendar.getInstance().time) {
                return insuranceBilling.billingDate
            }
        }

        val calendar : Calendar = Calendar.getInstance()

        return calendar.time
    }

    fun getNextBillingDateString() : String {
        val format: SimpleDateFormat = SimpleDateFormat("dd/MM/yy")

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
        val fuelEntity = InsuranceEntity(id, insurer, insurancePolicyStartDate, coverage, billingCycle, billing, lastBill, vehicleId)
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