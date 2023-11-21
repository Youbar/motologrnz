package com.example.motologr.ui.add.reg

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.navigation.fragment.findNavController
import com.example.motologr.R
import com.example.motologr.databinding.FragmentRegBinding
import com.example.motologr.ui.data.DataManager
import com.example.motologr.ui.data.Reg
import com.example.motologr.ui.data.Vehicle
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

        binding.radioGroupRegTypeCol1.setOnCheckedChangeListener { radioGroup, i ->
            if (i != -1)
                binding.radioGroupRegTypeCol2.clearCheck()
        }

        binding.radioGroupRegTypeCol2.setOnCheckedChangeListener { radioGroup, i ->
            if (i != -1)
                binding.radioGroupRegTypeCol1.clearCheck()
        }

        return root
    }

    private fun initialiseSaveButton() {
        // TODO: GET WHICH VEHICLE WE ARE EDITING

        binding.buttonRegSave.setOnClickListener {
            updateReg()
            findNavController().navigate(R.id.action_nav_reg_to_nav_vehicle_1)
        }
    }

    

    private fun getMonthsExtended() : Int {

        var radioButtonId = binding.radioGroupRegTypeCol1.checkedRadioButtonId

        if (radioButtonId == -1) {
            radioButtonId = binding.radioGroupRegTypeCol2.checkedRadioButtonId
        }

        if (radioButtonId == -1) {
            return 0
        }

        val checkedRadioButton = view?.findViewById<RadioButton>(radioButtonId)
        val checkedRadioButtonValue = checkedRadioButton!!.text[0].toString().toInt()

        return checkedRadioButtonValue
    }

    private fun updateReg() {
        val vehicle: Vehicle = DataManager.ReturnVehicle(0) ?: return

        val regExpiryDate = binding.editTextRegCurrDate.text.toString()
        val newRegExpiryDate = binding.editTextRegNextDate.text.toString()
        val monthsExtended = getMonthsExtended()
        val price = binding.editTextRegPrice.text.toString().toDouble()

        val format: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

        val reg: Reg = Reg(format.parse(newRegExpiryDate), format.parse(regExpiryDate), monthsExtended, price)

        vehicle.logReg(reg)

        vehicle.updateRegExpiry(newRegExpiryDate)
    }

    private fun setFragmentText() {
        val vehicle: Vehicle = DataManager.ReturnVehicle(0) ?: return

        binding.editTextRegCurrDate.setText(vehicle.returnRegExpiry())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}