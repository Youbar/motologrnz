package com.motologr.ui.add.wof

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.motologr.R
import com.motologr.databinding.FragmentWofBinding
import com.motologr.data.DataManager
import com.motologr.data.objects.vehicle.Vehicle
import com.motologr.data.objects.maint.Wof
import com.motologr.data.getDate
import com.motologr.data.toCalendar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class WofFragment : Fragment() {

    private var _binding: FragmentWofBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val wofViewModel =
            ViewModelProvider(this).get(WofViewModel::class.java)

        _binding = FragmentWofBinding.inflate(inflater, container, false)

        val root: View = binding.root

        setFragmentText()
        initialiseSaveButton()

        val bundle: Bundle? = arguments
        val logPos: Int? = arguments?.getInt("position");

        if (logPos != null) {
            DataManager.updateTitle(activity, "View WOF")
            var wof: Wof = DataManager.ReturnActiveVehicle()?.returnLoggableByPosition(logPos)!! as Wof
            setInterfaceToReadOnly(wof)
        } else {
            DataManager.updateTitle(activity, "Update WOF")
        }

        return root
    }

    private fun UpdateDatePicker(date: Date) {
        val calendar: Calendar = Calendar.getInstance().toCalendar(date)
        val day =  calendar.get(Calendar.DAY_OF_MONTH)
        val month =  calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        binding.editTextWofNextDate.updateDate(year, month, day)
    }

    private fun setInterfaceToReadOnly(wof: Wof) {
        val format: SimpleDateFormat = SimpleDateFormat("dd/MMM/yyyy")
        binding.editTextWofCurrDate.isEnabled = false
        binding.editTextWofCurrDate.setText(format.format(wof.wofCompletedDate))

        binding.editTextWofNextDate.isEnabled = false
        UpdateDatePicker(wof.wofDate)

        binding.editTextWofPrice.isEnabled = false
        binding.editTextWofPrice.setText(wof.price.toString())

        binding.buttonWofSave.isVisible = false
        binding.buttonWofSave.isEnabled = false
    }

    private fun initialiseSaveButton() {
        // TODO: GET WHICH VEHICLE WE ARE EDITING

        binding.buttonWofSave.setOnClickListener {
            updateWof()
        }
    }

    private fun updateWof() {

        if (!isValidWofInputs())
            return

        val vehicle: Vehicle = DataManager.ReturnActiveVehicle() ?: return

        val vehicleId: Int = DataManager.ReturnActiveVehicle()?.id!!
        val oldDate = binding.editTextWofCurrDate.text.toString()
        val newDate = binding.editTextWofNextDate.getDate()
        val price = binding.editTextWofPrice.text.toString()
            .replace(",","").toBigDecimal()

        val format: SimpleDateFormat = SimpleDateFormat("dd/MMM/yyyy")

        val wof = Wof(newDate, format.parse(oldDate), price, vehicleId)
        vehicle.logWof(wof)

        findNavController().navigate(R.id.action_nav_wof_to_nav_vehicle_1)
    }

    private fun displayValidationError(toastText : String) {
        Toast.makeText(activity, toastText, Toast.LENGTH_LONG).show()
    }

    private fun isValidWofInputs() : Boolean {

        // DatePicker does not need validation

        if (binding.editTextWofPrice.text.toString().isEmpty()) {
            displayValidationError("Please input price of WOF")
            return false
        }

        return true
    }

    private fun setFragmentText() {
        val vehicle: Vehicle = DataManager.ReturnActiveVehicle() ?: return

        binding.editTextWofCurrDate.setText(vehicle.returnWofExpiry())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}