package com.example.motologr.ui.plus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlusViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "New Car Details"
    }
    val text: LiveData<String> = _text
}