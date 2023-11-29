package com.motologr.ui.add.repair

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
import com.motologr.databinding.FragmentRepairBinding
import com.motologr.ui.data.DataManager
import com.motologr.ui.data.Fuel
import com.motologr.ui.data.Repair
import com.motologr.ui.data.Wof
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

        val bundle: Bundle? = arguments
        val logPos: Int? = arguments?.getInt("position");

        if (logPos != null) {
            var repair: Repair = DataManager.ReturnVehicle(0)?.returnLoggableByPosition(logPos)!! as Repair
            setInterfaceToReadOnly(repair)
        }

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

    private fun setInterfaceToReadOnly(repair: Repair) {
        binding.radioGroupRepairType.check(binding.radioGroupRepairType.getChildAt(repair.repairType).id)
        binding.radioButtonRepairMinor.isClickable = false
        binding.radioButtonRepairMinor.isEnabled = false
        binding.radioButtonRepairMajor.isClickable = false
        binding.radioButtonRepairMajor.isEnabled = false
        binding.radioButtonRepairCritical.isClickable = false
        binding.radioButtonRepairCritical.isEnabled = false

        val format: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        binding.editTextRepairDate.isEnabled = false
        binding.editTextRepairDate.setText(format.format(repair.repairDate))

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}