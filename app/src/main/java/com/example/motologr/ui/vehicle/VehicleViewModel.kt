package com.example.motologr.ui.vehicle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VehicleViewModel : ViewModel() {

    private val _textVehicle = MutableLiveData<String>().apply {
        value = ""
    }
    private val _textWOFDone = MutableLiveData<String>().apply {
        value = "Last WOF: TODO"
    }
    private val _textWOFDue = MutableLiveData<String>().apply {
        value = ""
    }
    private val _textRegDue = MutableLiveData<String>().apply {
        value = ""
    }
    private val _textOdometer = MutableLiveData<String>().apply {
        value = "Last Odometer Reading: TODO"
    }
    private val _textInsurer = MutableLiveData<String>().apply {
        value = "You are with TODO insurance"
    }
    private val _textInsurerDate = MutableLiveData<String>().apply {
        value = "and your next bill of \$TODO is due TODO"
    }
    private val _textApproxCostsTitle = MutableLiveData<String>().apply {
        value = "Approx. Annual Costs:"
    }
    private val _textApproxCosts = MutableLiveData<String>().apply {
        value = "\$TODO"
    }

    val textVehicle: LiveData<String> = _textVehicle
    val textWOFDone: LiveData<String> = _textWOFDone
    val textWOFDue: LiveData<String> = _textWOFDue
    val textRegDue: LiveData<String> = _textRegDue
    val textOdometer: LiveData<String> = _textOdometer
    val textInsurer: LiveData<String> = _textInsurer
    val textInsurerDate: LiveData<String> = _textInsurerDate
    val textApproxCostsTitle: LiveData<String> = _textApproxCostsTitle
    val textApproxCosts: LiveData<String> = _textApproxCosts
}