package com.motologr.ui.add.historical.wof

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.motologr.data.DataHelper
import com.motologr.data.DataManager
import com.motologr.data.objects.maint.Wof
import com.motologr.data.objects.vehicle.Vehicle

class HistoricalWofViewModel : ViewModel() {
    companion object {
        private const val HISTORICAL_WOF = "Historical WOF"
        private const val UPDATE_WOF = "Update WOF"
        const val HISTORICAL_WOF_BUTTON = "Record"
        const val UPDATE_WOF_BUTTON = "Update"
    }

    val wofTitle
        get() : String {
            if (isHistorical.value)
                return HISTORICAL_WOF
            else
                return UPDATE_WOF
        }

    var wofPrice = mutableStateOf("")
        private set

    var wofProvider = mutableStateOf("")
        private set

    var historicalWofDate = mutableStateOf("")
        private set

    var oldWofExpiryDate = mutableStateOf("")
        private set

    var newWofExpiryDate = mutableStateOf("")
        private set

    var isHistorical = mutableStateOf(false)
        private set

    fun initViewModel(activeVehicle : Vehicle, isHistorical : Boolean) {
        this.isHistorical.value = isHistorical

        val vehicleWofExpiryDate = activeVehicle.returnWofExpiryDate()
        oldWofExpiryDate.value = DataHelper.formatNumericalDateFormat(vehicleWofExpiryDate)
        val newVehicleWofExpiryDate = Wof.applyWofYearRule(vehicleWofExpiryDate, activeVehicle.modelYear)
        newWofExpiryDate.value = DataHelper.formatNumericalDateFormat(newVehicleWofExpiryDate)
    }

    var isReadOnly = mutableStateOf(false)
        private set

    private var wofId = -1

    val isExistingData : Boolean
        get() {
            return wofId != -1
        }

    fun setViewModelToReadOnly(wof: Wof) {
        wofId = wof.id
        isReadOnly.value = true
        isHistorical.value = wof.isHistorical

        if (isHistorical.value) {
            historicalWofDate.value = DataHelper.formatNumericalDateFormat(wof.wofCompletedDate)
        } else {
            oldWofExpiryDate.value = DataHelper.formatNumericalDateFormat(wof.wofCompletedDate)
            newWofExpiryDate.value = DataHelper.formatNumericalDateFormat(wof.wofDate)
        }

        wofPrice.value = wof.price.toString()
        wofProvider.value = wof.wofProvider
    }

    var displayToastMessage = { message : String -> }

    var navigateToVehicle = {

    }

    private fun isValidInputs() : Boolean {
        if (isHistorical.value) {
            if (!DataHelper.isValidStringInput(historicalWofDate.value, "WOF Date", displayToastMessage))
                return false
        } else {
            if (!DataHelper.isValidStringInput(oldWofExpiryDate.value, "WOF Date", displayToastMessage))
                return false
            if (!DataHelper.isValidStringInput(newWofExpiryDate.value, "WOF Date", displayToastMessage))
                return false
        }

        if (!DataHelper.isValidCurrencyInput(wofPrice.value, "WOF Price", displayToastMessage))
            return false

        if (!DataHelper.isValidStringInput(wofProvider.value, "WOF Provider", displayToastMessage))
            return false

        return true
    }

    private fun getWofObjectFromInputs() : Wof? {
        if (isValidInputs()) {
            val wofPrice = wofPrice.value
                .replace(",","").toBigDecimal()
            val wofProvider = wofProvider.value
            val activeVehicle = DataManager.returnActiveVehicle()!!

            if (isHistorical.value) {
                val minDt = DataHelper.getMinDt()
                val purchaseDate = DataHelper.parseNumericalDateFormat(historicalWofDate.value)
                return Wof(minDt, purchaseDate, wofPrice, activeVehicle.id, wofProvider, purchaseDate, isHistorical.value)
            } else {
                val newWofDate = DataHelper.parseNumericalDateFormat(newWofExpiryDate.value)
                val oldWofDate = DataHelper.parseNumericalDateFormat(oldWofExpiryDate.value)
                return Wof(newWofDate, oldWofDate, wofPrice, activeVehicle.id, wofProvider, oldWofDate, isHistorical.value)
            }
        }

        return null
    }

    var onRecordClick = {
        if (isValidInputs()) {
            val wofPrice = wofPrice.value
                .replace(",","").toBigDecimal()
            val wofProvider = wofProvider.value
            val activeVehicle = DataManager.returnActiveVehicle()!!

            val wof : Wof?
            if (isHistorical.value) {
                val minDt = DataHelper.getMinDt()
                val purchaseDate = DataHelper.parseNumericalDateFormat(historicalWofDate.value)
                wof = Wof(minDt, purchaseDate, wofPrice, activeVehicle.id, wofProvider, purchaseDate, isHistorical.value)
            } else {
                val newWofDate = DataHelper.parseNumericalDateFormat(newWofExpiryDate.value)
                val oldWofDate = DataHelper.parseNumericalDateFormat(oldWofExpiryDate.value)
                wof = Wof(newWofDate, oldWofDate, wofPrice, activeVehicle.id, wofProvider, oldWofDate, isHistorical.value)
            }

            activeVehicle.logWof(wof)
            displayToastMessage("WOF updated")
            navigateToVehicle()
        }
    }

    val onEditClick = {
        isReadOnly.value = false
    }

    val onSaveClick = {
        val wof = getWofObjectFromInputs()

        if (wof != null) {
            wof.id = wofId
            DataManager.returnActiveVehicle()?.updateWof(wof)
            displayToastMessage("Changes saved.")
            isReadOnly.value = true
        }
    }

    var isDisplayDeleteDialog = mutableStateOf(false)

    val onDeleteClick = {
        isDisplayDeleteDialog.value = true
    }

    val onConfirmClick = {
        DataManager.returnActiveVehicle()?.deleteWof(wofId)
        displayToastMessage("WOF record deleted.")
        navigateToVehicle()
    }

    val onDismissClick = {
        isDisplayDeleteDialog.value = false
        displayToastMessage("Deletion cancelled.")
    }
}