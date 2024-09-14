package com.motologr.ui.plus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.motologr.R
import com.motologr.databinding.FragmentPlusBinding
import com.motologr.data.DataManager
import com.motologr.data.objects.vehicle.Vehicle

class PlusFragment : Fragment() {

    private var _binding: FragmentPlusBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val plusViewModel =
            ViewModelProvider(this).get(PlusViewModel::class.java)

        _binding = FragmentPlusBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textBrand: TextView = binding.textBrandPrompt
        val editTextBrand: EditText = binding.editTextBrandInput
        val textModel: TextView = binding.textModelPrompt
        val editTextModel: EditText = binding.editTextModelInput
        val textYear: TextView = binding.textYearPrompt
        val editTextYear: EditText = binding.editTextYearInput

        plusViewModel.textBrand.observe(viewLifecycleOwner) {
            textBrand.text = it
        }
        plusViewModel.editTextBrand.observe(viewLifecycleOwner) {
            editTextBrand.hint = it
        }
        plusViewModel.textModel.observe(viewLifecycleOwner) {
            textModel.text = it
        }
        plusViewModel.editTextModel.observe(viewLifecycleOwner) {
            editTextModel.hint = it
        }
        plusViewModel.textYear.observe(viewLifecycleOwner) {
            textYear.text = it
        }
        plusViewModel.editTextYear.observe(viewLifecycleOwner) {
            editTextYear.hint = it
        }

        val confirmButton: View = binding.buttonConfirm
        confirmButton.setOnClickListener {
            convertPlusFragmentToObject()
        }

        return root
    }

    private fun convertPlusFragmentToObject() {
        if (!isValidPlusInputs())
            return

        val brandName: String = binding.editTextBrandInput.text.toString()
        val modelName: String = binding.editTextModelInput.text.toString()
        val modelYear: Int = Integer.valueOf(binding.editTextYearInput.text.toString())

        val minDt = DataManager.getMinDt()

        val vehicle = Vehicle(DataManager.fetchIdForVehicle(), brandName, modelName, modelYear, minDt, minDt, -1)

        DataManager.createNewVehicle(vehicle)
        DataManager.setLatestVehicleActive()

        findNavController().navigate(R.id.action_nav_plus_to_nav_vehicle_1, null, NavOptions.Builder()
            .setPopUpTo(R.id.nav_vehicle_1, true).build())
    }

    private fun displayValidationError(toastText : String) {
        Toast.makeText(activity, toastText, Toast.LENGTH_LONG).show()
    }

    private fun isValidPlusInputs() : Boolean {
        if (binding.editTextModelInput.text.toString().isEmpty()) {
            displayValidationError("Please input the vehicle model name")
            return false
        }

        if (binding.editTextYearInput.text.toString().isEmpty()) {
            displayValidationError("Please input the vehicle model year")
            return false
        }

        if (binding.editTextYearInput.text.toString().toIntOrNull() == null) {
            displayValidationError("The value for vehicle model year is too high")
            return false
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}