package com.motologr.ui.vehicle.settings

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.motologr.data.DataHelper
import com.motologr.data.objects.vehicle.Vehicle

class VehicleSettingsViewModel : ViewModel() {
    lateinit var applicationContext : Context

    var makeInput = mutableStateOf("")
        private set

    var modelInput = mutableStateOf("")
        private set

    var modelYearInput = mutableStateOf("")
        private set

    private fun validateGeneralInputs() : Boolean {
        return DataHelper.isValidIntegerInput(modelYearInput.value, "Year", applicationContext)
    }

    var onUpdateClick = {
        if (validateGeneralInputs()) {
            activeVehicle.updateVehicleName(makeInput.value, modelInput.value, modelYearInput.value.toInt())
            displayToastMessage("General settings updated")
        }
    }

    var isUseRucsBoolean = mutableStateOf(false)
        private set

    var isUseRucsText = mutableStateOf("")
        private set

    var isUseRucsInput = mutableStateOf("")
        private set

    var onRucSwitch = {
        isUseRucsText.value = getRucsInput(isUseRucsBoolean.value)
    }

    var isUseCofBoolean = mutableStateOf(false)
        private set

    var isUseCofText = mutableStateOf("This vehicle does not require COFs")

    var onCofSwitch = {

    }

    private fun validateComplianceInputs() : Boolean {
        if (isUseRucsBoolean.value)
            return DataHelper.isValidIntegerInput(isUseRucsInput.value, "Current RUCs", applicationContext)
        else
            return true
    }

    var onSaveClick = {
        if (validateComplianceInputs()) {
            var rucsHeld : Int = -1
            if (isUseRucsBoolean.value)
                rucsHeld = isUseRucsInput.value.toInt()

            activeVehicle.updateRucs(isUseRucsBoolean.value, rucsHeld)
            displayToastMessage("Compliance settings updated")
        }
    }

    lateinit var activeVehicle : Vehicle

    private fun getRucsInput(isUseRoadUserCharges: Boolean): String {
        if (isUseRoadUserCharges) {
            return "This vehicle requires RUCs"
        } else {
            return "This vehicle does not require RUCs"
        }
    }

    fun setDefaultValues(activeVehicle: Vehicle) {
        this.activeVehicle = activeVehicle

        makeInput.value = activeVehicle.brandName
        modelInput.value = activeVehicle.modelName
        modelYearInput.value = activeVehicle.modelYear.toString()

        isUseRucsBoolean.value = activeVehicle.isUseRoadUserCharges
        isUseRucsText.value = getRucsInput(activeVehicle.isUseRoadUserCharges)

        val roadUserChargesHeld = activeVehicle.roadUserChargesHeld
        if (roadUserChargesHeld == -1)
            isUseRucsInput.value = ""
        else
            isUseRucsInput.value = roadUserChargesHeld.toString()
    }

    var displayToastMessage: (String) -> Unit = { message : String -> }
}