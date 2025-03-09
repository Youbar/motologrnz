package com.motologr.ui.insurance.insurance_policy_cancel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.motologr.data.DataHelper
import com.motologr.data.objects.insurance.Insurance
import com.motologr.data.objects.vehicle.Vehicle

class InsurancePolicyCancelViewModel : ViewModel() {
    var cancellationDate = mutableStateOf("")
        private set

    val insurancePolicyCancelCardTitle = "Cancel Policy"

    val insurancePolicyCancelCardText = "All insurance payments after this date will be deleted and your policy end date in the overview will be updated to match your cancellation date. " +
            "\n\nFinally, a new bill will be generated for the cancellation date. You can use this to specify any arrears or a repayment from your insurance provider."

    var displayToastMessage = { message : String ->

    }

    private fun isValidInputs() : Boolean {
        if (!DataHelper.isValidStringInput(cancellationDate.value, "Cancellation Date", displayToastMessage))
            return false

        // Cancellation date cannot be before insurance start date or after insurance end date

        return true
    }

    private fun cancelInsurancePolicy() {
        if (!isValidInputs())
            return

        val cancellationDate = DataHelper.parseNumericalDateFormat(cancellationDate.value)
        activeVehicle.cancelInsurance(insurance.id, cancellationDate)

        displayToastMessage("Insurance policy cancelled effective ${this.cancellationDate.value}")
        navigateBack()
    }

    private lateinit var activeVehicle : Vehicle

    private lateinit var insurance : Insurance

    fun initViewModel(activeVehicle : Vehicle, insurance: Insurance) {
        cancellationDate.value = DataHelper.getCurrentDateString()
        this.activeVehicle = activeVehicle
        this.insurance = insurance
    }

    var navigateBack = {

    }

    var onCancelPolicyClick = {
        cancelInsurancePolicy()
    }
}