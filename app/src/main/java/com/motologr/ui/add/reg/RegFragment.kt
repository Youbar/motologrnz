package com.motologr.ui.add.reg

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.motologr.R
import com.motologr.databinding.FragmentRegBinding
import com.motologr.ui.data.DataManager
import com.motologr.ui.data.Reg
import com.motologr.ui.data.Vehicle
import com.motologr.ui.data.getDate
import java.text.SimpleDateFormat

class RegFragment : Fragment() {

    private var _binding: FragmentRegBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val regViewModel =
            ViewModelProvider(this).get(RegViewModel::class.java)

        _binding = FragmentRegBinding.inflate(inflater, container, false)

        val root: View = binding.root

        setFragmentText()
        initialiseSaveButton()
        setOnCheckedChangeListener()

        return root
    }

    private var isChangingRadioGroup : Boolean = false

    private fun setOnCheckedChangeListener() {
        binding.radioGroupRegTypeCol1.setOnCheckedChangeListener { radioGroup, i ->
            if (i != -1 && !isChangingRadioGroup) {
                isChangingRadioGroup = true
                binding.radioGroupRegTypeCol2.clearCheck()
                isChangingRadioGroup = false
            }
        }

        binding.radioGroupRegTypeCol2.setOnCheckedChangeListener { radioGroup, i ->
            if (i != -1 && !isChangingRadioGroup) {
                isChangingRadioGroup = true
                binding.radioGroupRegTypeCol1.clearCheck()
                isChangingRadioGroup = false
            }
        }
    }

    private fun initialiseSaveButton() {
        binding.buttonRegSave.setOnClickListener {
            updateReg()
        }
    }



    private fun getMonthsExtended() : Int {

        var radioButtonId = binding.radioGroupRegTypeCol1.checkedRadioButtonId

        if (radioButtonId == -1) {
            radioButtonId = binding.radioGroupRegTypeCol2.checkedRadioButtonId
        }

        if (radioButtonId == -1) {
            return -1
        }

        val checkedRadioButton = view?.findViewById<RadioButton>(radioButtonId)
        val checkedRadioButtonText = checkedRadioButton!!.text.toString()

        if (checkedRadioButtonText == "10 Months")
            return 10
        else if (checkedRadioButtonText == "11 Months")
            return 11
        else if (checkedRadioButtonText == "12 Months")
            return 12
        else
         return checkedRadioButton!!.text[0].toString().toInt()
    }

    private fun updateReg() {
        if (!isValidRegInputs())
            return

        val vehicle: Vehicle = DataManager.ReturnActiveVehicle() ?: return

        val regExpiryDate = binding.editTextRegCurrDate.text.toString()
        val newRegExpiryDate = binding.editTextRegNextDate.getDate()
        val monthsExtended = getMonthsExtended()
        val price = binding.editTextRegPrice.text.toString().toDouble()

        val format: SimpleDateFormat = SimpleDateFormat("dd/MMM/yyyy")

        val reg: Reg = Reg(newRegExpiryDate, format.parse(regExpiryDate), monthsExtended, price)

        vehicle.logReg(reg)

        vehicle.updateRegExpiry(newRegExpiryDate)
        findNavController().navigate(R.id.action_nav_reg_to_nav_vehicle_1)
    }

    private fun displayValidationError(toastText : String) {
        Toast.makeText(activity, toastText, Toast.LENGTH_LONG).show()
    }

    private fun isValidRegInputs() : Boolean {
        if (getMonthsExtended() == -1) {
            displayValidationError("Please select a registration extension")
            return false
        }

        // DatePicker does not need validation

        if (binding.editTextRegPrice.text.toString().isEmpty()) {
            displayValidationError("Please input the new registration price")
            return false
        }

        return true
    }

    private fun setFragmentText() {
        val vehicle: Vehicle = DataManager.ReturnActiveVehicle() ?: return

        binding.editTextRegCurrDate.setText(vehicle.returnRegExpiry())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}