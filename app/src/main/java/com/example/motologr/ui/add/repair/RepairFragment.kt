package com.example.motologr.ui.add.repair

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.navigation.fragment.findNavController
import com.example.motologr.R
import com.example.motologr.databinding.FragmentRepairBinding
import com.example.motologr.databinding.FragmentServiceBinding
import com.example.motologr.databinding.FragmentWofRegBinding
import com.example.motologr.ui.add.service.ServiceViewModel
import com.example.motologr.ui.data.DataManager
import com.example.motologr.ui.data.Repair
import com.example.motologr.ui.data.Service
import java.text.SimpleDateFormat
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
        val format: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

        val repairType: Int = parseRepairTypeRadioGroup()
        val repairDate: Date = format.parse(binding.editTextRepairDate.text.toString())
        val repairProvider: String = binding.editTextRepairProvider.text.toString()
        val repairPrice: Double = binding.editTextRepairPrice.text.toString().toDouble()
        val repairComment: String = binding.editTextRepairComment.text.toString()

        val repair: Repair = Repair(repairType, repairPrice, repairDate, repairProvider, repairComment)

        DataManager.ReturnVehicle(0)?.logRepair(repair)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}