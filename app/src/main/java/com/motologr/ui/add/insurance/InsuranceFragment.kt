package com.motologr.ui.add.insurance

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.motologr.R
import com.motologr.databinding.FragmentInsuranceBinding
import com.motologr.data.DataManager
import com.motologr.data.objects.insurance.Insurance
import com.motologr.data.getDate
import com.motologr.data.toCalendar
import java.math.BigDecimal
import java.math.RoundingMode
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

        DataManager.updateTitle(activity, "Add Insurance Policy")

        initialiseSaveButton()

        return root
    }

    private fun updateDatePicker(date: Date) {
        val calendar: Calendar = Calendar.getInstance().toCalendar(date)
        val day =  calendar.get(Calendar.DAY_OF_MONTH)
        val month =  calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        binding.editTextInsuranceDate.updateDate(year, month, day)
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
        if (!isValidInsuranceInputs())
            return

        val vehicleId: Int = DataManager.returnActiveVehicle()?.id!!
        val insurerName: String = binding.editTextInsuranceInsurer.text.toString()
        val insurancePolicyStartDate: Date = binding.editTextInsurancePolicyStartDate.getDate()
        val insuranceType: Int = parseCoverageRadioGroup()
        val insuranceCycle: Int = parseBillingRadioGroup()
        val insuranceValue: BigDecimal = binding.editTextInsuranceBill.text.toString()
            .replace(",","").toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
        val insuranceDate: Date = binding.editTextInsuranceDate.getDate()

        val insurance = Insurance(DataManager.fetchIdForInsurance(), insurerName, insurancePolicyStartDate, insuranceType, insuranceCycle, insuranceValue, insuranceDate, vehicleId)
        insurance.generateInsuranceBills()

        DataManager.returnActiveVehicle()?.logInsurance(insurance)

        findNavController().navigate(R.id.action_nav_insurance_to_nav_vehicle_1, null, NavOptions.Builder()
            .setPopUpTo(R.id.nav_vehicle_1, true).build())
    }

    private fun parseCoverageRadioGroup() : Int {
        val radioButtonId = binding.radioGroupInsuranceCoverage.checkedRadioButtonId
        val checkedRadioButton = view?.findViewById<RadioButton>(radioButtonId)

        return when (checkedRadioButton?.text) {
            "Comprehensive" -> {
                0
            }
            "Third Party Fire & Theft" -> {
                1
            }
            "Third Party" -> {
                2
            }
            else -> -1
        }

    }

    private fun parseBillingRadioGroup() : Int {
        val radioButtonId = binding.radioGroupInsuranceCycle.checkedRadioButtonId
        val checkedRadioButton = view?.findViewById<RadioButton>(radioButtonId)

        return when (checkedRadioButton?.text) {
            "Fortnightly" -> {
                0
            }
            "Monthly" -> {
                1
            }
            "Annually" -> {
                2
            }
            else -> -1
        }

    }

    private fun displayValidationError(toastText : String) {
        Toast.makeText(activity, toastText, Toast.LENGTH_LONG).show()
    }

    private fun isValidInsuranceInputs() : Boolean {
        if (binding.editTextInsuranceInsurer.text.toString().isEmpty()) {
            displayValidationError("Please input your insurer")
            return false
        }

        // DatePicker might need validation

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

        val insurancePolicyStartDate: Date = binding.editTextInsurancePolicyStartDate.getDate()
        val insuranceDate: Date = binding.editTextInsuranceDate.getDate()

        if (insuranceDate.time < insurancePolicyStartDate.time) {
            displayValidationError("Your last bill cannot be prior to your insurance start date")
            return false
        }

        // DatePicker does not need validation

        return true
    }
}