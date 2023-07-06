package com.example.motologr.ui.motorcycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MotorcycleViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "KTM Duke 200 details here"
    }
    val text: LiveData<String> = _text
}