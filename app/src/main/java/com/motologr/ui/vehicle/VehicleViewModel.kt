package com.motologr.ui.vehicle

import android.content.SharedPreferences
import android.provider.ContactsContract.Data
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.motologr.R
import com.motologr.data.DataManager
import com.motologr.data.objects.vehicle.Vehicle
import java.math.RoundingMode
import java.text.DecimalFormat

class VehicleViewModel : ViewModel() {

    private val _textVehicle = MutableLiveData<String>().apply {
        value = ""
    }

    val textVehicle: LiveData<String> = _textVehicle

    private val _textWOFDue = MutableLiveData<String>().apply {
        value = ""
    }

    val textWOFDue: LiveData<String> = _textWOFDue

    private val _textRegDue = MutableLiveData<String>().apply {
        value = ""
    }

    val textRegDue: LiveData<String> = _textRegDue

    private val _isOdometerDisplayed = MutableLiveData<Boolean>().apply {
        value = false
    }

    val isOdometerDisplayed : LiveData<Boolean> = _isOdometerDisplayed


    private val _textOdometer = MutableLiveData<String>().apply {
        value = ""
    }

    val textOdometer: LiveData<String> = _textOdometer

    private val _isRoadUserChargesDisplayed = MutableLiveData<Boolean>().apply {
        value = false
    }

    val isRoadUserChargesDisplayed : LiveData<Boolean> = _isRoadUserChargesDisplayed


    private val _textRoadUserCharges = MutableLiveData<String>().apply {
        value = ""
    }

    val textRoadUserCharges: LiveData<String> = _textRoadUserCharges


    private val _textInsurer = MutableLiveData<String>().apply {
        value = ""
    }

    val textInsurer: LiveData<String> = _textInsurer

    private val _textInsurerCoverage = MutableLiveData<String>().apply {
        value = ""
    }

    val textInsurerCoverage: LiveData<String> = _textInsurerCoverage

    private val _textInsurerCost = MutableLiveData<String>().apply {
        value = ""
    }

    val textInsurerCost: LiveData<String> = _textInsurerCost

    private val _textInsurerCycle = MutableLiveData<String>().apply {
        value = ""
    }

    val textInsurerCycle: LiveData<String> = _textInsurerCycle

    private val _textNextCharge = MutableLiveData<String>().apply {
        value = ""
    }

    val textNextCharge: LiveData<String> = _textNextCharge


    private val _textInsurerDaysToNextCharge = MutableLiveData<String>().apply {
        value = ""
    }

    val textInsurerDaysToNextCharge: LiveData<String> = _textInsurerDaysToNextCharge

    private val _hasCurrentInsurance = MutableLiveData<Boolean>().apply {
        value = false
    }

    val hasCurrentInsurance : LiveData<Boolean> = _hasCurrentInsurance

    private val _textCurrentCosts = MutableLiveData<String>().apply {
        value = ""
    }

    val textCurrentCosts: LiveData<String> = _textCurrentCosts

    private val _textProjectedCosts = MutableLiveData<String>().apply {
        value = ""
    }

    val textProjectedCosts: LiveData<String> = _textProjectedCosts

    private fun updateFieldsForVehicle(vehicle: Vehicle) {
        _textVehicle.value = vehicle.brandName + " " + vehicle.modelName + " | " + vehicle.year.toString()
        _textWOFDue.value = vehicle.returnWofExpiry()
        _textRegDue.value = vehicle.returnRegExpiry()
        _isRoadUserChargesDisplayed.value = vehicle.isUseRoadUserCharges

        if (_isRoadUserChargesDisplayed.value == true)
            _textRoadUserCharges.value = vehicle.returnLatestRucUnits() + " km"

        if (vehicle.hasCurrentInsurance()) {
            _hasCurrentInsurance.value = true

            val insurancePolicy = vehicle.returnLatestInsurancePolicy()
            _textInsurer.value = insurancePolicy.insurer
            _textInsurerCoverage.value = insurancePolicy.returnCoverageTypeShorthand()
            _textInsurerCost.value = insurancePolicy.returnFormattedBilling()
            _textInsurerCycle.value = insurancePolicy.returnCycleTypeShorthand()

            if (insurancePolicy.billingCycle == 2)
                _textNextCharge.value = "Expires"
            else
                _textNextCharge.value = "Nxt Chrg"
            _textInsurerDaysToNextCharge.value = insurancePolicy.returnDaysToNextCharge()
        } else {
            _hasCurrentInsurance.value = false

            _textInsurer.value = "No current policy"
        }

        val df = DecimalFormat("0.00")
        df.roundingMode = RoundingMode.HALF_EVEN

        val currentExpenses = df.format(vehicle.returnCurrentExpensesWithinFinancialYear())
        val projectedExpenses = df.format(vehicle.returnExpensesWithinFinancialYear())

        _textCurrentCosts.value = "$$currentExpenses current"
        _textProjectedCosts.value = "$$projectedExpenses projected"
    }

    private var activeVehicle : Vehicle? = null

    init {
        activeVehicle = DataManager.returnActiveVehicle()

        if (activeVehicle != null) {
            updateFieldsForVehicle(activeVehicle!!)
        }
    }

    fun updateOdometerView(isTrackingFuelConsumption : Boolean) {
        if (activeVehicle == null)
            return

        _isOdometerDisplayed.value = isTrackingFuelConsumption
        if (activeVehicle!!.isMeetingCompliance() && activeVehicle!!.getLatestOdometerReading() >= 0)
            _textOdometer.value = activeVehicle!!.getLatestOdometerReading().toString() + " km"
        else
            _textOdometer.value = "N/A"

    }
}