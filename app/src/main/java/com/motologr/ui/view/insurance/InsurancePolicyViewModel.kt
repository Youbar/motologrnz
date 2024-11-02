package com.motologr.ui.view.insurance

import android.os.Bundle
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.motologr.data.DataHelper
import com.motologr.data.DataManager
import com.motologr.data.objects.insurance.Insurance

class InsurancePolicyViewModel : ViewModel() {
    var insurerName = mutableStateOf("")
        private set

    var startDate = mutableStateOf("")
        private set

    var endDate = mutableStateOf("")
        private set

    var pricing = mutableStateOf("")
        private set

    var coverage = mutableStateOf("")
        private set

    private var insuranceId = -1

    fun initInsurancePolicyViewModel(arguments: Bundle?) {
        var argumentInsuranceId = -1
        val policyIndex = arguments?.getInt("position")

        if (policyIndex == null)
            argumentInsuranceId = arguments?.getInt("insuranceId") ?: -1

        if (policyIndex == null && argumentInsuranceId == -1)
            return

        val insurance = if (policyIndex != null) {
            DataManager.returnActiveVehicle()?.insuranceLog?.returnInsurance(policyIndex)
        } else {
            DataManager.returnActiveVehicle()?.insuranceLog?.returnInsuranceById(insuranceId)
        }

        if (insurance == null)
            return

        insuranceId = insurance.id
        insurerName.value = insurance.insurer
        startDate.value = "Start Date - ${DataHelper.formatNumericalDateFormat(insurance.insurancePolicyStartDate)}"
        endDate.value = "End Date - ${DataHelper.formatNumericalDateFormat(insurance.endDt)}"
        pricing.value = "Pricing - $${DataHelper.roundToTwoDecimalPlaces(insurance.billing)} ${insurance.returnCycleType()}"
        coverage.value = "Coverage - ${insurance.returnCoverageType()}"
    }

    var displayToastMessage = { message : String ->

    }

    var navigateToVehicle = {

    }

    var isDisplayDeleteDialog = mutableStateOf(false)
        private set

    val onDeleteClick = {
        isDisplayDeleteDialog.value = true
    }

    val onDismissClick = {
        isDisplayDeleteDialog.value = false
    }

    val onConfirmClick = {
        DataManager.returnActiveVehicle()?.deleteInsurance(insuranceId)
        displayToastMessage("Insurance policy deleted.")
        navigateToVehicle()
    }
}