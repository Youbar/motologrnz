package com.motologr.ui.plus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlusViewModel : ViewModel() {

    private val _textBrand = MutableLiveData<String>().apply {
        value = "Brand/Manufacturer"
    }
    private val _editTextBrand = MutableLiveData<String>().apply {
        value = "Brand name"
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
    private val _textCurrReg = MutableLiveData<String>().apply {
        value = "When does your current registration expire?"
    }
    private val _textCurrOdo = MutableLiveData<String>().apply {
        value = "How many kms has your car done?"
    }
    private val _editTextCurrOdo = MutableLiveData<String>().apply {
        value = "xxxx km"
    }

    val textBrand: LiveData<String> = _textBrand
    val editTextBrand: LiveData<String> = _editTextBrand
    val textModel: LiveData<String> = _textModel
    val editTextModel: LiveData<String> = _editTextModel
    val textYear: LiveData<String> = _textYear
    val editTextYear: LiveData<String> = _editTextYear
    val textLastWOF: LiveData<String> = _textLastWOF
    val textCurrentReg: LiveData<String> = _textCurrReg
    val textCurrOdo: LiveData<String> = _textCurrOdo
    val editTextCurrOdo: LiveData<String> = _editTextCurrOdo
}