package com.motologr.ui.add.wof

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.motologr.R
import com.motologr.databinding.FragmentWofBinding
import com.motologr.ui.data.DataManager
import com.motologr.ui.data.Fuel
import com.motologr.ui.data.Vehicle
import com.motologr.ui.data.Wof
import java.text.SimpleDateFormat

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
            var wof: Wof = DataManager.ReturnVehicle(0)?.wofLog?.returnWof(logPos)!!
            setInterfaceToReadOnly(wof)
        }

        return root
    }

    private fun setInterfaceToReadOnly(wof: Wof) {
        val format: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        binding.editTextWofCurrDate.isEnabled = false
        binding.editTextWofCurrDate.setText(format.format(wof.wofCompletedDate))

        binding.editTextWofNextDate.isEnabled = false
        binding.editTextWofNextDate.setText(format.format(wof.wofDate))

        binding.editTextWofPrice.isEnabled = false
        binding.editTextWofPrice.setText(wof.price.toString())

        binding.buttonWofSave.isVisible = false
        binding.buttonWofSave.isEnabled = false
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

        val oldDate = binding.editTextWofCurrDate.text.toString()
        val newDate = binding.editTextWofNextDate.text.toString()
        val price = binding.editTextWofPrice.text.toString().toDouble()

        val format: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

        val wof: Wof = Wof(format.parse(newDate), format.parse(oldDate), price)

        vehicle.logWof(wof)

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