package com.example.motologr.ui.add.reg

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.motologr.R
import com.example.motologr.databinding.FragmentRegBinding
import com.example.motologr.ui.data.DataManager
import com.example.motologr.ui.data.Vehicle

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

        setFragmentText()
        initialiseSaveButton()

        return root
    }

    private fun initialiseSaveButton() {
        // TODO: GET WHICH VEHICLE WE ARE EDITING

        binding.buttonRegSave.setOnClickListener {
            updateWofReg()
            findNavController().navigate(R.id.action_nav_reg_to_nav_vehicle_1)
        }
    }

    private fun updateWofReg() {
        val vehicle: Vehicle = DataManager.ReturnVehicle(0) ?: return

        val newDate = binding.editTextRegNextDate.text.toString()

        vehicle.updateRegExpiry(newDate)
    }

    private fun setFragmentText() {
        val vehicle: Vehicle = DataManager.ReturnVehicle(0) ?: return

        binding.editTextRegCurrDate.setText(vehicle.returnRegExpiry())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}