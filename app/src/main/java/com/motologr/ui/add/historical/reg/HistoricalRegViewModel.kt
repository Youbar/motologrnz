package com.motologr.ui.add.historical.reg

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.motologr.data.DataHelper
import com.motologr.data.DataManager
import com.motologr.data.objects.reg.Reg
import com.motologr.data.objects.vehicle.Vehicle
import kotlin.math.roundToInt

class HistoricalRegViewModel : ViewModel() {
    companion object {
        private const val HISTORICAL_REG = "Historical Registration"
        private const val UPDATE_REG = "Update Registration"
        const val HISTORICAL_REG_BUTTON = "Record"
        const val UPDATE_REG_BUTTON = "Update"
    }

    val regTitle
        get() : String {
            return if (isHistorical.value)
                HISTORICAL_REG
            else
                UPDATE_REG
        }

    var regPrice = mutableStateOf("")
        private set

    var sliderPosition = mutableFloatStateOf(0f)
        private set

    var historicalRegDate = mutableStateOf("")
        private set

    var oldRegExpiryDate = mutableStateOf("")
        private set

    var newRegExpiryDate = mutableStateOf("")

    var isHistorical = mutableStateOf(false)
        private set

    fun initViewModel(activeVehicle : Vehicle, isHistorical : Boolean) {
        this.isHistorical.value = isHistorical

        val vehicleRegExpiryDate = activeVehicle.returnRegExpiryDate()
        oldRegExpiryDate.value = DataHelper.formatNumericalDateFormat(vehicleRegExpiryDate)
    }

    var isReadOnly = mutableStateOf(false)
        private set

    private var regId = -1

    val isExistingData : Boolean
        get() {
            return regId != -1
        }

    fun setViewModelToReadOnly(reg: Reg) {
        regId = reg.id
        isReadOnly.value = true
        isHistorical.value = reg.isHistorical

        if (isHistorical.value) {
            historicalRegDate.value = DataHelper.formatNumericalDateFormat(reg.purchaseDate)
        } else {
            oldRegExpiryDate.value = DataHelper.formatNumericalDateFormat(reg.regExpiryDate)
            newRegExpiryDate.value = DataHelper.formatNumericalDateFormat(reg.newRegExpiryDate)
        }

        sliderPosition.value = reg.monthsExtended.toFloat()
        regPrice.value = reg.price.toString()
    }

    var displayToastMessage = { message : String -> }

    var navigateToVehicle = {

    }

    private fun isValidInputs() : Boolean {
        if (isHistorical.value) {
            if (!DataHelper.isValidStringInput(historicalRegDate.value, "Purchase Date", displayToastMessage))
                return false
        } else {
            if (!DataHelper.isValidStringInput(oldRegExpiryDate.value, "Old Expiry Date", displayToastMessage))
                return false
            if (!DataHelper.isValidStringInput(newRegExpiryDate.value, "New Expiry Date", displayToastMessage))
                return false
        }

        if (sliderPosition.floatValue.roundToInt() == 0) {
            displayToastMessage("You cannot record 0 months of registration")
            return false
        }

        if (!DataHelper.isValidCurrencyInput(regPrice.value, "Purchase Price", displayToastMessage))
            return false

        return true
    }

    private fun getRegObjectFromInputs() : Reg? {
        if (isValidInputs()) {
            val regPrice = regPrice.value
                .replace(",","").toBigDecimal()
            val registrationMonths = sliderPosition.floatValue.roundToInt()
            val activeVehicle = DataManager.returnActiveVehicle()!!

            if (isHistorical.value) {
                val minDt = DataHelper.getMinDt()
                val purchaseDate = DataHelper.parseNumericalDateFormat(historicalRegDate.value)
                return Reg(minDt, minDt, registrationMonths, regPrice, activeVehicle.id, purchaseDate, isHistorical.value)
            } else {
                val newRegExpiryDate = DataHelper.parseNumericalDateFormat(newRegExpiryDate.value)
                val oldRegExpiryDate = DataHelper.parseNumericalDateFormat(oldRegExpiryDate.value)
                return Reg(newRegExpiryDate, oldRegExpiryDate, registrationMonths, regPrice, activeVehicle.id, oldRegExpiryDate, isHistorical.value)
            }
        }

        return null
    }

    var onRecordClick = {
        if (isValidInputs()) {
            val reg = getRegObjectFromInputs()

            if (reg != null) {
                DataManager.returnActiveVehicle()?.logReg(reg)
                displayToastMessage("Registration saved")
                navigateToVehicle()
            }
        }
    }

    val onEditClick = {
        isReadOnly.value = false
    }

    val onSaveClick = {
        val reg = getRegObjectFromInputs()

        if (reg != null) {
            reg.id = regId
            DataManager.returnActiveVehicle()?.updateReg(reg)
            displayToastMessage("Changes saved.")
            isReadOnly.value = true
        }
    }

    var isDisplayDeleteDialog = mutableStateOf(false)

    val onDeleteClick = {
        isDisplayDeleteDialog.value = true
    }

    val onConfirmClick = {
        DataManager.returnActiveVehicle()?.deleteReg(regId)
        displayToastMessage("Reg record deleted.")
        navigateToVehicle()
    }

    val onDismissClick = {
        isDisplayDeleteDialog.value = false
        displayToastMessage("Deletion cancelled.")
    }
}