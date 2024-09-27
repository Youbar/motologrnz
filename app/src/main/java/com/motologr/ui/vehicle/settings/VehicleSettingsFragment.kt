package com.motologr.ui.vehicle.settings

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.motologr.R
import com.motologr.databinding.FragmentVehicleSettingsBinding
import com.motologr.ui.compose.StringInput
import com.motologr.ui.theme.AppTheme

class VehicleSettingsFragment : Fragment() {
    private var _binding: FragmentVehicleSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private val viewModel: VehicleSettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVehicleSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val composeView = root.findViewById<ComposeView>(R.id.compose_view_vehicle_settings)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    VehicleSettingsInterface()
                }
            }
        }

        return root
    }
}

@Preview
@Composable
fun VehicleSettingsInterface() {
LazyColumn {
        item {
            GeneralVehicleSettingsCard()
        }
        item {
            ComplianceVehicleSettingsCard()
        }
    }
}

@Composable
fun GeneralVehicleSettingsCard(
    makeInput : MutableState<String> = mutableStateOf("Mazda"),
    modelInput : MutableState<String> = mutableStateOf("323"),
    yearInput : MutableState<String> = mutableStateOf("1989")
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Text(
            text = "General",
            modifier = Modifier
                .padding(16.dp, 8.dp, 0.dp, 0.dp)
                .fillMaxWidth(),
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
        StringInput(makeInput, "Make", Modifier.padding(16.dp, 8.dp))
        StringInput(modelInput, "Model", Modifier.padding(16.dp, 8.dp))
        StringInput(yearInput, "Year", Modifier.padding(16.dp, 8.dp, 16.dp, 16.dp))
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier
            .padding(16.dp, 8.dp, 16.dp, 8.dp)
            .fillMaxWidth()) {
            Button(onClick = { }, contentPadding = PaddingValues(8.dp)) {
                Text("Update", fontSize = 3.em, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun ComplianceVehicleSettingsCard(
    isUseRucsBoolean : MutableState<Boolean> = mutableStateOf(true),
    isUseRucsText : MutableState<String> = mutableStateOf("This vehicle requires RUCs"),
    isUseRucsInput : MutableState<String> = mutableStateOf(""),
    isUseCofBoolean : MutableState<Boolean> = mutableStateOf(false),
    isUseCofText : MutableState<String> = mutableStateOf("This vehicle does not require COFs"),
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Text(
            text = "Compliance",
            modifier = Modifier
                .padding(16.dp, 8.dp, 0.dp, 0.dp)
                .fillMaxWidth(),
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
        SettingsSwitch(Modifier.padding(16.dp, 8.dp), isUseRucsBoolean, isUseRucsText)
        if (isUseRucsBoolean.value) {
            Row {
                StringInput(isUseRucsInput, "Current RUCs for Vehicle (km)", Modifier.padding(16.dp, 8.dp))
            }
        }
        SettingsSwitch(Modifier.padding(16.dp, 8.dp), isUseCofBoolean, isUseCofText, false)
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier
            .padding(16.dp, 8.dp, 16.dp, 8.dp)
            .fillMaxWidth()) {
            Button(onClick = { }, contentPadding = PaddingValues(8.dp)) {
                Text("Save", fontSize = 3.em, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun SettingsSwitch(
    modifier : Modifier = Modifier,
    isSwitchEnabledBoolean : MutableState<Boolean> = mutableStateOf(false),
    isSwitchEnabledText : MutableState<String> = mutableStateOf("This vehicle does not use RUCs"),
    isEnabled : Boolean = true
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        var checked by remember { isSwitchEnabledBoolean }

        Switch(
            modifier = modifier,
            checked = checked,
            onCheckedChange = {
                checked = it
            },
            enabled = isEnabled
        )
        Text(text = isSwitchEnabledText.value,
            modifier = Modifier
                .padding(16.dp, 8.dp)
                .fillMaxWidth(),
            fontSize = 14.sp)
    }
}