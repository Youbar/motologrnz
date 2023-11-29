package com.motologr.ui.add.service

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.motologr.R
import com.motologr.databinding.FragmentServiceBinding
import com.motologr.ui.data.DataManager
import com.motologr.ui.data.Repair
import com.motologr.ui.data.Service
import java.text.SimpleDateFormat
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
            var service: Service = DataManager.ReturnVehicle(0)?.returnLoggableByPosition(logPos)!! as Service
            setInterfaceToReadOnly(service)
        }

        return root
    }

    private fun initialiseSaveButton() {
        // TODO: GET WHICH VEHICLE WE ARE EDITING

        binding.buttonServiceAdd.setOnClickListener {
            addService()
            findNavController().navigate(R.id.action_nav_service_to_nav_vehicle_1)
        }
    }

    private fun addService() {
        val format: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

        val serviceType: Int = parseServiceTypeRadioGroup()
        val serviceDate: Date = format.parse(binding.editTextServiceDate.text.toString())
        val serviceProvider: String = binding.editTextServiceProvider.text.toString()
        val servicePrice: Double = binding.editTextServicePrice.text.toString().toDouble()
        val serviceComment: String = binding.editTextServiceComment.text.toString()

        val service: Service = Service(serviceType, servicePrice, serviceDate, serviceProvider, serviceComment)

        DataManager.ReturnVehicle(0)?.logService(service)
    }

    private fun setInterfaceToReadOnly(service: Service) {
        binding.radioGroupServiceType.check(binding.radioGroupServiceType.getChildAt(service.serviceType).id)
        binding.radioButtonServiceOilChange.isClickable = false
        binding.radioButtonServiceOilChange.isEnabled = false
        binding.radioButtonServiceGeneral.isClickable = false
        binding.radioButtonServiceGeneral.isEnabled = false
        binding.radioButtonServiceFull.isClickable = false
        binding.radioButtonServiceFull.isEnabled = false

        val format: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        binding.editTextServiceDate.isEnabled = false
        binding.editTextServiceDate.setText(format.format(service.serviceDate))

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}