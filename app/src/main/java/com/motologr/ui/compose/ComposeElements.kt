package com.motologr.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.motologr.data.DataHelper
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun SliderWithUnits(sliderPrice: MutableState<String> = mutableStateOf(""),
                    sliderPosition : MutableState<Float> = mutableFloatStateOf(0f),
                    sliderSteps : Int,
                    sliderUnits : String,
                    calculationFormula : (Int) -> BigDecimal,
                    isReadOnly: Boolean = false) {
    var sliderPriceObserver by remember { sliderPrice }
    var sliderPositionObserver by remember { sliderPosition }
    val sliderMaxRange = (sliderSteps + 1).toFloat()
    Slider(
        value = sliderPositionObserver,
        onValueChange = { sliderPositionObserver = it
            sliderPriceObserver = DataHelper.roundToTwoDecimalPlaces(calculationFormula(sliderPositionObserver.roundToInt())) },
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.secondary,
            activeTrackColor = MaterialTheme.colorScheme.secondary,
            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        steps = sliderSteps,
        valueRange = 0f..sliderMaxRange,
        enabled = !isReadOnly
    )
    Text(text = "${sliderPositionObserver.roundToInt()} $sliderUnits")
}

@Composable
fun SliderWithUnitsForRegistration(sliderPrice: MutableState<String> = mutableStateOf(""),
                                   sliderPosition : MutableState<Float> = mutableFloatStateOf(0f),
                                   sliderSteps : Int,
                                   sliderUnits : String,
                                   calculationFormula : (Int) -> BigDecimal,
                                   oldExpiryDate : MutableState<String>,
                                   newExpiryDate : MutableState<String>,
                                   dateFormula : (String, Int) -> String,
                                   isReadOnly: Boolean = false) {
    var newExpiryDateObserver by remember { newExpiryDate }
    var sliderPriceObserver by remember { sliderPrice }
    var sliderPositionObserver by remember { sliderPosition }
    val sliderMaxRange = (sliderSteps + 1).toFloat()
    Slider(
        value = sliderPositionObserver,
        onValueChange = { sliderPositionObserver = it
            sliderPriceObserver = DataHelper.roundToTwoDecimalPlaces(calculationFormula(sliderPositionObserver.roundToInt()))
            newExpiryDateObserver = dateFormula(oldExpiryDate.value, sliderPositionObserver.roundToInt())
                        },
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.secondary,
            activeTrackColor = MaterialTheme.colorScheme.secondary,
            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        steps = sliderSteps,
        valueRange = 0f..sliderMaxRange,
        enabled = !isReadOnly
    )
    Text(text = "${sliderPositionObserver.roundToInt()} $sliderUnits")
}

@Composable
fun SliderWithUnitsForRoadUserCharges(sliderPrice: MutableState<String> = mutableStateOf(""),
                                   sliderPosition : MutableState<Float> = mutableFloatStateOf(0f),
                                   sliderSteps : Int,
                                   sliderUnits : String,
                                   calculationFormula : (Int) -> BigDecimal,
                                   oldUnitsHeld : MutableState<String>,
                                   newUnitsHeld : MutableState<String>,
                                   rucFormula : (String, Int) -> String,
                                   isReadOnly: Boolean = false) {
    var newUnitsHeldObserver by remember { newUnitsHeld }
    var sliderPriceObserver by remember { sliderPrice }
    var sliderPositionObserver by remember { sliderPosition }
    val sliderMaxRange = (sliderSteps + 1).toFloat()
    Slider(
        value = sliderPositionObserver,
        onValueChange = { sliderPositionObserver = it
            sliderPriceObserver = DataHelper.roundToTwoDecimalPlaces(calculationFormula(sliderPositionObserver.roundToInt()))
            newUnitsHeldObserver = rucFormula(oldUnitsHeld.value, sliderPositionObserver.roundToInt())
        },
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.secondary,
            activeTrackColor = MaterialTheme.colorScheme.secondary,
            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        steps = sliderSteps,
        valueRange = 0f..sliderMaxRange,
        enabled = !isReadOnly
    )
    Text(text = "${sliderPositionObserver.roundToInt()} $sliderUnits")
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@ExperimentalMaterial3Api
@Composable
fun DatePickerModal(selectedDate: MutableState<String> = mutableStateOf(""), inputLabel : String,
                    hasDefaultValue : Boolean = false, isReadOnly : Boolean = false,
                    modifier : Modifier = Modifier) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    if (!hasDefaultValue) {
        selectedDate.value = datePickerState.selectedDateMillis?.let {
            convertMillisToDate(it)
        } ?: ""
    } else {
        selectedDate.value = datePickerState.selectedDateMillis?.let {
            convertMillisToDate(it)
        } ?: selectedDate.value
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedDate.value,
            onValueChange = { },
            label = { Text(inputLabel) },
            readOnly = true,
            trailingIcon = {
                if (!isReadOnly) {
                    IconButton(onClick = { showDatePicker = !showDatePicker }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select registration purchase date"
                        )
                    }
                }
            },
            modifier = modifier
                .height(64.dp)
                .fillMaxWidth()
        )

        val onDateSelected = { selectedDateMillis : Long? ->
            datePickerState.selectedDateMillis = selectedDateMillis
        }

        var initialSelectedDateMillis : Long? = null
        val oneDayInMillis : Long = 86400000
        if (selectedDate.value.isNotEmpty()) {
            initialSelectedDateMillis = DataHelper.parseNumericalDateFormat(selectedDate.value).time + oneDayInMillis
        }

        if (showDatePicker) {
            DatePickerModalInput(onDateSelected,  initialSelectedDateMillis) { showDatePicker = false }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalInput(
    onDateSelected: (Long?) -> Unit,
    initialSelectedDateMillis : Long? = null,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialSelectedDateMillis, initialDisplayMode = DisplayMode.Picker)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState, showModeToggle = false)
    }
}

