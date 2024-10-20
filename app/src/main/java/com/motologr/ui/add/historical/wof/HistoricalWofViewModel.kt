package com.motologr.ui.add.historical.wof

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.motologr.data.DataHelper
import com.motologr.data.DataManager
import com.motologr.data.objects.maint.Wof
import com.motologr.data.objects.reg.Reg
import kotlin.math.roundToInt

class HistoricalWofViewModel : ViewModel() {
    var wofPrice = mutableStateOf("")
        private set

    var wofProvider = mutableStateOf("")
        private set

    var wofDate = mutableStateOf("")
        private set

    var displayToastMessage = { message : String -> }

    var navigateToVehicle = {

    }

    private fun isValidInputs() : Boolean {
        if (wofDate.value.isEmpty()) {
            displayToastMessage("Please input a valid purchase date")
            return false
        }

        if (wofPrice.value.isEmpty()) {
            displayToastMessage("Please input a WOF price")
            return false
        }

        try {
            wofPrice.value
                .replace(",","").toBigDecimal()
        } catch (exception : Exception) {
            displayToastMessage("Please input a valid WOF price")
            return false
        }

        if (wofProvider.value.isEmpty()) {
            displayToastMessage("Please input a WOF provider")
            return false
        }

        return true
    }

    var onClick = {
        if (isValidInputs()) {
            val wofPrice = wofPrice.value
                .replace(",","").toBigDecimal()
            val wofProvider = wofProvider.value
            val purchaseDate = DataHelper.parseNumericalDateFormat(wofDate.value)

            val minDt = DataHelper.getMinDt()
            val activeVehicle = DataManager.returnActiveVehicle()

            if (activeVehicle != null) {
                val wof = Wof(minDt, purchaseDate, wofPrice, activeVehicle.id, wofProvider, purchaseDate, true)
                activeVehicle.logWof(wof)
                displayToastMessage("WOF saved")
                navigateToVehicle()
            }
        }
    }
}