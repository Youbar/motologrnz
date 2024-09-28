package com.motologr.ui.vehicle.settings

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.motologr.data.objects.vehicle.Vehicle

class VehicleSettingsViewModel : ViewModel() {
    var makeInput = mutableStateOf("")
        private set

    var modelInput = mutableStateOf("")
        private set

    var modelYearInput = mutableStateOf("")
        private set

    private fun validateGeneralInputs() {

    }

    var onUpdateClick = {
        activeVehicle.updateVehicleName(makeInput.value, modelInput.value, modelYearInput.value.toInt())
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

    private fun validateComplianceInputs() {

    }

    var onSaveClick = {
        activeVehicle.updateRucs(isUseRucsBoolean.value, isUseRucsInput.value.toInt())
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
        isUseRucsInput.value = activeVehicle.roadUserChargesHeld.toString()
    }

    var displayToastMessage: (String) -> Unit = { message : String -> }
}