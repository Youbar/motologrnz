package com.motologr.ui.add.historical.ruc

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.motologr.data.DataHelper
import com.motologr.data.DataManager
import com.motologr.data.objects.ruc.Ruc
import kotlin.math.roundToInt

class HistoricalRucViewModel : ViewModel() {
    var registrationPrice = mutableStateOf("")
        private set

    var sliderPosition = mutableFloatStateOf(0f)
        private set

    var selectedDate = mutableStateOf("")
        private set

    var displayToastMessage = { message : String -> }

    var navigateToVehicle = {

    }

    private fun isValidInputs() : Boolean {
        if (registrationPrice.value.isEmpty()) {
            displayToastMessage("Please input a registration price")
            return false
        }

        try {
            registrationPrice.value
                .replace(",","").toBigDecimal()
        } catch (exception : Exception) {
            displayToastMessage("Please input a valid registration price")
            return false
        }

        if (sliderPosition.floatValue.roundToInt() == 0) {
            displayToastMessage("You cannot record 0 months of registration")
            return false
        }

        if (selectedDate.value.isEmpty()) {
            displayToastMessage("Please input a valid purchase date")
            return false
        }

        return true
    }

    var onClick = {
        if (isValidInputs()) {
            val rucPrice = registrationPrice.value
                .replace(",","").toBigDecimal()
            val unitsPurchased = sliderPosition.floatValue.roundToInt()
            val purchaseDate = DataHelper.parseNumericalDateFormat(selectedDate.value)

            val activeVehicle = DataManager.returnActiveVehicle()
            if (activeVehicle != null) {
                val ruc = Ruc(purchaseDate, unitsPurchased, -1, rucPrice, activeVehicle.id, true)
                activeVehicle.logRuc(ruc)
                displayToastMessage("RUCs saved")
                navigateToVehicle()
            }
        }
    }
}