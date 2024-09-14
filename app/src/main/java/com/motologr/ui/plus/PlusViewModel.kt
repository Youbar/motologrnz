package com.motologr.ui.plus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlusViewModel : ViewModel() {

    private val _textBrand = MutableLiveData<String>().apply {
        value = "Brand/Manufacturer"
    }

    val textBrand: LiveData<String> = _textBrand

    private val _editTextBrand = MutableLiveData<String>().apply {
        value = "Brand name"
    }

    val editTextBrand: LiveData<String> = _editTextBrand

    private val _textModel = MutableLiveData<String>().apply {
        value = "Model"
    }

    val textModel: LiveData<String> = _textModel

    private val _editTextModel = MutableLiveData<String>().apply {
        value = "Model name"
    }

    val editTextModel: LiveData<String> = _editTextModel

    private val _textYear = MutableLiveData<String>().apply {
        value = "Year"
    }

    val textYear: LiveData<String> = _textYear

    private val _editTextYear = MutableLiveData<String>().apply {
        value = "Model year"
    }

    val editTextYear: LiveData<String> = _editTextYear
}