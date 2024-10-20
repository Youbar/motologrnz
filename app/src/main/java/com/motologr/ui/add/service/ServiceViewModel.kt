package com.motologr.ui.add.service

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.motologr.data.DataHelper
import com.motologr.data.DataManager
import com.motologr.data.EnumConstants
import com.motologr.data.objects.maint.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Date

class ServiceViewModel : ViewModel() {
    companion object {
        const val RECORD_SERVICE = "Record Service"
        const val UPDATE_SERVICE = "Update Service"
    }

    val serviceCardTitle : String
        get() {
            if (isExistingData)
                return UPDATE_SERVICE
            else
                return RECORD_SERVICE
        }

    var serviceDate = mutableStateOf("")
        private set

    var servicePrice = mutableStateOf("")
        private set

    var isOilChangeChecked = mutableStateOf(false)
        private set

    var isGeneralChecked = mutableStateOf(false)
        private set

    var isFullChecked = mutableStateOf(false)
        private set

    var serviceProvider = mutableStateOf("")
        private set

    var serviceComments = mutableStateOf("")
        private set

    val onBoxChecked: (Int) -> Unit = { serviceType ->
        if (serviceType == EnumConstants.ServiceType.OilChange.ordinal) {
            isGeneralChecked.value = false
            isFullChecked.value = false
        } else if (serviceType == EnumConstants.ServiceType.General.ordinal) {
            isOilChangeChecked.value = false
            isFullChecked.value = false
        } else if (serviceType == EnumConstants.ServiceType.Full.ordinal) {
            isOilChangeChecked.value = false
            isGeneralChecked.value = false
        }
    }

    var displayToastMessage: (String) -> Unit = { message : String -> }

    private fun getServiceObjectFromInputs() : Service? {
        if (validateServiceInputs()) {
            val vehicleId: Int = DataManager.returnActiveVehicle()?.id!!
            val serviceDate: Date = DataHelper.parseNumericalDateFormat(serviceDate.value)
            val servicePrice: BigDecimal = servicePrice.value
                .replace(",","").toBigDecimal().setScale(2, RoundingMode.HALF_UP)
            val serviceType: Int = returnCheckedServiceType()
            val serviceProvider : String = serviceProvider.value
            val serviceComments : String = serviceComments.value

            return Service(serviceType, servicePrice, serviceDate, serviceProvider, serviceComments, vehicleId)
        }

        return null
    }

    val onRecordClick = {
        val service = getServiceObjectFromInputs()

        if (service != null) {
            DataManager.returnActiveVehicle()?.logService(service)
            navigateToVehicle()
        }
    }

    fun initServiceViewModel(serviceProvider : String) {
        this.serviceProvider.value = serviceProvider
        this.serviceDate.value = DataHelper.getCurrentDateString()
    }

    var navigateToVehicle = { }

    var isReadOnly = mutableStateOf(false)
        private set

    private var serviceId = -1

    val isExistingData : Boolean
        get() {
            return serviceId != -1
        }

    fun setViewModelToReadOnly(service : Service) {
        serviceId = service.id
        isReadOnly.value = true
        serviceDate.value = DataHelper.formatNumericalDateFormat(service.serviceDate)
        servicePrice.value = service.price.toString()
        setCheckedServiceType(service.serviceType)
        serviceProvider.value = service.serviceProvider
        serviceComments.value = service.comment
    }

    private fun setCheckedServiceType(serviceType : Int) {
        if (serviceType == EnumConstants.ServiceType.OilChange.ordinal)
            isOilChangeChecked.value = true
        else if (serviceType == EnumConstants.ServiceType.General.ordinal)
            isGeneralChecked.value = true
        else if (serviceType == EnumConstants.ServiceType.Full.ordinal)
            isFullChecked.value = true
    }

    private fun returnCheckedServiceType() : Int {
        if (isOilChangeChecked.value)
            return EnumConstants.ServiceType.OilChange.ordinal

        if (isGeneralChecked.value)
            return EnumConstants.ServiceType.General.ordinal

        if (isFullChecked.value)
            return EnumConstants.ServiceType.Full.ordinal

        return -1
    }

    private val serviceTypeInputs : Array<Boolean>
        get() : Array<Boolean> = arrayOf(isOilChangeChecked.value, isGeneralChecked.value, isFullChecked.value)

    private fun validateServiceInputs() : Boolean {
        if (!DataHelper.isValidStringInput(serviceDate.value, "Service Date", displayToastMessage))
            return false

        if (!DataHelper.isValidCurrencyInput(servicePrice.value, "Service Price", displayToastMessage))
            return false

        if (serviceTypeInputs.none { f -> f }) {
            displayToastMessage("Please select a service type")
            return false
        }

        if (!DataHelper.isValidStringInput(serviceProvider.value, "Service Provider", displayToastMessage))
            return false

        // Optional string input does not need validation

        return true
    }

    val onEditClick = {
        isReadOnly.value = false
    }

    val onSaveClick = {
        val service = getServiceObjectFromInputs()

        if (service != null) {
            service.id = serviceId
            DataManager.returnActiveVehicle()?.updateService(service)
            displayToastMessage("Changes saved.")
            isReadOnly.value = true
        }
    }

    var isDisplayDeleteDialog = mutableStateOf(false)

    val onDeleteClick = {
        isDisplayDeleteDialog.value = true
    }

    val onConfirmClick = {
        DataManager.returnActiveVehicle()?.deleteService(serviceId)
        displayToastMessage("Service record deleted.")
        navigateToVehicle()
    }

    val onDismissClick = {
        isDisplayDeleteDialog.value = false
        displayToastMessage("Deletion cancelled.")
    }
}