package com.motologr.ui.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.motologr.data.DataManager

class AddViewModel : ViewModel() {
    var isDefaultListVisible by mutableStateOf(false)
        private set

    var isComplianceListVisible by mutableStateOf(false)
        private set

    var isMechanicalListVisible by mutableStateOf(false)
        private set

    val onComplianceCardClicked : () -> Unit = {
        isDefaultListVisible = false
        isComplianceListVisible = true
        isMechanicalListVisible = false
    }

    val onMechanicalCardClicked : () -> Unit = {
        isDefaultListVisible = false
        isComplianceListVisible = false
        isMechanicalListVisible = true
    }

    val onBackCardClicked : () -> Unit = {
        isDefaultListVisible = true
        isComplianceListVisible = false
        isMechanicalListVisible = false
    }

    var isRucsVisible by mutableStateOf(false)
        private set

    init {
        isDefaultListVisible = true
        isRucsVisible = DataManager.isActiveVehicleUsingRucs()
    }
}