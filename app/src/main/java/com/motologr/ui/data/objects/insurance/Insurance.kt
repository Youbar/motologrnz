package com.motologr.ui.data.objects.insurance

import com.motologr.ui.data.DataManager
import com.motologr.ui.data.logging.Log
import com.motologr.ui.data.logging.Loggable
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class Insurance (var insurer: String,
                 var insurancePolicyStartDate: Date,
                 var coverage: Int,
                 var billingCycle: Int,
                 var billing: BigDecimal,
                 var lastBill: Date,
                 override var vehicleId: Int
) : Loggable(insurancePolicyStartDate, 200, billing, vehicleId) {

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
        val vehicleId: Int = DataManager.ReturnActiveVehicle()?.id!!

        while (lastBillingDate < policyEndDate) {
            if (billingCycle == 0) {
                calendar.add(Calendar.DATE, 14)
            } else if (billingCycle == 1) {
                calendar.add(Calendar.MONTH, 1)
            } else if (billingCycle == 2) {
                calendar.add(Calendar.YEAR, 1)
            }

            lastBillingDate = calendar.time

            val insuranceBilling = InsuranceBilling(lastBillingDate, billing, vehicleId)
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

class InsuranceBilling(var billingDate: Date, var price: BigDecimal, override var vehicleId: Int) : Loggable(billingDate, 201, price, vehicleId)