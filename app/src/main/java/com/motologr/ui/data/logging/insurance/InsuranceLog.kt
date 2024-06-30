package com.motologr.ui.data.logging.insurance

import com.motologr.ui.data.logging.Log
import com.motologr.ui.data.objects.insurance.Insurance
import com.motologr.ui.data.objects.insurance.InsuranceBillEntity
import com.motologr.ui.data.objects.insurance.InsuranceEntity

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

    companion object {
        fun castInsuranceLoggableEntities(insuranceEntities : List<InsuranceEntity>?, insuranceBillEntities : List<InsuranceBillEntity>?) : InsuranceLog {
            val insuranceLog = InsuranceLog()

            if (insuranceEntities == null || insuranceBillEntities == null)
                return insuranceLog

            for (insuranceEntity in insuranceEntities){
                val insurance = insuranceEntity.convertToInsuranceObject()
                insurance.generateInsuranceBills(insuranceBillEntities)

                insuranceLog.addInsuranceToInsuranceLog(insuranceEntity.convertToInsuranceObject())
            }

            return insuranceLog
        }
    }
}