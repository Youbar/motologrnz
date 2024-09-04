package com.motologr.ui.add.repair

import androidx.lifecycle.ViewModelProvider
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
import androidx.preference.PreferenceManager
import com.motologr.R
import com.motologr.databinding.FragmentRepairBinding
import com.motologr.data.DataManager
import com.motologr.data.objects.maint.Repair
import com.motologr.data.getDate
import com.motologr.data.toCalendar
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Calendar
import java.util.Date

class RepairFragment : Fragment() {

    private var _binding: FragmentRepairBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val repairViewModel =
            ViewModelProvider(this).get(RepairViewModel::class.java)

        _binding = FragmentRepairBinding.inflate(inflater, container, false)

        val root: View = binding.root

        initialiseSaveButton()

        val bundle: Bundle? = arguments
        val logPos: Int? = arguments?.getInt("position");

        if (logPos != null) {
            DataManager.updateTitle(activity, "View Repair")
            var repair: Repair = DataManager.returnActiveVehicle()?.returnLoggableByPosition(logPos)!! as Repair
            setInterfaceToReadOnly(repair)
        }else {
            DataManager.updateTitle(activity, "Record Repair")

            val sharedPref = PreferenceManager.getDefaultSharedPreferences(this.requireContext())

            if (sharedPref != null) {
                val defaultMechanic = sharedPref.getString(getString(R.string.default_mechanic_key), "")
                binding.editTextRepairProvider.setText(defaultMechanic)
            }
        }

        return root
    }

    private fun initialiseSaveButton() {
        // TODO: GET WHICH VEHICLE WE ARE EDITING

        binding.buttonRepairAdd.setOnClickListener {
            addRepair()
        }
    }

    private fun addRepair() {
        if (!isValidRepairInputs())
            return

        val vehicleId: Int = DataManager.returnActiveVehicle()?.id!!
        val repairType: Int = parseRepairTypeRadioGroup()
        val repairDate: Date = binding.editTextRepairDate.getDate()
        val repairProvider: String = binding.editTextRepairProvider.text.toString()
        val repairPrice: BigDecimal = binding.editTextRepairPrice.text.toString()
            .replace(",","").toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
        val repairComment: String = binding.editTextRepairComment.text.toString()

        val repair: Repair = Repair(repairType, repairPrice, repairDate, repairProvider, repairComment, vehicleId)

        DataManager.returnActiveVehicle()?.logRepair(repair)
        findNavController().navigate(R.id.action_nav_repair_to_nav_vehicle_1, null, NavOptions.Builder()
            .setPopUpTo(R.id.nav_vehicle_1, true).build())
    }

    private fun UpdateDatePicker(date: Date) {
        val calendar: Calendar = Calendar.getInstance().toCalendar(date)
        val day =  calendar.get(Calendar.DAY_OF_MONTH)
        val month =  calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        binding.editTextRepairDate.updateDate(year, month, day)
    }

    private fun setInterfaceToReadOnly(repair: Repair) {
        binding.radioGroupRepairType.check(binding.radioGroupRepairType.getChildAt(repair.repairType).id)
        binding.radioButtonRepairMinor.isClickable = false
        binding.radioButtonRepairMinor.isEnabled = false
        binding.radioButtonRepairMajor.isClickable = false
        binding.radioButtonRepairMajor.isEnabled = false
        binding.radioButtonRepairCritical.isClickable = false
        binding.radioButtonRepairCritical.isEnabled = false

        binding.editTextRepairDate.isEnabled = false
        UpdateDatePicker(repair.repairDate)

        binding.editTextRepairProvider.isEnabled = false
        binding.editTextRepairProvider.setText(repair.repairProvider.toString())

        binding.editTextRepairPrice.isEnabled = false
        binding.editTextRepairPrice.setText(repair.price.toString())

        binding.editTextRepairComment.isEnabled = false
        binding.editTextRepairComment.setText(repair.comment.toString())

        binding.buttonRepairAdd.isVisible = false
        binding.buttonRepairAdd.isEnabled = false
    }

    private fun parseRepairTypeRadioGroup() : Int {
        val radioButtonId = binding.radioGroupRepairType.checkedRadioButtonId
        val checkedRadioButton = view?.findViewById<RadioButton>(radioButtonId)
        val radioButtonText = checkedRadioButton?.text

        if (radioButtonText == "Minor") {
            return 0
        } else if (radioButtonText == "Major") {
            return 1
        } else if (radioButtonText == "Critical") {
            return 2
        }

        return -1
    }

    private fun displayValidationError(toastText : String) {
        Toast.makeText(activity, toastText, Toast.LENGTH_LONG).show()
    }

    private fun isValidRepairInputs() : Boolean {
        if (parseRepairTypeRadioGroup() == -1) {
            displayValidationError("Please select a repair type")
            return false
        }

        // DatePicker does not need validation

        if (binding.editTextRepairPrice.text.toString().isEmpty()) {
            displayValidationError("Please input a repair price")
            return false
        }

        if (binding.editTextRepairProvider.text.toString().isEmpty()) {
            displayValidationError("Please input a repair provider")
            return false
        }

        // Comment input optional

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}