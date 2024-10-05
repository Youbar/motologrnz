package com.motologr.ui.add.fuel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.motologr.data.DataHelper
import com.motologr.data.DataManager
import com.motologr.data.EnumConstants
import com.motologr.data.objects.fuel.Fuel
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Date

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

    var isTrackingFuelConsumption = mutableStateOf(false)
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

    var displayToastMessage: (String) -> Unit = { message : String -> }

    private fun setCheckedFuelType(fuelType : Int) {
        if (fuelType == EnumConstants.FuelType.Unleaded91.ordinal)
            is91Checked.value = true
        else if (fuelType == EnumConstants.FuelType.Unleaded95.ordinal)
            is95Checked.value = true
        else if (fuelType == EnumConstants.FuelType.Unleaded98.ordinal)
            is98Checked.value = true
        else if (fuelType == EnumConstants.FuelType.Diesel.ordinal)
            isDieselChecked.value = true
    }

    private fun returnCheckedFuelType() : Int {
        if (is91Checked.value)
            return EnumConstants.FuelType.Unleaded91.ordinal

        if (is95Checked.value)
            return EnumConstants.FuelType.Unleaded95.ordinal

        if (is98Checked.value)
            return EnumConstants.FuelType.Unleaded98.ordinal

        if (isDieselChecked.value)
            return EnumConstants.FuelType.Diesel.ordinal

        return -1
    }

    private val fuelTypeInputs : Array<Boolean>
        get() : Array<Boolean> = arrayOf(is91Checked.value, is95Checked.value, is98Checked.value, isDieselChecked.value)

    private fun validateFuelInputs() : Boolean {
        if (!DataHelper.isValidStringInput(fuelDate.value, "Purchase Date", displayToastMessage))
            return false

        if (!DataHelper.isValidCurrencyInput(fuelPrice.value, "Purchase Price", displayToastMessage))
            return false

        if (fuelTypeInputs.none { f -> f }) {
            displayToastMessage("Please select a fuel type")
            return false
        }

        if (isTrackingFuelConsumption.value) {
            if (!DataHelper.isValidIntegerInput(fuelOdometer.value, "Odometer", displayToastMessage))
                return false

            if (!DataHelper.isValidCurrencyInput(fuelLitres.value, "Litres", displayToastMessage))
                return false
        }

        return true
    }

    var navigateToVehicle = { }

    var onClick = {
        if (validateFuelInputs()) {
            val vehicleId: Int = DataManager.returnActiveVehicle()?.id!!
            val fuelType: Int = returnCheckedFuelType()
            val price: BigDecimal = fuelPrice.value
                .replace(",","").toBigDecimal().setScale(2, RoundingMode.HALF_UP)
            val purchaseDate: Date = DataHelper.parseNumericalDateFormat(fuelDate.value)

            val fuel: Fuel
            if (isTrackingFuelConsumption.value) {
                val litres: BigDecimal = fuelLitres.value
                    .replace(",","").toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                val odometer: Int = fuelOdometer.value.toInt()

                fuel = Fuel(fuelType, price, litres, purchaseDate, odometer, vehicleId)
            } else {
                fuel = Fuel(fuelType, price, (-1.0).toBigDecimal(), purchaseDate, -1, vehicleId)
            }

            DataManager.returnActiveVehicle()?.logFuel(fuel)
            navigateToVehicle()
        }
    }
        private set

    fun initFuelViewModel(isTrackingFuelConsumption : Boolean) {
        this.isTrackingFuelConsumption.value = isTrackingFuelConsumption
        this.fuelDate.value = DataHelper.getCurrentDateString()
    }

    var isReadOnly = mutableStateOf(false)
        private set

    fun setViewModelToReadOnly(fuel : Fuel) {
        isReadOnly.value = true
        fuelDate.value = DataHelper.formatNumericalDateFormat(fuel.purchaseDate)
        fuelPrice.value = fuel.price.toString()
        setCheckedFuelType(fuel.fuelType)
        fuelLitres.value = fuel.litres.toString()
        fuelOdometer.value = fuel.odometerReading.toString()
    }
}