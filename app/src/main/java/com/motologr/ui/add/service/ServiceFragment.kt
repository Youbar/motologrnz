package com.motologr.ui.add.service

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.navigation.fragment.findNavController
import com.motologr.R
import com.motologr.databinding.FragmentServiceBinding
import com.motologr.ui.data.DataManager
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