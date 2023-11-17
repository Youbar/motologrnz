package com.example.motologr.ui.add.insurance

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.motologr.R
import com.example.motologr.databinding.FragmentInsuranceBinding
import com.example.motologr.ui.add.service.ServiceViewModel
import com.example.motologr.ui.data.DataManager
import com.example.motologr.ui.data.Insurance
import java.text.SimpleDateFormat
import java.util.Date

class InsuranceFragment : Fragment() {

    private var _binding: FragmentInsuranceBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val insuranceViewModel =
            ViewModelProvider(this).get(InsuranceViewModel::class.java)

        _binding = FragmentInsuranceBinding.inflate(inflater, container, false)

        val root: View = binding.root

        initialiseSwitch()

        initialiseSaveButton()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initialiseSaveButton() {
        // TODO: GET WHICH VEHICLE WE ARE EDITING

        binding.buttonInsuranceSave.setOnClickListener {
            convertFragmentToInsuranceObject()
            findNavController().navigate(R.id.action_nav_insurance_to_nav_vehicle_1)
        }
    }

    private fun convertFragmentToInsuranceObject() {
        val format: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

        val isActive: Boolean = binding.switchInsuranceBool.isChecked
        val insurerName: String = binding.editTextInsuranceInsurer.text.toString()
        val insuranceType: Int = parseCoverageRadioGroup()
        val insuranceCycle: Int = parseBillingRadioGroup()
        val insuranceValue: Double = binding.editTextInsuranceBill.text.toString().toDouble()
        val insuranceDate: Date = format.parse(binding.editTextInsuranceDate.text.toString())


        val insurance: Insurance = Insurance(isActive, insurerName, insuranceType, insuranceCycle, insuranceValue, insuranceDate)

        DataManager.SetVehicleInsurance(0, insurance)
    }

    private fun parseCoverageRadioGroup() : Int {
        val radioButtonId = binding.radioGroupInsuranceCoverage.checkedRadioButtonId
        val checkedRadioButton = view?.findViewById<RadioButton>(radioButtonId)
        val radioButtonText = checkedRadioButton?.text

        if (radioButtonText == "Comprehensive") {
            return 0
        } else if (radioButtonText == "Third Party Fire &amp; Theft") {
            return 1
        } else if (radioButtonText == "Third Party") {
            return 2
        }

        return -1
    }

    private fun parseBillingRadioGroup() : Int {
        val radioButtonId = binding.radioGroupInsuranceCycle.checkedRadioButtonId
        val checkedRadioButton = view?.findViewById<RadioButton>(radioButtonId)
        val radioButtonText = checkedRadioButton?.text

        if (radioButtonText == "Fortnightly") {
            return 0
        } else if (radioButtonText == "Monthly") {
            return 1
        } else if (radioButtonText == "Annually") {
            return 2
        }

        return -1
    }

    private fun initialiseSwitch() {
        checkSwitchState()

        binding.switchInsuranceBool.setOnCheckedChangeListener { compoundButton, b ->
            checkSwitchState()
        }
    }

    private fun checkSwitchState() {
        val switchIsActive = binding.switchInsuranceBool.isChecked

        binding.textInsuranceInsurer.isVisible = switchIsActive
        binding.editTextInsuranceInsurer.isVisible = switchIsActive
        binding.textInsuranceCoverage.isVisible = switchIsActive
        binding.radioGroupInsuranceCoverage.isVisible = switchIsActive
        binding.textInsuranceBilling.isVisible = switchIsActive
        binding.radioGroupInsuranceCycle.isVisible = switchIsActive
        binding.editTextInsuranceBill.isVisible = switchIsActive
        binding.editTextInsuranceDate.isVisible = switchIsActive
    }
}