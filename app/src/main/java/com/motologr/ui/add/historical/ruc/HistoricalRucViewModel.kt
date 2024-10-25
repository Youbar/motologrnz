package com.motologr.ui.add.historical.ruc

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.motologr.data.DataHelper
import com.motologr.data.DataManager
import com.motologr.data.objects.ruc.Ruc
import com.motologr.data.objects.vehicle.Vehicle
import kotlin.math.roundToInt

class HistoricalRucViewModel : ViewModel() {
    companion object {
        private const val HISTORICAL_RUC = "Historical Road User Charges"
        private const val UPDATE_RUC = "Update Road User Charges"
        const val HISTORICAL_RUC_BUTTON = "Record"
        const val UPDATE_RUC_BUTTON = "Update"
    }

    val rucTitle
        get() : String {
            return if (isHistorical.value)
                HISTORICAL_RUC
            else
                UPDATE_RUC
        }

    var rucPrice = mutableStateOf("")
        private set

    var sliderPosition = mutableFloatStateOf(0f)
        private set

    var purchaseDate = mutableStateOf("")
        private set

    var oldUnitsHeld = mutableStateOf("")
        private set

    var newUnitsHeld = mutableStateOf("")
        private set

    var isHistorical = mutableStateOf(false)
        private set

    fun initViewModel(activeVehicle : Vehicle, isHistorical : Boolean) {
        this.isHistorical.value = isHistorical

        purchaseDate.value = DataHelper.getCurrentDateString()
        val oldRucsHeld = activeVehicle.returnLatestRucUnits()
        oldUnitsHeld.value = oldRucsHeld
    }

    var isReadOnly = mutableStateOf(false)
        private set

    private var rucId = -1

    val isExistingData : Boolean
        get() {
            return rucId != -1
        }

    fun setViewModelToReadOnly(ruc: Ruc) {
        rucId = ruc.id
        isReadOnly.value = true
        isHistorical.value = ruc.isHistorical

        purchaseDate.value = DataHelper.formatNumericalDateFormat(ruc.purchaseDate)

        if (!isHistorical.value) {
            oldUnitsHeld.value = ruc.unitsHeldAfterTransaction.toString()
            newUnitsHeld.value = ruc.unitsHeldAfterTransaction.toString()
        }

        sliderPosition.value = ruc.unitsPurchased.toFloat()
        rucPrice.value = ruc.price.toString()
    }

    var displayToastMessage = { message : String -> }

    var navigateToVehicle = {

    }

    private fun isValidInputs() : Boolean {
        if (!DataHelper.isValidStringInput(purchaseDate.value, "Purchase Date", displayToastMessage))
            return false

        if (!isHistorical.value) {
            if (!DataHelper.isValidIntegerInput(oldUnitsHeld.value, "RUCs held", displayToastMessage))
                return false

            if (!DataHelper.isValidIntegerInput(newUnitsHeld.value, "New RUCs held", displayToastMessage))
                return false
        }

        if (sliderPosition.floatValue.roundToInt() == 0) {
            displayToastMessage("You cannot purchase 0 units of RUCs")
            return false
        }

        if (!DataHelper.isValidCurrencyInput(rucPrice.value, "Purchase Price", displayToastMessage))
            return false

        return true
    }

    private fun getRucObjectFromInputs() : Ruc? {
        if (isValidInputs()) {
            val purchaseDate = DataHelper.parseNumericalDateFormat(purchaseDate.value)
            val unitsPurchased = sliderPosition.floatValue.roundToInt()
            val rucPrice = rucPrice.value
                .replace(",", "").toBigDecimal()

            var unitsHeldAfterTransaction = -1
            if (!isHistorical.value)
                unitsHeldAfterTransaction = newUnitsHeld.value.toInt()

            val activeVehicleId = DataManager.returnActiveVehicle()?.id

            if (activeVehicleId != null)
                return Ruc(purchaseDate, unitsPurchased, unitsHeldAfterTransaction, rucPrice, activeVehicleId, isHistorical.value)
        }

        return null
    }

    var onRecordClick = {
        val ruc = getRucObjectFromInputs()

        if (ruc != null) {
            DataManager.returnActiveVehicle()?.logRuc(ruc)
            displayToastMessage("RUCs saved")
            navigateToVehicle()
        }
    }

    val onEditClick = {
        isReadOnly.value = false
    }

    val onSaveClick = {
        val ruc = getRucObjectFromInputs()

        if (ruc != null) {
            ruc.id = rucId
            DataManager.returnActiveVehicle()?.updateRuc(ruc)
            displayToastMessage("Changes saved.")
            isReadOnly.value = true
        }
    }

    var isDisplayDeleteDialog = mutableStateOf(false)

    val onDeleteClick = {
        isDisplayDeleteDialog.value = true
    }

    val onConfirmClick = {
        DataManager.returnActiveVehicle()?.deleteRuc(rucId)
        displayToastMessage("RUCs record deleted.")
        navigateToVehicle()
    }

    val onDismissClick = {
        isDisplayDeleteDialog.value = false
        displayToastMessage("Deletion cancelled.")
    }
}