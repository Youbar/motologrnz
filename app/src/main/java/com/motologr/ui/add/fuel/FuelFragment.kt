package com.motologr.ui.add.fuel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
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
        val addViewModel =
            ViewModelProvider(this).get(FuelViewModel::class.java)

        _binding = FragmentFuelBinding.inflate(inflater, container, false)

        val root: View = binding.root


        val bundle: Bundle? = arguments
        val logPos: Int? = arguments?.getInt("position");

        var trackingFuelConsumption = false
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this.requireContext())

        if (sharedPref != null) {
            trackingFuelConsumption = sharedPref.getBoolean(getString(R.string.fuel_consumption_key), false)
            binding.textFuelOdoPrompt.isVisible = trackingFuelConsumption
            binding.editTextFuelOdo.isVisible = trackingFuelConsumption
            binding.textFuelLitresPrompt.isVisible = trackingFuelConsumption
            binding.editTextFuelLitres.isVisible = trackingFuelConsumption
            binding.textFuelEstimate.isVisible = trackingFuelConsumption
            binding.textFuelEstimateField.isVisible = trackingFuelConsumption
        }

        addEventListeners(trackingFuelConsumption)

        if (logPos != null) {
            DataManager.updateTitle(activity, "View Fuel Record")
            var fuel: Fuel = DataManager.returnActiveVehicle()?.fuelLog?.returnFuel(logPos)!!
            setInterfaceToReadOnly(fuel)
        } else {
            DataManager.updateTitle(activity, "Record Fuel Purchase")
        }

        return root
    }

    private fun UpdateDatePicker(date: Date) {
        val calendar: Calendar = Calendar.getInstance().toCalendar(date)
        val day =  calendar.get(Calendar.DAY_OF_MONTH)
        val month =  calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        binding.editTextFuelDate.updateDate(year, month, day)
    }

    private fun setInterfaceToReadOnly(fuel: Fuel) {
        binding.radioGroupFuelType.check(binding.radioGroupFuelType.getChildAt(fuel.fuelType).id)
        binding.radioButtonFuel91.isClickable = false
        binding.radioButtonFuel91.isEnabled = false
        binding.radioButtonFuel95.isClickable = false
        binding.radioButtonFuel95.isEnabled = false
        binding.radioButtonFuel98.isClickable = false
        binding.radioButtonFuel98.isEnabled = false
        binding.radioButtonFuelDiesel.isClickable = false
        binding.radioButtonFuelDiesel.isEnabled = false

        binding.editTextFuelPrice.isEnabled = false
        binding.editTextFuelPrice.setText(fuel.price.toString())

        binding.editTextFuelLitres.isEnabled = false
        binding.editTextFuelLitres.setText(fuel.litres.toString())

        binding.editTextFuelDate.isEnabled = false
        UpdateDatePicker(fuel.purchaseDate)

        binding.editTextFuelOdo.isEnabled = false
        binding.editTextFuelOdo.setText(fuel.odometerReading.toString())

        binding.buttonFuelAdd.isVisible = false
        binding.buttonFuelAdd.isEnabled = false
    }

    private fun addEventListeners(trackingFuelConsumption: Boolean) {

        binding.buttonFuelAdd.setOnClickListener {
            convertFragmentToFuelObject(trackingFuelConsumption)
        }

        binding.editTextFuelPrice.doAfterTextChanged {
            fuelPriceOrVolumeModified()
        }

        binding.editTextFuelLitres.doAfterTextChanged {
            fuelPriceOrVolumeModified()
        }
    }

    private fun fuelPriceOrVolumeModified() {
        val price: String = binding.editTextFuelPrice.text.toString()
        val litres: String = binding.editTextFuelLitres.text.toString()

        if (price.isNotEmpty() && litres.isNotEmpty()) {
            binding.textFuelEstimateField.text = "$" + DataManager.roundOffDecimal(price.toDouble() / litres.toDouble())
        } else {
            binding.textFuelEstimateField.text = ""
        }
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
            .replace(",","").toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
        val purchaseDate: Date = binding.editTextFuelDate.getDate()

        var fuel: Fuel
        if (trackingFuelConsumption) {
            val litres: BigDecimal = binding.editTextFuelLitres.text.toString()
                .replace(",","").toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
            val odometer: Int = binding.editTextFuelOdo.text.toString().toInt()

            fuel = Fuel(fuelType, price, litres, purchaseDate, odometer, vehicleId)
        } else {
            fuel = Fuel(fuelType, price, -1.0.toBigDecimal(), purchaseDate, -1, vehicleId);
        }

        DataManager.returnActiveVehicle()?.logFuel(fuel)
        findNavController().navigate(R.id.action_nav_fuel_to_nav_vehicle_1, null, NavOptions.Builder()
            .setPopUpTo(R.id.nav_vehicle_1, true).build())
    }

    private fun parseFuelTypeRadioGroup() : Int {
        val radioButtonId = binding.radioGroupFuelType.checkedRadioButtonId
        val checkedRadioButton = view?.findViewById<RadioButton>(radioButtonId)
        val radioButtonText = checkedRadioButton?.text

        if (radioButtonText == "91 Unleaded") {
            return 0
        } else if (radioButtonText == "95 Unleaded") {
            return 1
        } else if (radioButtonText == "98 Unleaded") {
            return 2
        } else if (radioButtonText == "Diesel") {
            return 3
        }

        return -1
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