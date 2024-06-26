package com.motologr.ui.data.logging.insurance

import com.motologr.ui.data.logging.Log
import com.motologr.ui.data.objects.insurance.Insurance

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