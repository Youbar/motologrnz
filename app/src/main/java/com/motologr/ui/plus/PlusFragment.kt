package com.motologr.ui.plus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.motologr.R
import com.motologr.databinding.FragmentPlusBinding
import com.motologr.ui.data.DataManager
import com.motologr.ui.data.objects.vehicle.Vehicle
import com.motologr.ui.data.getDate
import java.util.Date

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
        val textLastWOF: TextView = binding.textWofLastPrompt
        val editTextLastWOF: DatePicker = binding.editTextWofLastInput
        val textCurrentReg: TextView = binding.textRegExpirePrompt
        val editTextCurrentReg: DatePicker = binding.editTextRegExpireInput
        val textCurrentOdo: TextView = binding.textOdoPrompt
        val editTextCurrentOdo: EditText = binding.editTextOdoInput

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
        plusViewModel.textLastWOF.observe(viewLifecycleOwner) {
            textLastWOF.text = it
        }
        plusViewModel.textCurrentReg.observe(viewLifecycleOwner) {
            textCurrentReg.text = it
        }
        plusViewModel.textCurrOdo.observe(viewLifecycleOwner) {
            textCurrentOdo.text = it
        }
        plusViewModel.editTextCurrOdo.observe(viewLifecycleOwner) {
            editTextCurrentOdo.hint = it
        }

        val button: View = binding.buttonConfirm
        button.setOnClickListener {
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
        val modelWOF: Date = binding.editTextWofLastInput.getDate()
        val modelReg: Date = binding.editTextRegExpireInput.getDate()
        val odometer: Int = Integer.valueOf(binding.editTextOdoInput.text.toString())

        val vehicle = Vehicle(DataManager.FetchIdForVehicle(), brandName, modelName, modelYear, modelWOF, modelReg, odometer)

        DataManager.CreateNewVehicle(vehicle)
        DataManager.setLatestVehicleActive()

        findNavController().navigate(R.id.nav_vehicle_1)
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

        // DatePicker does not need validation

        // DatePicker does not need validation

        if (binding.editTextOdoInput.text.toString().isEmpty()) {
            displayValidationError("Please input the vehicle odometer reading")
            return false
        }

        if (binding.editTextYearInput.text.toString().toIntOrNull() == null) {
            displayValidationError("The value for vehicle odometer is too high")
            return false
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}