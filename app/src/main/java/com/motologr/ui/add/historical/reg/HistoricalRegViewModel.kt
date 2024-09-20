package com.motologr.ui.add.historical.reg

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class HistoricalRegViewModel : ViewModel() {
    var registrationPrice = mutableStateOf("")
        private set

    var sliderPosition = mutableFloatStateOf(0f)
        private set

    var selectedDate = mutableStateOf("")
        private set

    var onClick = {
        val registrationPrice = registrationPrice.value
        val registrationMonths = sliderPosition.value
        val purchaseDate = selectedDate.value

    }
}