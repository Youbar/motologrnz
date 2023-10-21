package com.example.motologr.ui.add.fuel

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.example.motologr.R
import com.example.motologr.databinding.FragmentAddBinding
import com.example.motologr.databinding.FragmentFuelBinding
import com.example.motologr.ui.data.DataManager
import com.example.motologr.ui.data.Fuel
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

        return root
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

/*class FuelFragment : Fragment() {

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

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}*/
