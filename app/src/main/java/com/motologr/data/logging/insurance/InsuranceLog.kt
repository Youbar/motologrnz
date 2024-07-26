package com.motologr.data.logging.insurance

import com.motologr.data.logging.Log
import com.motologr.data.objects.insurance.Insurance
import com.motologr.data.objects.insurance.InsuranceBill
import com.motologr.data.objects.insurance.InsuranceBillEntity
import com.motologr.data.objects.insurance.InsuranceEntity

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

    fun returnInsuranceBillLogs(): ArrayList<InsuranceBill> {
        val insuranceBills = ArrayList<InsuranceBill>()

        for (insurance in insuranceLog) {
            insuranceBills.addAll(insurance.returnInsuranceBillingLogs().returnInsuranceBillLog())
        }

        return insuranceBills
    }

    companion object {
        fun castInsuranceLoggableEntities(insuranceEntities : List<InsuranceEntity>?, insuranceBillEntities : List<InsuranceBillEntity>?) : InsuranceLog {
            val insuranceLog = InsuranceLog()

            if (insuranceEntities == null || insuranceBillEntities == null)
                return insuranceLog

            for (insuranceEntity in insuranceEntities){
                val insurance = insuranceEntity.convertToInsuranceObject()
                insurance.generateInsuranceBills(insuranceBillEntities)

                insuranceLog.addInsuranceToInsuranceLog(insurance)
            }

            return insuranceLog
        }
    }
}