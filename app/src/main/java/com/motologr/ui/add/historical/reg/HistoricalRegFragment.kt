@file:OptIn(ExperimentalMaterial3Api::class)

package com.motologr.ui.add.historical.reg

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.shape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.motologr.R
import com.motologr.data.DataHelper
import com.motologr.data.objects.reg.Reg
import com.motologr.databinding.FragmentHistoricalRegBinding
import com.motologr.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class HistoricalRegFragment : Fragment() {
    private var _binding: FragmentHistoricalRegBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val historicalRegViewModel =
            ViewModelProvider(this)[HistoricalRegViewModel::class.java]

        historicalRegViewModel.displayToastMessage = { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
                .show()
        }

        historicalRegViewModel.navigateToVehicle = { findNavController().navigate(R.id.nav_vehicle_1) }

        _binding = FragmentHistoricalRegBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val composeView = root.findViewById<ComposeView>(R.id.compose_view_historical_reg)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    HistoricalRegCard(historicalRegViewModel.registrationPrice, historicalRegViewModel.sliderPosition,
                        historicalRegViewModel.selectedDate, historicalRegViewModel.onClick)
                }
            }
        }

        return root
    }
}

@Composable
fun HistoricalRegCard(registrationPrice: MutableState<String>, sliderPosition: MutableState<Float>,
                      selectedDate: MutableState<String>, onButtonClick: () -> Unit = {}) {
    Card(modifier = Modifier
        .padding(16.dp, 8.dp, 16.dp, 8.dp)
        .border(1.dp, MaterialTheme.colorScheme.secondary, shape)) {
        Column(modifier = Modifier
            .padding(16.dp, 8.dp, 16.dp, 8.dp)
            .height(IntrinsicSize.Min)){
            Text("Historical Registration", fontSize = 24.sp,
                modifier = Modifier
                    .padding(PaddingValues(0.dp, 0.dp))
                    .fillMaxWidth(),
                lineHeight = 1.em,
                textAlign = TextAlign.Center)
            DatePickerDocked(selectedDate)
            HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(PaddingValues(16.dp, 8.dp, 16.dp, 0.dp)))
            HistoricalRegSlider(registrationPrice, sliderPosition)
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier
                .padding(0.dp, 32.dp, 0.dp, 0.dp)
                .fillMaxWidth()) {
                Button(onClick = onButtonClick, contentPadding = PaddingValues(8.dp)) {
                    Text("Save", fontSize = 3.em, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun HistoricalRegSlider(registrationPrice: MutableState<String> = mutableStateOf(""),
                        sliderPosition : MutableState<Float> = mutableFloatStateOf(0f)) {
    var registrationPriceObserver by remember { registrationPrice }
    var sliderPositionObserver by remember { sliderPosition }
    Slider(
        value = sliderPositionObserver,
        onValueChange = { sliderPositionObserver = it
            registrationPriceObserver = DataHelper.roundToTwoDecimalPlaces(Reg.calculateRegistration(sliderPositionObserver.roundToInt())) },
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.secondary,
            activeTrackColor = MaterialTheme.colorScheme.secondary,
            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        steps = 11,
        valueRange = 0f..12f
    )
    Text(text = "${sliderPositionObserver.roundToInt()} months")
    OutlinedTextField(
        value = registrationPriceObserver,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        onValueChange = { registrationPriceObserver = it },
        label = { Text("Purchase Price") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun DatePickerDocked(selectedDate: MutableState<String> = mutableStateOf("")) {
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
            label = { Text("Purchase Date") },
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

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}