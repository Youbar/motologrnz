package com.motologr.ui.plus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.motologr.R
import com.motologr.data.DataManager
import com.motologr.data.getDate
import com.motologr.databinding.FragmentComplianceBinding

class ComplianceFragment : Fragment() {
    private var _binding: FragmentComplianceBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentComplianceBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.textWofLastPrompt.text = "When does your current WOF expire?"
        binding.textRegExpirePrompt.text = "When does your current registration expire?"
        binding.textRoadUserChargesPrompt.text = "Does your vehicle use Road User Charges (RUCs)?"

        setRoadUserChargesRadioGroupVisibility(false)
        binding.radioButtonRucNo.setOnClickListener {
            setRoadUserChargesRadioGroupVisibility(false)
        }

        binding.radioButtonRucYes.setOnClickListener {
            setRoadUserChargesRadioGroupVisibility(true)
        }

        binding.buttonConfirm.setOnClickListener {
            if (isValidComplianceInputs()) {
                val wofExpiry = binding.editTextWofLastInput.getDate()
                val regExpiry = binding.editTextRegExpireInput.getDate()
                val radioGroupOption = parseRoadUserChargesRadioGroup()
                var roadUserChargesHeld = -1

                var isRoadUserCharges = false
                if (radioGroupOption == 1) {
                    isRoadUserCharges = true
                    roadUserChargesHeld = binding.editTextRoadUserChargesHeld.text.toString().toInt()
                }

                DataManager.returnActiveVehicle()?.submitCompliance(wofExpiry, regExpiry)
                DataManager.returnActiveVehicle()?.submitRUCs(isRoadUserCharges, roadUserChargesHeld)
                findNavController().navigate(R.id.nav_vehicle_1, null, NavOptions.Builder()
                    .setPopUpTo(R.id.nav_vehicle_1, true).build())
            }
        }

        return root
    }

    private fun setRoadUserChargesRadioGroupVisibility(isVisible : Boolean) {
        binding.editTextRoadUserChargesHeld.isVisible = isVisible
        binding.textRoadUserChargesHeldPrompt.isVisible = isVisible
        binding.textRoadUserChargesHeldPromptUnits.isVisible = isVisible
    }

    private fun parseRoadUserChargesRadioGroup() : Int {
        val radioButtonId = binding.radioGroupRoadUserCharges.checkedRadioButtonId
        val checkedRadioButton = view?.findViewById<RadioButton>(radioButtonId)
        val radioButtonText = checkedRadioButton?.text

        if (radioButtonText == "No") {
            return 0
        } else if (radioButtonText == "Yes") {
            return 1
        }

        return -1
    }

    private fun isValidComplianceInputs() : Boolean {
        if (parseRoadUserChargesRadioGroup() == -1) {
            displayValidationError("Please indicate whether your vehicle uses Road User Charges")
            return false
        }

        val isUsingRoadUserCharges = parseRoadUserChargesRadioGroup()

        if (isUsingRoadUserCharges == 1) {
            val roadUserChargesHeld = binding.editTextRoadUserChargesHeld.text.toString()

            if (roadUserChargesHeld.isEmpty()) {
                displayValidationError("Please indicate how many RUCs you currently hold")
                return false
            }
        }

        return true
    }

    private fun displayValidationError(toastText : String) {
        Toast.makeText(activity, toastText, Toast.LENGTH_LONG).show()
    }
}