@Composable
fun CurrencyInput(priceMutable : MutableState<String>, priceLabel : String, modifier: Modifier = Modifier, isReadOnly : Boolean = false) {
    var priceObserver by remember { priceMutable }
    OutlinedTextField(
        value = priceObserver,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
        onValueChange = { priceObserver = it },
        label = { Text(priceLabel) },
        modifier = modifier.fillMaxWidth(),
        readOnly = isReadOnly,
        singleLine = true
    )
}

@Composable
fun NumberInput(numberMutable : MutableState<String>, numberLabel : String, modifier: Modifier = Modifier, isReadOnly: Boolean = false) {
    var stringObserver by remember { numberMutable }
    OutlinedTextField(
        value = stringObserver,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        onValueChange = { stringObserver = it },
        label = { Text(numberLabel) },
        modifier = modifier.fillMaxWidth(),
        readOnly = isReadOnly,
        singleLine = true
    )
}

@Composable
fun StringInput(stringMutable : MutableState<String>, stringLabel : String, modifier: Modifier = Modifier, isReadOnly : Boolean = false) {
    var stringObserver by remember { stringMutable }
    OutlinedTextField(
        value = stringObserver,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
        onValueChange = { stringObserver = it },
        label = { Text(stringLabel) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun MultiLineStringInput(stringMutable : MutableState<String>, stringLabel : String, modifier: Modifier = Modifier, isReadOnly : Boolean = false) {
    var stringObserver by remember { stringMutable }
    OutlinedTextField(
        value = stringObserver,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
        onValueChange = { stringObserver = it },
        label = { Text(stringLabel) },
        modifier = modifier.fillMaxWidth(),
        singleLine = false
    )
}

@Composable
fun WarningDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String
) {
    AlertDialog(
        icon = {
            Icon(Icons.Filled.Warning, contentDescription = "Warning Symbol")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditDeleteFABs(onDeleteClick: () -> Unit = {},
                   onEditClick: () -> Unit = {}) {
    Column(modifier = Modifier
        .fillMaxSize(1f)
        .padding(start = 8.dp, bottom = 8.dp, end = 8.dp),
        verticalArrangement = Arrangement.Bottom) {
        Row {
            SmallFloatingActionButton(
                onClick = { onDeleteClick() },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(64.dp)
            ) {
                Icon(Icons.Filled.Delete, "Small delete button.")
            }
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End) {
                SmallFloatingActionButton(
                    onClick = { onEditClick() },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(Icons.Filled.Edit, "Small edit button.")
                }
            }
        }
    }
}

@Composable
fun SaveFAB(onClick: () -> Unit = {}) {
    Column(modifier = Modifier
        .fillMaxSize(1f)
        .padding(start = 8.dp, bottom = 8.dp, end = 8.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End) {
        SmallFloatingActionButton(
            onClick = { onClick() },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.size(64.dp)
        ) {
            Icon(Icons.Filled.Check, "Small save button.")
        }
    }
}