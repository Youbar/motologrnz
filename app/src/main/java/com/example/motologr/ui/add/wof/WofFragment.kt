package com.example.motologr.ui.add.wof

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.motologr.R
import com.example.motologr.databinding.FragmentWofBinding
import com.example.motologr.ui.data.DataManager
import com.example.motologr.ui.data.Vehicle

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

        return root
    }

    private fun initialiseSaveButton() {
        // TODO: GET WHICH VEHICLE WE ARE EDITING

        binding.buttonWofSave.setOnClickListener {
            updateWofReg()
            findNavController().navigate(R.id.action_nav_wof_to_nav_vehicle_1)
        }
    }

    private fun updateWofReg() {
        val vehicle: Vehicle = DataManager.ReturnVehicle(0) ?: return

        val newDate = binding.editTextWofNextDate.text.toString()

        vehicle.updateWofExpiry(newDate)
    }

    private fun setFragmentText() {
        val vehicle: Vehicle = DataManager.ReturnVehicle(0) ?: return

        binding.editTextWofCurrDate.setText(vehicle.returnWofExpiry())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}