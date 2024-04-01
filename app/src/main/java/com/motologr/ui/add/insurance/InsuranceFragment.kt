package com.motologr.ui.add.insurance

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.motologr.R
import com.motologr.databinding.FragmentInsuranceBinding
import com.motologr.ui.data.DataManager
import com.motologr.ui.data.Insurance
import com.motologr.ui.data.getDate
import com.motologr.ui.data.toCalendar
import java.util.Calendar
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

        val isInsuranceInitalized: Boolean = DataManager.ReturnActiveVehicle()?.isInsuranceInitialised() == true

        if (isInsuranceInitalized) {
            val insurance = DataManager.ReturnActiveVehicle()?.insurance
            if (insurance != null)
                showCurrentData(insurance)
        }

        return root
    }

    private fun UpdateDatePicker(date: Date) {
        val calendar: Calendar = Calendar.getInstance().toCalendar(date)
        val day =  calendar.get(Calendar.DAY_OF_MONTH)
        val month =  calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        binding.editTextInsuranceDate.updateDate(year, month, day)
    }

    private fun showCurrentData(insurance: Insurance) {
        val isActive: Boolean = insurance.isActive
        val insurerName: String = insurance.insurer
        val insuranceType: Int = insurance.coverage
        val insuranceCycle: Int = insurance.billingCycle
        val insuranceValue: Double = insurance.billing
        val insuranceDate: Date = insurance.lastBill

        binding.switchInsuranceBool.isChecked = isActive
        binding.editTextInsuranceInsurer.setText(insurerName)
        binding.radioGroupInsuranceCoverage.check(binding.radioGroupInsuranceCoverage.getChildAt(insuranceType).id)
        binding.radioGroupInsuranceCycle.check(binding.radioGroupInsuranceCycle.getChildAt(insuranceCycle).id)
        binding.editTextInsuranceBill.setText(insuranceValue.toString())
        UpdateDatePicker(insuranceDate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initialiseSaveButton() {
        binding.buttonInsuranceSave.setOnClickListener {
            convertFragmentToInsuranceObject()
        }
    }

    private fun convertFragmentToInsuranceObject() {
        val isActive: Boolean = binding.switchInsuranceBool.isChecked

        if (!isActive) {
            DataManager.ReturnActiveVehicle()?.setVehicleInsuranceInactive()
            findNavController().navigate(R.id.action_nav_insurance_to_nav_vehicle_1)
        }

        if (!isValidInsuranceInputs())
            return

        val insurerName: String = binding.editTextInsuranceInsurer.text.toString()
        val insuranceType: Int = parseCoverageRadioGroup()
        val insuranceCycle: Int = parseBillingRadioGroup()
        val insuranceValue: Double = binding.editTextInsuranceBill.text.toString().toDouble()
        val insuranceDate: Date = binding.editTextInsuranceDate.getDate()

        val insurance: Insurance = Insurance(isActive, insurerName, insuranceType, insuranceCycle, insuranceValue, insuranceDate)

        DataManager.ReturnActiveVehicle()?.setVehicleInsurance(insurance)

        findNavController().navigate(R.id.action_nav_insurance_to_nav_vehicle_1)
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

    private fun displayValidationError(toastText : String) {
        Toast.makeText(activity, toastText, Toast.LENGTH_LONG).show()
    }

    private fun isValidInsuranceInputs() : Boolean {
        if (binding.editTextInsuranceInsurer.text.toString().isEmpty()) {
            displayValidationError("Please input your insurer")
            return false
        }

        if (parseCoverageRadioGroup() == -1) {
            displayValidationError("Please select a coverage type")
            return false
        }

        if (parseBillingRadioGroup() == -1) {
            displayValidationError("Please select a billing cycle")
            return false
        }

        if (binding.editTextInsuranceBill.text.toString().isEmpty()) {
            displayValidationError("Please input your insurance payment")
            return false
        }

        // DatePicker does not need validation

        return true
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
        binding.textInsuranceDate.isVisible = switchIsActive
        binding.editTextInsuranceDate.isVisible = switchIsActive
    }
}