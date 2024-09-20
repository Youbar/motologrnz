package com.motologr.ui.add.reg

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
import com.motologr.databinding.FragmentRegBinding
import com.motologr.data.DataManager
import com.motologr.data.objects.reg.Reg
import com.motologr.data.objects.vehicle.Vehicle
import com.motologr.data.getDate
import com.motologr.data.toCalendar
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

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

        DataManager.updateTitle(activity, "Update Registration")

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

                val monthsExtended = getMonthsExtended()
                setRegistrationEstimate(monthsExtended)
                setDateEstimate(monthsExtended)
            }
        }

        binding.radioGroupRegTypeCol2.setOnCheckedChangeListener { radioGroup, i ->
            if (i != -1 && !isChangingRadioGroup) {
                isChangingRadioGroup = true
                binding.radioGroupRegTypeCol1.clearCheck()
                isChangingRadioGroup = false

                val monthsExtended = getMonthsExtended()
                setRegistrationEstimate(monthsExtended)
                setDateEstimate(monthsExtended)
            }
        }
    }

    private fun initialiseSaveButton() {
        binding.buttonRegSave.setOnClickListener {
            updateReg()
        }
    }

    private fun updateDatePicker(date: Date) {
        val calendar: Calendar = Calendar.getInstance().toCalendar(date)
        val day =  calendar.get(Calendar.DAY_OF_MONTH)
        val month =  calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        binding.editTextRegNextDate.updateDate(year, month, day)
    }

    private fun setDateEstimate(months : Int) {
        val vehicle: Vehicle = DataManager.returnActiveVehicle() ?: return

        val format: SimpleDateFormat = SimpleDateFormat("dd/MMM/yyyy")
        val date: Date = format.parse(vehicle.returnRegExpiry())

        val calendar: Calendar = Calendar.getInstance().toCalendar(date)
        calendar.add(Calendar.MONTH, months)
        val day =  calendar.get(Calendar.DAY_OF_MONTH)
        val month =  calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        binding.editTextRegNextDate.updateDate(year, month, day)
    }

    private fun setRegistrationEstimate(months : Int) {
        val df = DecimalFormat("0.00")
        df.roundingMode = RoundingMode.HALF_UP
        val registrationEstimate = Reg.calculateRegistration(months)
        binding.editTextRegPrice.setText(df.format(registrationEstimate))
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

        val vehicle: Vehicle = DataManager.returnActiveVehicle() ?: return

        val vehicleId: Int = DataManager.returnActiveVehicle()?.id!!
        val regExpiryDate = binding.editTextRegCurrDate.text.toString()
        val newRegExpiryDate = binding.editTextRegNextDate.getDate()
        val monthsExtended = getMonthsExtended()
        val price = binding.editTextRegPrice.text.toString()
            .replace(",","").toBigDecimal().setScale(2, RoundingMode.HALF_UP)

        val format: SimpleDateFormat = SimpleDateFormat("dd/MMM/yyyy")

        val reg = Reg(newRegExpiryDate, format.parse(regExpiryDate), monthsExtended, price, vehicleId)

        vehicle.logReg(reg)
        findNavController().navigate(R.id.action_nav_reg_to_nav_vehicle_1, null, NavOptions.Builder()
            .setPopUpTo(R.id.nav_vehicle_1, true).build())
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
        val vehicle: Vehicle = DataManager.returnActiveVehicle() ?: return

        binding.editTextRegCurrDate.setText(vehicle.returnRegExpiry())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}