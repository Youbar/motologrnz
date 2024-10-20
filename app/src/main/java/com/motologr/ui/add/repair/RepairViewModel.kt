package com.motologr.ui.add.repair

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.motologr.data.DataHelper
import com.motologr.data.DataManager
import com.motologr.data.EnumConstants
import com.motologr.data.objects.maint.Repair
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Date

class RepairViewModel : ViewModel() {
    companion object {
        const val RECORD_REPAIR = "Record Repair"
        const val UPDATE_REPAIR = "Update Repair"
    }

    val repairCardTitle : String
        get() {
            if (isExistingData)
                return UPDATE_REPAIR
            else
                return RECORD_REPAIR
        }

    var repairDate = mutableStateOf("")
        private set

    var repairPrice = mutableStateOf("")
        private set

    var isMinorChecked = mutableStateOf(false)
        private set

    var isMajorChecked = mutableStateOf(false)
        private set

    var isCriticalChecked = mutableStateOf(false)
        private set

    var repairProvider = mutableStateOf("")
        private set

    var repairComments = mutableStateOf("")
        private set

    val onBoxChecked: (Int) -> Unit = { repairType ->
        if (repairType == EnumConstants.RepairType.Minor.ordinal) {
            isMajorChecked.value = false
            isCriticalChecked.value = false
        } else if (repairType == EnumConstants.RepairType.Major.ordinal) {
            isMinorChecked.value = false
            isCriticalChecked.value = false
        } else if (repairType == EnumConstants.RepairType.Critical.ordinal) {
            isMinorChecked.value = false
            isMajorChecked.value = false
        }
    }

    var displayToastMessage: (String) -> Unit = { message : String -> }

    private fun getRepairObjectFromInputs() : Repair? {
        if (validateRepairInputs()) {
            val vehicleId: Int = DataManager.returnActiveVehicle()?.id!!
            val repairDate: Date = DataHelper.parseNumericalDateFormat(repairDate.value)
            val repairPrice: BigDecimal = repairPrice.value
                .replace(",","").toBigDecimal().setScale(2, RoundingMode.HALF_UP)
            val repairType: Int = returnCheckedRepairType()
            val repairProvider : String = repairProvider.value
            val repairComments : String = repairComments.value

            return Repair(repairType, repairPrice, repairDate, repairProvider, repairComments, vehicleId)
        }

        return null
    }

    val onRecordClick = {
        val repair = getRepairObjectFromInputs()

        if (repair != null) {
            DataManager.returnActiveVehicle()?.logRepair(repair)
            navigateToVehicle()
        }
    }

    fun initRepairViewModel(repairProvider : String) {
        this.repairProvider.value = repairProvider
        this.repairDate.value = DataHelper.getCurrentDateString()
    }

    var navigateToVehicle = { }

    var isReadOnly = mutableStateOf(false)
        private set

    private var repairId = -1

    val isExistingData : Boolean
        get() {
            return repairId != -1
        }

    fun setViewModelToReadOnly(repair : Repair) {
        repairId = repair.id
        isReadOnly.value = true
        repairDate.value = DataHelper.formatNumericalDateFormat(repair.repairDate)
        repairPrice.value = repair.price.toString()
        setCheckedRepairType(repair.repairType)
        repairProvider.value = repair.repairProvider
        repairComments.value = repair.comment
    }

    private fun setCheckedRepairType(repairType : Int) {
        if (repairType == EnumConstants.RepairType.Minor.ordinal)
            isMinorChecked.value = true
        else if (repairType == EnumConstants.RepairType.Major.ordinal)
            isMajorChecked.value = true
        else if (repairType == EnumConstants.RepairType.Critical.ordinal)
            isCriticalChecked.value = true
    }

    private fun returnCheckedRepairType() : Int {
        if (isMinorChecked.value)
            return EnumConstants.RepairType.Minor.ordinal

        if (isMajorChecked.value)
            return EnumConstants.RepairType.Major.ordinal

        if (isCriticalChecked.value)
            return EnumConstants.RepairType.Critical.ordinal

        return -1
    }

    private val repairTypeInputs : Array<Boolean>
        get() : Array<Boolean> = arrayOf(isMinorChecked.value, isMajorChecked.value, isCriticalChecked.value)

    private fun validateRepairInputs() : Boolean {
        if (!DataHelper.isValidStringInput(repairDate.value, "Repair Date", displayToastMessage))
            return false

        if (!DataHelper.isValidCurrencyInput(repairPrice.value, "Repair Price", displayToastMessage))
            return false

        if (repairTypeInputs.none { f -> f }) {
            displayToastMessage("Please select a repair type")
            return false
        }

        if (!DataHelper.isValidStringInput(repairProvider.value, "Repair Provider", displayToastMessage))
            return false

        // Optional string input does not need validation

        return true
    }

    val onEditClick = {
        isReadOnly.value = false
    }

    val onSaveClick = {
        val repair = getRepairObjectFromInputs()

        if (repair != null) {
            repair.id = repairId
            DataManager.returnActiveVehicle()?.updateRepair(repair)
            displayToastMessage("Changes saved.")
            isReadOnly.value = true
        }
    }

    var isDisplayDeleteDialog = mutableStateOf(false)

    val onDeleteClick = {
        isDisplayDeleteDialog.value = true
    }

    val onConfirmClick = {
        DataManager.returnActiveVehicle()?.deleteRepair(repairId)
        displayToastMessage("Repair record deleted.")
        navigateToVehicle()
    }

    val onDismissClick = {
        isDisplayDeleteDialog.value = false
        displayToastMessage("Deletion cancelled.")
    }
}