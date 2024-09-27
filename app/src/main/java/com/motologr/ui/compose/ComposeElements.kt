package com.motologr.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
fun SliderWithUnits(registrationPrice: MutableState<String> = mutableStateOf(""),
                    sliderPosition : MutableState<Float> = mutableFloatStateOf(0f),
                    sliderSteps : Int, sliderUnits : String, calculationFormula : (Int) -> BigDecimal) {
    var registrationPriceObserver by remember { registrationPrice }
    var sliderPositionObserver by remember { sliderPosition }
    val sliderMaxRange = (sliderSteps + 1).toFloat()
    Slider(
        value = sliderPositionObserver,
        onValueChange = { sliderPositionObserver = it
            registrationPriceObserver = DataHelper.roundToTwoDecimalPlaces(calculationFormula(sliderPositionObserver.roundToInt())) },
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.secondary,
            activeTrackColor = MaterialTheme.colorScheme.secondary,
            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        steps = sliderSteps,
        valueRange = 0f..sliderMaxRange
    )
    Text(text = "${sliderPositionObserver.roundToInt()} $sliderUnits")
}

@ExperimentalMaterial3Api
@Composable
fun DatePickerModal(selectedDate: MutableState<String> = mutableStateOf(""), inputLabel : String) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    selectedDate.value = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedDate.value,
            onValueChange = { },
            label = { Text(inputLabel) },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = !showDatePicker }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select registration purchase date"
                    )
                }
            },
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
        )

        val onDateSelected = { selectedDateMillis : Long? ->
            datePickerState.selectedDateMillis = selectedDateMillis
        }

        if (showDatePicker) {
            DatePickerModalInput(onDateSelected) { showDatePicker = false }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalInput(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker)

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
fun CurrencyInput(priceMutable : MutableState<String>, priceLabel : String, modifier: Modifier = Modifier) {
    var priceObserver by remember { priceMutable }
    OutlinedTextField(
        value = priceObserver,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
        onValueChange = { priceObserver = it },
        label = { Text(priceLabel) },
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
fun StringInput(stringMutable : MutableState<String>, stringLabel : String, modifier: Modifier = Modifier) {
    var stringObserver by remember { stringMutable }
    OutlinedTextField(
        value = stringObserver,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
        onValueChange = { stringObserver = it },
        label = { Text(stringLabel) },
        modifier = modifier.fillMaxWidth()
    )
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}