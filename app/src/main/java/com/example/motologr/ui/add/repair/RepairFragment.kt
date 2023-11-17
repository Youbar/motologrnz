package com.example.motologr.ui.add.repair

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.motologr.R
import com.example.motologr.databinding.FragmentRepairBinding
import com.example.motologr.databinding.FragmentServiceBinding
import com.example.motologr.databinding.FragmentWofRegBinding
import com.example.motologr.ui.add.service.ServiceViewModel

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

        return root
    }

    private fun initialiseSaveButton() {
        // TODO: GET WHICH VEHICLE WE ARE EDITING

        binding.buttonRepairAdd.setOnClickListener {
            addRepair()
            findNavController().navigate(R.id.action_nav_repair_to_nav_vehicle_1)
        }
    }

    private fun addRepair() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}