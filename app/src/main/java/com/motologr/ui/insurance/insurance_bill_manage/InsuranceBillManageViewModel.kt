package com.motologr.ui.insurance.insurance_bill_manage

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.motologr.data.DataHelper
import com.motologr.data.DataManager
import com.motologr.data.objects.insurance.InsuranceBill
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Date

class InsuranceBillManageViewModel : ViewModel() {
    companion object {
        const val ADD_INSURANCE_BILL = "Add New Payment"
        const val UPDATE_INSURANCE_BILL = "Update Payment"
    }

    val insuranceBillCardTitle : String
        get() {
            if (isExistingData)
                return UPDATE_INSURANCE_BILL
            else
                return ADD_INSURANCE_BILL
        }

    var insuranceBillDate = mutableStateOf("")
        private set

    var insuranceBillPrice = mutableStateOf("")
        private set

    var displayToastMessage: (String) -> Unit = { message : String ->

    }

    private fun isValidInputs() : Boolean {
        if (!DataHelper.isValidStringInput(insuranceBillDate.value, "Payment Date", displayToastMessage))
            return false

        if (!DataHelper.isValidCurrencyInput(insuranceBillPrice.value, "Payment Amount", displayToastMessage))
            return false

        return true
    }

    var navigateToVehicle = { }

    private fun getInsuranceBillObjectFromInputs() : InsuranceBill? {
        if (isValidInputs()) {
            val vehicleId: Int = DataManager.returnActiveVehicle()?.id!!
            val billingDate: Date = DataHelper.parseNumericalDateFormat(insuranceBillDate.value)
            val price: BigDecimal = insuranceBillPrice.value
                .replace(",","").toBigDecimal().setScale(2, RoundingMode.HALF_UP)

            return InsuranceBill(billingDate, price, insuranceId, vehicleId)
        }

        return null
    }

    val onRecordClick = {
        val insuranceBill = getInsuranceBillObjectFromInputs()

        if (insuranceBill != null) {
            DataManager.returnActiveVehicle()?.logInsuranceBill(insuranceBill)
            navigateToVehicle()
        }
    }

    fun initViewModel(insuranceId : Int) {
        this.insuranceId = insuranceId
    }

    var isReadOnly = mutableStateOf(false)
        private set

    private var insuranceId = -1
    private var insuranceBillId = -1

    val isExistingData : Boolean
        get() {
            return insuranceBillId != -1
        }

    fun setViewModelToReadOnly(insuranceBill: InsuranceBill) {
        insuranceBillId = insuranceBill.id
        isReadOnly.value = true
        insuranceBillDate.value = DataHelper.formatNumericalDateFormat(insuranceBill.billingDate)
        insuranceBillPrice.value = insuranceBill.price.toString()
    }

    val onEditClick = {
        isReadOnly.value = false
    }

    val onSaveClick = {
        val insuranceBill = getInsuranceBillObjectFromInputs()

        if (insuranceBill != null) {
            insuranceBill.id = insuranceBillId
            DataManager.returnActiveVehicle()?.updateInsuranceBill(insuranceBill)
            displayToastMessage("Changes saved.")
            isReadOnly.value = true
        }
    }

    var isDisplayDeleteDialog = mutableStateOf(false)

    val onDeleteClick = {
        isDisplayDeleteDialog.value = true
    }

    val onConfirmClick = {
        DataManager.returnActiveVehicle()?.deleteInsuranceBill(insuranceId, insuranceBillId)
        displayToastMessage("Insurance payment deleted.")
        navigateToVehicle()
    }

    val onDismissClick = {
        isDisplayDeleteDialog.value = false
        displayToastMessage("Deletion cancelled.")
    }
}