package com.motologr.ui.add.historical.reg

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.motologr.data.DataHelper
import com.motologr.data.DataManager
import com.motologr.data.objects.reg.Reg
import java.math.RoundingMode
import kotlin.math.roundToInt

class HistoricalRegViewModel : ViewModel() {
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
            val registrationPrice = registrationPrice.value
                .replace(",","").toBigDecimal()
            val registrationMonths = sliderPosition.floatValue.roundToInt()
            val purchaseDate = DataHelper.parseNumericalDateFormat(selectedDate.value)

            val minDt = DataHelper.getMinDt()
            val activeVehicle = DataManager.returnActiveVehicle()

            if (activeVehicle != null) {
                val reg : Reg = Reg(minDt, minDt, registrationMonths, registrationPrice, activeVehicle.id, purchaseDate, true)
                activeVehicle.logReg(reg)
                displayToastMessage("Registration saved")
                navigateToVehicle()
            }
        }
    }
}