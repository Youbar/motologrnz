package com.motologr.ui.add.fuel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults.shape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.motologr.R
import com.motologr.databinding.FragmentFuelBinding
import com.motologr.data.DataManager
import com.motologr.data.objects.fuel.Fuel
import com.motologr.ui.compose.CurrencyInput
import com.motologr.ui.compose.DatePickerModal
import com.motologr.ui.compose.NumberInput
import com.motologr.ui.compose.WarningDialog
import com.motologr.ui.theme.AppTheme


class FuelFragment : Fragment() {

    private var _binding: FragmentFuelBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fuelViewModel =
            ViewModelProvider(this)[FuelViewModel::class.java]

        _binding = FragmentFuelBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val bundle: Bundle? = arguments
        val logPos: Int? = arguments?.getInt("position")

        var isTrackingFuelConsumption = false
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
        if (sharedPref != null) {
            isTrackingFuelConsumption =
                sharedPref.getBoolean(getString(R.string.fuel_consumption_key), false)
        }

        fuelViewModel.displayToastMessage = { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
                .show()
        }
        fuelViewModel.initFuelViewModel(isTrackingFuelConsumption)
        fuelViewModel.navigateToVehicle = {
            findNavController().navigate(R.id.action_nav_fuel_to_nav_vehicle_1, null, NavOptions.Builder()
                .setPopUpTo(R.id.nav_vehicle_1, true).build())
        }

        if (logPos != null) {
            DataManager.updateTitle(activity, "View Fuel Record")
            val fuel: Fuel = DataManager.returnActiveVehicle()?.fuelLog?.returnFuel(logPos)!!
            fuelViewModel.setViewModelToReadOnly(fuel)
        } else {
            DataManager.updateTitle(activity, "Record Fuel Purchase")
        }

        val composeView = root.findViewById<ComposeView>(R.id.compose_view_fuel)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    Column {
                        FuelLoggingInterface(fuelViewModel)
                        if (fuelViewModel.isExistingData && fuelViewModel.isReadOnly.value)
                            EditDeleteFABs(fuelViewModel.onDeleteClick, fuelViewModel.onEditClick)
                        else if (fuelViewModel.isExistingData && !fuelViewModel.isReadOnly.value)
                            SaveFAB(fuelViewModel.onSaveClick)
                        if (fuelViewModel.isDisplayDeleteDialog.value) {
                            WarningDialog(fuelViewModel.onDismissClick, fuelViewModel.onConfirmClick, "Delete Record", "Are you sure you want to delete this record? The deletion is irreversible.")
                        }
                    }
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelLoggingInterface(viewModel: FuelViewModel) {
    LazyColumn {
        item {
            OutlinedCard(modifier = Modifier
                .padding(16.dp, 8.dp, 16.dp, 8.dp)
                .border(1.dp, MaterialTheme.colorScheme.secondary, shape)
                .height(IntrinsicSize.Min)) {
                Column(modifier = Modifier
                    .padding(16.dp, 8.dp, 16.dp, 8.dp)
                    .height(IntrinsicSize.Min)){
                    Text(viewModel.fuelCardTitle, fontSize = 24.sp,
                        modifier = Modifier
                            .padding(PaddingValues(0.dp, 0.dp))
                            .fillMaxWidth(),
                        lineHeight = 1.em,
                        textAlign = TextAlign.Center)
                    DatePickerModal(viewModel.fuelDate, "Purchase Date", true, viewModel.isReadOnly.value)
                    CurrencyInput(viewModel.fuelPrice, "Purchase Price", isReadOnly = viewModel.isReadOnly.value)
                    RowOfFuelTypes(viewModel.is91Checked, viewModel.is95Checked, viewModel.is98Checked, viewModel.isDieselChecked,
                        viewModel.onBoxChecked, viewModel.isReadOnly.value)

                    if (viewModel.isTrackingFuelConsumption.value) {
                        HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(PaddingValues(16.dp)))
                        Text("Fuel Consumption Data", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 20.sp)
                        NumberInput(viewModel.fuelOdometer, "Odometer", isReadOnly = viewModel.isExistingData)
                        CurrencyInput(viewModel.fuelLitres, "Litres", isReadOnly = viewModel.isExistingData)
                    }

                    if (!viewModel.isReadOnly.value && !viewModel.isExistingData) {
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier
                            .padding(0.dp, 32.dp, 0.dp, 0.dp)
                            .fillMaxWidth()) {
                            Button(onClick = viewModel.onRecordClick, contentPadding = PaddingValues(8.dp)) {
                                Text("Record", fontSize = 3.em, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowOfFuelTypes(is91Checked : MutableState<Boolean>, is95Checked : MutableState<Boolean>,
                   is98Checked : MutableState<Boolean>, isDieselChecked : MutableState<Boolean>,
                   onBoxChecked: (Int) -> Unit, isReadOnly: Boolean) {
    Row {
        Column {
            FuelTypeCheckbox(is91Checked, "91 Unleaded", onBoxChecked, 0, isReadOnly)
            FuelTypeCheckbox(is95Checked, "95 Unleaded", onBoxChecked, 1, isReadOnly)
        }
        Column (horizontalAlignment = Alignment.End) {
            FuelTypeCheckbox(is98Checked, "98 Unleaded", onBoxChecked, 2, isReadOnly)
            FuelTypeCheckbox(isDieselChecked, "Diesel", onBoxChecked, 3, isReadOnly)
        }
    }
}

@Composable
fun FuelTypeCheckbox(checkboxBoolean : MutableState<Boolean>, checkboxText : String, onBoxChecked : (Int) -> Unit, fuelTypeId : Int, isReadOnly : Boolean) {
    var boxChecked by remember { checkboxBoolean }
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            checkboxText
        )
        Checkbox(
            checked = boxChecked,
            onCheckedChange = {
                boxChecked = it
                onBoxChecked(fuelTypeId)
            },
            enabled = !isReadOnly
        )
    }
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
                shape = CircleShape,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(Icons.Filled.Delete, "Small delete button.")
            }
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End) {
                SmallFloatingActionButton(
                    onClick = { onEditClick() },
                    shape = CircleShape,
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
            shape = CircleShape,
            modifier = Modifier.size(64.dp)
        ) {
            Icon(Icons.Filled.Check, "Small save button.")
        }
    }
}