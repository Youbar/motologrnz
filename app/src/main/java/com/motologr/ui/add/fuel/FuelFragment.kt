package com.motologr.ui.add.fuel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults.shape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.motologr.R
import com.motologr.databinding.FragmentFuelBinding
import com.motologr.data.DataManager
import com.motologr.data.objects.fuel.Fuel
import com.motologr.data.getDate
import com.motologr.data.toCalendar
import com.motologr.ui.compose.CurrencyInput
import com.motologr.ui.compose.DatePickerModal
import com.motologr.ui.compose.NumberInput
import com.motologr.ui.theme.AppTheme
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Calendar
import java.util.Date


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

        val composeView = root.findViewById<ComposeView>(R.id.compose_view_fuel)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    FuelLoggingInterface(fuelViewModel)
                }
            }
        }


        val bundle: Bundle? = arguments
        val logPos: Int? = arguments?.getInt("position")

        var trackingFuelConsumption = false
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this.requireContext())

        if (sharedPref != null) {
            trackingFuelConsumption = sharedPref.getBoolean(getString(R.string.fuel_consumption_key), false)
            // View model input here
        }

        if (logPos != null) {
            DataManager.updateTitle(activity, "View Fuel Record")
            val fuel: Fuel = DataManager.returnActiveVehicle()?.fuelLog?.returnFuel(logPos)!!
            //setInterfaceToReadOnly(fuel)
        } else {
            DataManager.updateTitle(activity, "Record Fuel Purchase")
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun convertFragmentToFuelObject(trackingFuelConsumption: Boolean) {

        if (!isValidFuelInputs(trackingFuelConsumption)) {
            return
        }

        val vehicleId: Int = DataManager.returnActiveVehicle()?.id!!
        val fuelType: Int = parseFuelTypeRadioGroup()
        val price: BigDecimal = binding.editTextFuelPrice.text.toString()
            .replace(",","").toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val purchaseDate: Date = binding.editTextFuelDate.getDate()

        val fuel: Fuel
        if (trackingFuelConsumption) {
            val litres: BigDecimal = binding.editTextFuelLitres.text.toString()
                .replace(",","").toBigDecimal().setScale(2, RoundingMode.HALF_UP)
            val odometer: Int = binding.editTextFuelOdo.text.toString().toInt()

            fuel = Fuel(fuelType, price, litres, purchaseDate, odometer, vehicleId)
        } else {
            fuel = Fuel(fuelType, price, (-1.0).toBigDecimal(), purchaseDate, -1, vehicleId)
        }

        DataManager.returnActiveVehicle()?.logFuel(fuel)
        findNavController().navigate(R.id.action_nav_fuel_to_nav_vehicle_1, null, NavOptions.Builder()
            .setPopUpTo(R.id.nav_vehicle_1, true).build())
    }

    private fun displayValidationError(toastText : String) {
        Toast.makeText(activity, toastText, Toast.LENGTH_LONG).show()
    }

    private fun isValidFuelInputs(trackingFuelConsumption: Boolean) : Boolean {
        if (parseFuelTypeRadioGroup() == -1) {
            displayValidationError("Please select a fuel type")
            return false
        }

        if (binding.editTextFuelPrice.text.toString().isEmpty()) {
            displayValidationError("Please input a fuel price")
            return false
        }

        if (trackingFuelConsumption && binding.editTextFuelLitres.text.toString().isEmpty()) {
            displayValidationError("Please input a fuel quantity")
            return false
        }

        if (trackingFuelConsumption && binding.editTextFuelLitres.text.toString() == "0") {
            displayValidationError("You cannot record a value of 0 litres purchased")
            return false
        }

        // DatePicker does not need validation

        if (trackingFuelConsumption && binding.editTextFuelOdo.text.toString().isEmpty()) {
            displayValidationError("Please input odometer reading")
            return false
        }

        return true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelLoggingInterface(viewModel: FuelViewModel) {
    OutlinedCard(modifier = Modifier
        .padding(16.dp, 8.dp, 16.dp, 8.dp)
        .border(1.dp, MaterialTheme.colorScheme.secondary, shape)) {
        Column(modifier = Modifier
            .padding(16.dp, 8.dp, 16.dp, 8.dp)
            .height(IntrinsicSize.Min)){
            Text("Record Fuel Purchase", fontSize = 24.sp,
                modifier = Modifier
                    .padding(PaddingValues(0.dp, 0.dp))
                    .fillMaxWidth(),
                lineHeight = 1.em,
                textAlign = TextAlign.Center)
            DatePickerModal(viewModel.fuelDate, "Purchase Date")
            CurrencyInput(viewModel.fuelPrice, "Purchase Price")
            RowOfFuelTypes(viewModel.is91Checked, viewModel.is95Checked, viewModel.is98Checked, viewModel.isDieselChecked,
                viewModel.onBoxChecked)
            HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(PaddingValues(16.dp)))
            Text("Fuel Consumption Data", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 20.sp)
            NumberInput(viewModel.fuelOdometer, "Odometer")
            CurrencyInput(viewModel.fuelLitres, "Litres")
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier
                .padding(0.dp, 32.dp, 0.dp, 0.dp)
                .fillMaxWidth()) {
                Button(onClick = {}, contentPadding = PaddingValues(8.dp)) {
                    Text("Record", fontSize = 3.em, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun RowOfFuelTypes(is91Checked : MutableState<Boolean>, is95Checked : MutableState<Boolean>,
                   is98Checked : MutableState<Boolean>, isDieselChecked : MutableState<Boolean>,
                   onBoxChecked: (Int) -> Unit) {
    Row {
        Column {
            FuelTypeCheckbox(is91Checked, "91 Unleaded", onBoxChecked, 0)
            FuelTypeCheckbox(is95Checked, "95 Unleaded", onBoxChecked, 1)
        }
        Column (horizontalAlignment = Alignment.End) {
            FuelTypeCheckbox(is98Checked, "98 Unleaded", onBoxChecked, 2)
            FuelTypeCheckbox(isDieselChecked, "Diesel", onBoxChecked, 3)
        }
    }
}

@Composable
fun FuelTypeCheckbox(checkboxBoolean : MutableState<Boolean>, checkboxText : String, onBoxChecked : (Int) -> Unit, fuelTypeId : Int) {
    var boxChecked by remember { checkboxBoolean }
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            checkboxText
        )
        Checkbox(
            checked = boxChecked,
            onCheckedChange = { boxChecked = it;
                onBoxChecked(fuelTypeId)
            }
        )
    }
}