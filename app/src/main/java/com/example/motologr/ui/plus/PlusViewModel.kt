package com.example.motologr.ui.plus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlusViewModel : ViewModel() {

    private val _textNewVehicle = MutableLiveData<String>().apply {
        value = "New Car Details"
    }
    private val _textModel = MutableLiveData<String>().apply {
        value = "Model"
    }
    private val _editTextModel = MutableLiveData<String>().apply {
        value = "Model name"
    }
    private val _textYear = MutableLiveData<String>().apply {
        value = "Year"
    }
    private val _editTextYear = MutableLiveData<String>().apply {
        value = "Model year"
    }
    private val _textLastWOF = MutableLiveData<String>().apply {
        value = "When is your next WOF due?"
    }
    private val _editTextLastWOF = MutableLiveData<String>().apply {
        value = "**/**/**"
    }
    private val _textCurrReg = MutableLiveData<String>().apply {
        value = "When does your current registration expire?"
    }
    private val _editTextCurrReg = MutableLiveData<String>().apply {
        value = "**/**/**"
    }
    private val _textCurrOdo = MutableLiveData<String>().apply {
        value = "How many kms has your car done?"
    }
    private val _editTextCurrOdo = MutableLiveData<String>().apply {
        value = "xxxx km"
    }

    val textNewVehicle: LiveData<String> = _textNewVehicle
    val textModel: LiveData<String> = _textModel
    val editTextModel: LiveData<String> = _editTextModel
    val textYear: LiveData<String> = _textYear
    val editTextYear: LiveData<String> = _editTextYear
    val textLastWOF: LiveData<String> = _textLastWOF
    val editTextLastWOF: LiveData<String> = _editTextLastWOF
    val textCurrentReg: LiveData<String> = _textCurrReg
    val editTextCurrentReg: LiveData<String> = _editTextCurrReg
    val textCurrOdo: LiveData<String> = _textCurrOdo
    val editTextCurrOdo: LiveData<String> = _editTextCurrOdo
}