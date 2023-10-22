package com.example.motologr.ui.add.wofreg

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.motologr.R
import com.example.motologr.databinding.FragmentWofRegBinding
import com.example.motologr.ui.data.DataManager
import com.example.motologr.ui.data.Vehicle

class WofRegFragment : Fragment() {

    private var _binding: FragmentWofRegBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val insuranceViewModel =
            ViewModelProvider(this).get(WofRegViewModel::class.java)

        _binding = FragmentWofRegBinding.inflate(inflater, container, false)

        val root: View = binding.root

        getBundleIntent(arguments)
        setFragmentText()
        initialiseSaveButton()

        return root
    }

    private fun initialiseSaveButton() {
        // TODO: GET WHICH VEHICLE WE ARE EDITING

        binding.buttonWofRegSave.setOnClickListener {
            updateWofReg()
            findNavController().navigate(R.id.action_nav_wofreg_to_nav_vehicle_1)
        }
    }

    private lateinit var bundleTarget : String

    private fun getBundleIntent(bundle: Bundle?) {
        if (bundle == null) {
            bundleTarget = ""
        } else if (bundle.getString("intent") == "wof") {
            bundleTarget = "wof"
        } else if (bundle.getString("intent") == "reg") {
            bundleTarget = "reg"
        }
    }

    private fun updateWofReg() {
        val vehicle: Vehicle = DataManager.ReturnVehicle(0) ?: return

        val newDate = binding.editTextWofRegNextDate.text.toString()

        if (bundleTarget == "wof") {
            vehicle.updateWofExpiry(newDate)
        } else if (bundleTarget == "reg") {
            vehicle.updateRegExpiry(newDate)
        }
    }

    private fun setFragmentText() {
        val vehicle: Vehicle = DataManager.ReturnVehicle(0) ?: return

        if (bundleTarget == "wof") {
            setFragmentToWof(vehicle.returnWofExpiry())
        } else if (bundleTarget == "reg") {
            setFragmentToReg(vehicle.returnRegExpiry())
        }
    }

    private fun setFragmentToWof(currentWof: String) {
        binding.textWofRegTitle.text = "Update WOF"
        binding.textWofRegCurrDatePrompt.text = "Your current WOF is due:"
        binding.editTextWofRegCurrDate.setText(currentWof)
        binding.textWofRegNextDatePrompt.text = "Your new WOF is due:"
    }

    private fun setFragmentToReg(currentReg: String) {
        binding.textWofRegTitle.text = "Update Registration"
        binding.textWofRegCurrDatePrompt.text = "Your current reg expires:"
        binding.editTextWofRegCurrDate.setText(currentReg)
        binding.textWofRegNextDatePrompt.text = "Your new reg expires:"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}