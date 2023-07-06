package com.example.motologr.ui.vehicle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VehicleViewModel : ViewModel() {

    private val _textVehicle = MutableLiveData<String>().apply {
        value = "Mazda 323 | 1989"
    }
    private val _textWOFDone = MutableLiveData<String>().apply {
        value = "Last WOF: 6/9/23"
    }
    private val _textWOFDue = MutableLiveData<String>().apply {
        value = "Next WOF: 6/9/24"
    }
    private val _textRegDue = MutableLiveData<String>().apply {
        value = "Next Reg: 6/9/24"
    }
    private val _textOdometer = MutableLiveData<String>().apply {
        value = "Last Odometer Reading: 1234 km"
    }
    private val _textInsurer = MutableLiveData<String>().apply {
        value = "You are with AA insurance"
    }
    private val _textInsurerDate = MutableLiveData<String>().apply {
        value = "and your next bill of $123 is due 6/9/23"
    }
    private val _textApproxCostsTitle = MutableLiveData<String>().apply {
        value = "Approx. Annual Costs:"
    }
    private val _textApproxCosts = MutableLiveData<String>().apply {
        value = "$1234.56"
    }

    val textCar: LiveData<String> = _textVehicle
    val textWOFDone: LiveData<String> = _textWOFDone
    val textWOFDue: LiveData<String> = _textWOFDue
    val textRegDue: LiveData<String> = _textRegDue
    val textOdometer: LiveData<String> = _textOdometer
    val textInsurer: LiveData<String> = _textInsurer
    val textInsurerDate: LiveData<String> = _textInsurerDate
    val textApproxCostsTitle: LiveData<String> = _textApproxCostsTitle
    val textApproxCosts: LiveData<String> = _textApproxCosts
}