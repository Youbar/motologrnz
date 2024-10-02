package com.motologr.ui.add.fuel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.motologr.data.EnumConstants

class FuelViewModel : ViewModel() {
    var fuelDate = mutableStateOf("")
        private set

    var fuelPrice = mutableStateOf("")
        private set

    var fuelOdometer = mutableStateOf("")
        private set

    var fuelLitres = mutableStateOf("")
        private set

    var is91Checked = mutableStateOf(false)
        private set

    var is95Checked = mutableStateOf(false)
        private set

    var is98Checked = mutableStateOf(false)
        private set

    var isDieselChecked = mutableStateOf(false)
        private set

    val onBoxChecked: (Int) -> Unit = { fuelType ->
        if (fuelType == EnumConstants.FuelType.Unleaded91.ordinal) {
            is95Checked.value = false
            is98Checked.value = false
            isDieselChecked.value = false
        } else if (fuelType == EnumConstants.FuelType.Unleaded95.ordinal) {
            is91Checked.value = false
            is98Checked.value = false
            isDieselChecked.value = false
        } else if (fuelType == EnumConstants.FuelType.Unleaded98.ordinal) {
            is91Checked.value = false
            is95Checked.value = false
            isDieselChecked.value = false
        } else if (fuelType == EnumConstants.FuelType.Diesel.ordinal) {
            is91Checked.value = false
            is95Checked.value = false
            is98Checked.value = false
        }
    }
}