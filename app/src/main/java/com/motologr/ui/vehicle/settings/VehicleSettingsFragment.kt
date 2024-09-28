package com.motologr.ui.vehicle.settings

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.motologr.R
import com.motologr.data.DataManager
import com.motologr.databinding.FragmentVehicleSettingsBinding
import com.motologr.ui.compose.NumberInput
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

        viewModel.setDefaultValues(DataManager.returnActiveVehicle()!!)
        viewModel.displayToastMessage = { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
                .show()
        }
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
                    VehicleSettingsInterface(viewModel)
                }
            }
        }

        return root
    }
}

@Composable
fun VehicleSettingsInterface(viewModel: VehicleSettingsViewModel) {
LazyColumn {
        item {
            GeneralVehicleSettingsCard(viewModel.makeInput, viewModel.modelInput, viewModel.modelYearInput, viewModel.onUpdateClick)
        }
        item {
            ComplianceVehicleSettingsCard(
                viewModel.isUseRucsBoolean,
                viewModel.isUseRucsText,
                viewModel.isUseRucsInput,
                viewModel.onRucSwitch,
                viewModel.isUseCofBoolean,
                viewModel.isUseCofText,
                viewModel.onCofSwitch,
                viewModel.onSaveClick
            )
        }
    }
}

@Composable
fun GeneralVehicleSettingsCard(
    makeInput : MutableState<String>,
    modelInput : MutableState<String>,
    yearInput : MutableState<String>,
    onUpdateClick : () -> Unit
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
        NumberInput(yearInput, "Year", Modifier.padding(16.dp, 8.dp, 16.dp, 16.dp))
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier
            .padding(16.dp, 8.dp, 16.dp, 8.dp)
            .fillMaxWidth()) {
            Button(onClick = onUpdateClick, contentPadding = PaddingValues(8.dp)) {
                Text("Update", fontSize = 3.em, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun ComplianceVehicleSettingsCard(
    isUseRucsBoolean : MutableState<Boolean>,
    isUseRucsText : MutableState<String>,
    isUseRucsInput : MutableState<String>,
    onRucsSwitch : () -> Unit,
    isUseCofBoolean : MutableState<Boolean>,
    isUseCofText : MutableState<String>,
    onCofSwitch : () -> Unit,
    onSaveClick : () -> Unit
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
        SettingsSwitch(Modifier.padding(16.dp, 8.dp), isUseRucsBoolean, isUseRucsText, onRucsSwitch)
        if (isUseRucsBoolean.value) {
            Row {
                NumberInput(isUseRucsInput, "Current RUCs for Vehicle (km)", Modifier.padding(16.dp, 8.dp))
            }
            Text("The value above should only be used to record the initial RUCs for your vehicle when using this app and must not be updated further.",
                modifier = Modifier
                .padding(32.dp, 8.dp)
                .fillMaxWidth(),
                fontSize = 14.sp)
        }
        SettingsSwitch(Modifier.padding(16.dp, 8.dp), isUseCofBoolean, isUseCofText, onCofSwitch, false)
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier
            .padding(16.dp, 8.dp, 16.dp, 8.dp)
            .fillMaxWidth()) {
            Button(onClick = onSaveClick, contentPadding = PaddingValues(8.dp)) {
                Text("Save", fontSize = 3.em, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun SettingsSwitch(
    modifier : Modifier = Modifier,
    isSwitchEnabledBoolean : MutableState<Boolean>,
    isSwitchEnabledText : MutableState<String>,
    onSwitch: () -> Unit,
    isEnabled : Boolean = true
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        var checked by remember { isSwitchEnabledBoolean }

        Switch(
            modifier = modifier,
            checked = checked,
            onCheckedChange = {
                checked = it
                onSwitch()
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