package com.motologr.ui.add.insurance

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.motologr.data.DataHelper
import com.motologr.data.DataManager
import com.motologr.data.EnumConstants
import com.motologr.data.getDate
import com.motologr.data.objects.insurance.Insurance
import java.math.RoundingMode
import java.util.Date

class InsuranceViewModel : ViewModel() {
    var startDate = mutableStateOf("")
        private set

    var lastBillingDate = mutableStateOf("")
        private set

    var insurerName = mutableStateOf("")
        private set

    var isThirdChecked = mutableStateOf(false)
        private set

    var isThirdPlusChecked = mutableStateOf(false)
        private set

    var isComprehensiveChecked = mutableStateOf(false)
        private set

    var lastBillingAmount = mutableStateOf("")
        private set

    var isFortnightlyChecked = mutableStateOf(false)
        private set

    var isMonthlyChecked = mutableStateOf(false)
        private set

    var isAnnuallyChecked = mutableStateOf(false)
        private set

    private val coverageTypeInputs : Array<Boolean>
        get() : Array<Boolean> = arrayOf(isThirdChecked.value, isThirdPlusChecked.value, isComprehensiveChecked.value)

    val onCoverageChecked: (Int) -> Unit = { coverageType ->
        if (coverageType == EnumConstants.InsuranceCoverage.Third.ordinal) {
            isThirdPlusChecked.value = false
            isComprehensiveChecked.value = false
        } else if (coverageType == EnumConstants.InsuranceCoverage.ThirdPlus.ordinal) {
            isThirdChecked.value = false
            isComprehensiveChecked.value = false
        } else if (coverageType == EnumConstants.InsuranceCoverage.Comprehensive.ordinal) {
            isThirdChecked.value = false
            isThirdPlusChecked.value = false
        }
    }

    private fun returnCoverageChecked() : Int {
        if (isThirdChecked.value)
            return EnumConstants.InsuranceCoverage.Third.ordinal

        if (isThirdPlusChecked.value)
            return EnumConstants.InsuranceCoverage.ThirdPlus.ordinal

        if (isComprehensiveChecked.value)
            return EnumConstants.InsuranceCoverage.Comprehensive.ordinal

        return -1
    }

    private val billingCycleTypeInputs : Array<Boolean>
        get() : Array<Boolean> = arrayOf(isFortnightlyChecked.value, isMonthlyChecked.value, isAnnuallyChecked.value)

    val onBillingCycleChecked: (Int) -> Unit = { coverageType ->
        if (coverageType == EnumConstants.InsuranceBillingCycle.Fortnightly.ordinal) {
            isMonthlyChecked.value = false
            isAnnuallyChecked.value = false
        } else if (coverageType == EnumConstants.InsuranceBillingCycle.Monthly.ordinal) {
            isFortnightlyChecked.value = false
            isAnnuallyChecked.value = false
        } else if (coverageType == EnumConstants.InsuranceBillingCycle.Annually.ordinal) {
            isFortnightlyChecked.value = false
            isMonthlyChecked.value = false
        }
    }

    private fun returnBillingCycleChecked() : Int {
        if (isFortnightlyChecked.value)
            return EnumConstants.InsuranceBillingCycle.Fortnightly.ordinal

        if (isMonthlyChecked.value)
            return EnumConstants.InsuranceBillingCycle.Monthly.ordinal

        if (isAnnuallyChecked.value)
            return EnumConstants.InsuranceBillingCycle.Annually.ordinal

        return -1
    }

    var displayToastMessage = { message : String ->

    }

    var navigateToVehicle = {

    }

    private fun isValidInputs() : Boolean {
        if (!DataHelper.isValidStringInput(startDate.value, "Start Date", displayToastMessage))
            return false

        if (!DataHelper.isValidStringInput(lastBillingDate.value, "Last Billing Date", displayToastMessage))
            return false

        val startDate = DataHelper.parseNumericalDateFormat(startDate.value)
        val lastBillingDate = DataHelper.parseNumericalDateFormat(lastBillingDate.value)

        if (lastBillingDate.time < startDate.time) {
            displayToastMessage("Your last bill cannot be prior to your insurance start date")
            return false
        }

        if (coverageTypeInputs.none { f -> f }) {
            displayToastMessage("Please select a coverage type")
            return false
        }

        if (!DataHelper.isValidStringInput(insurerName.value, "Insurer Name", displayToastMessage))
            return false

        if (!DataHelper.isValidCurrencyInput(lastBillingAmount.value, "Last Billing Amount", displayToastMessage))
            return false

        if (billingCycleTypeInputs.none { f -> f }) {
            displayToastMessage("Please select a billing cycle")
            return false
        }

        return true
    }

    private fun getInsuranceObjectFromInputs() : Insurance? {
        if (isValidInputs()) {
            val id = DataManager.fetchIdForInsurance()
            val insurer = insurerName.value
            val startDate = DataHelper.parseNumericalDateFormat(startDate.value)
            val coverage = returnCoverageChecked()
            val billingCycle = returnBillingCycleChecked()
            val billing = lastBillingAmount.value
                .replace(",","").toBigDecimal().setScale(2, RoundingMode.HALF_UP)
            val lastBillingDate = DataHelper.parseNumericalDateFormat(lastBillingDate.value)
            val vehicleId = DataManager.returnActiveVehicle()?.id ?: -1

            return Insurance(id, insurer, startDate, coverage, billingCycle, billing, lastBillingDate, vehicleId)
        }

        return null
    }

    val onRecordClick = {
        val insurance = getInsuranceObjectFromInputs()

        if (insurance != null) {
            DataManager.returnActiveVehicle()?.logInsurance(insurance)
            navigateToVehicle()
        }
    }

    fun initInsuranceViewModel() {
        val currentDate = DataHelper.getCurrentDateString()
        startDate.value = currentDate
        lastBillingDate.value = currentDate
    }
}