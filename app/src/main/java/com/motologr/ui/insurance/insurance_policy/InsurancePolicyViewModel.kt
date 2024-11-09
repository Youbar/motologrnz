package com.motologr.ui.insurance.insurance_policy

import android.os.Bundle
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.motologr.data.DataHelper
import com.motologr.data.DataManager

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

    var isPolicyCancelled = mutableStateOf(false)
        private set

    var insuranceId = -1
        private set

    fun initInsurancePolicyViewModel(arguments: Bundle?) {
        var argumentInsuranceId = -1
        val policyIndex = arguments?.getInt("position", -1) ?: -1

        if (policyIndex == -1)
            argumentInsuranceId = arguments?.getInt("insuranceId", -1) ?: -1

        if (policyIndex == -1 && argumentInsuranceId == -1)
            return

        val insurance = if (policyIndex != -1) {
            DataManager.returnActiveVehicle()?.insuranceLog?.returnInsurance(policyIndex)
        } else {
            DataManager.returnActiveVehicle()?.insuranceLog?.returnInsuranceById(argumentInsuranceId)
        }

        if (insurance == null)
            return

        insuranceId = insurance.id
        insurerName.value = insurance.insurer
        startDate.value = "Start Date - ${DataHelper.formatNumericalDateFormat(insurance.insurancePolicyStartDate)}"
        endDate.value = "End Date - ${DataHelper.formatNumericalDateFormat(insurance.insurancePolicyEndDate)}"
        pricing.value = "Pricing - $${DataHelper.roundToTwoDecimalPlaces(insurance.billing)} ${insurance.returnCycleType()}"
        coverage.value = "Coverage - ${insurance.returnCoverageType()}"
        isPolicyCancelled.value = insurance.isCancelled
    }

    var displayToastMessage = { message : String ->

    }

    var navigateToVehicle = {

    }

    var isDisplayDeleteDialog = mutableStateOf(false)
        private set

    var onManageBillsClick = { _ : Int ->

    }

    var onCancelPolicyClick = { _ : Int ->

    }

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