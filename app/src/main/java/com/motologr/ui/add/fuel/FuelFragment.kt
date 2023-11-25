package com.motologr.ui.add.fuel

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.motologr.R
import com.motologr.databinding.FragmentFuelBinding
import com.motologr.ui.data.DataManager
import com.motologr.ui.data.Fuel
import java.text.SimpleDateFormat
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

        addEventListeners()

        val bundle: Bundle? = arguments
        val logPos: Int? = arguments?.getInt("position");

        var fuel: Fuel

        if (logPos != null) {
            fuel = DataManager.ReturnVehicle(0)?.fuelLog?.returnFuel(logPos)!!
            setInterfaceToReadOnly(fuel)
        }

        return root
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

        val format: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        binding.editTextFuelDate.isEnabled = false
        binding.editTextFuelDate.setText(format.format(fuel.purchaseDate))

        binding.editTextFuelOdo.isEnabled = false
        binding.editTextFuelOdo.setText(fuel.odometerReading.toString())

        binding.buttonFuelAdd.isVisible = false
        binding.buttonFuelAdd.isEnabled = false
    }

    private fun addEventListeners() {

        binding.buttonFuelAdd.setOnClickListener {
            convertFragmentToFuelObject()
            findNavController().navigate(R.id.action_nav_fuel_to_nav_vehicle_1)
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

        if (!price.isNullOrEmpty() && !litres.isNullOrEmpty()) {
            binding.textFuelEstimateField.text = (price.toDouble() / litres.toDouble()).toString()
        } else {
            binding.textFuelEstimateField.text = ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun convertFragmentToFuelObject() {
        val format: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

        val fuelType: Int = parseFuelTypeRadioGroup()
        val price: Double = binding.editTextFuelPrice.text.toString().toDouble()
        val litres: Double = binding.editTextFuelLitres.text.toString().toDouble()
        val purchaseDate: Date = format.parse(binding.editTextFuelDate.text.toString())
        val odometer: Int = binding.editTextFuelOdo.text.toString().toInt()

        val fuel: Fuel = Fuel(fuelType, price, litres, purchaseDate, odometer)

        DataManager.ReturnVehicle(0)?.logFuel(fuel)
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
}