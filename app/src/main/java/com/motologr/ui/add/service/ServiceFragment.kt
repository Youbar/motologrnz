package com.motologr.ui.add.service

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
import com.motologr.databinding.FragmentServiceBinding
import com.motologr.data.DataManager
import com.motologr.data.objects.maint.Service
import com.motologr.data.getDate
import com.motologr.data.toCalendar
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Calendar
import java.util.Date

class ServiceFragment : Fragment() {

    private var _binding: FragmentServiceBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val serviceViewModel =
            ViewModelProvider(this).get(ServiceViewModel::class.java)

        _binding = FragmentServiceBinding.inflate(inflater, container, false)

        val root: View = binding.root

        initialiseSaveButton()

        val bundle: Bundle? = arguments
        val logPos: Int? = arguments?.getInt("position");

        if (logPos != null) {
            DataManager.updateTitle(activity, "View Service")
            var service: Service = DataManager.returnActiveVehicle()?.returnLoggableByPosition(logPos)!! as Service
            setInterfaceToReadOnly(service)
        } else {
            DataManager.updateTitle(activity, "Record Service")

            val sharedPref = PreferenceManager.getDefaultSharedPreferences(this.requireContext())

            if (sharedPref != null) {
                val defaultMechanic = sharedPref.getString(getString(R.string.default_mechanic_key), "")
                binding.editTextServiceProvider.setText(defaultMechanic)
            }
        }

        return root
    }

    private fun initialiseSaveButton() {
        // TODO: GET WHICH VEHICLE WE ARE EDITING

        binding.buttonServiceAdd.setOnClickListener {
            addService()
        }
    }

    private fun addService() {
        if (!isValidServiceInputs())
            return

        val vehicleId: Int = DataManager.returnActiveVehicle()?.id!!
        val serviceType: Int = parseServiceTypeRadioGroup()
        val serviceDate: Date = binding.editTextServiceDate.getDate()
        val serviceProvider: String = binding.editTextServiceProvider.text.toString()
        val servicePrice: BigDecimal = binding.editTextServicePrice.text.toString()
            .replace(",","").toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val serviceComment: String = binding.editTextServiceComment.text.toString()

        val service: Service = Service(serviceType, servicePrice, serviceDate, serviceProvider, serviceComment, vehicleId)

        DataManager.returnActiveVehicle()?.logService(service)
        findNavController().navigate(R.id.action_nav_service_to_nav_vehicle_1, null, NavOptions.Builder()
            .setPopUpTo(R.id.nav_vehicle_1, true).build())
    }

    private fun UpdateDatePicker(date: Date) {
        val calendar: Calendar = Calendar.getInstance().toCalendar(date)
        val day =  calendar.get(Calendar.DAY_OF_MONTH)
        val month =  calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        binding.editTextServiceDate.updateDate(year, month, day)
    }

    private fun setInterfaceToReadOnly(service: Service) {
        binding.radioGroupServiceType.check(binding.radioGroupServiceType.getChildAt(service.serviceType).id)
        binding.radioButtonServiceOilChange.isClickable = false
        binding.radioButtonServiceOilChange.isEnabled = false
        binding.radioButtonServiceGeneral.isClickable = false
        binding.radioButtonServiceGeneral.isEnabled = false
        binding.radioButtonServiceFull.isClickable = false
        binding.radioButtonServiceFull.isEnabled = false

        binding.editTextServiceDate.isEnabled = false
        UpdateDatePicker(service.serviceDate)

        binding.editTextServiceProvider.isEnabled = false
        binding.editTextServiceProvider.setText(service.serviceProvider.toString())

        binding.editTextServicePrice.isEnabled = false
        binding.editTextServicePrice.setText(service.price.toString())

        binding.editTextServiceComment.isEnabled = false
        binding.editTextServiceComment.setText(service.comment.toString())

        binding.buttonServiceAdd.isVisible = false
        binding.buttonServiceAdd.isEnabled = false
    }

    private fun parseServiceTypeRadioGroup() : Int {
        val radioButtonId = binding.radioGroupServiceType.checkedRadioButtonId
        val checkedRadioButton = view?.findViewById<RadioButton>(radioButtonId)
        val radioButtonText = checkedRadioButton?.text

        if (radioButtonText == "Oil Change") {
            return 0
        } else if (radioButtonText == "General") {
            return 1
        } else if (radioButtonText == "Full") {
            return 2
        }

        return -1
    }

    private fun displayValidationError(toastText : String) {
        Toast.makeText(activity, toastText, Toast.LENGTH_LONG).show()
    }

    private fun isValidServiceInputs() : Boolean {
        if (parseServiceTypeRadioGroup() == -1) {
            displayValidationError("Please select a service type")
            return false
        }

        // DatePicker does not need validation

        if (binding.editTextServicePrice.text.toString().isEmpty()) {
            displayValidationError("Please input a service price")
            return false
        }

        if (binding.editTextServiceProvider.text.toString().isEmpty()) {
            displayValidationError("Please input a service provider")
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