package com.motologr.ui.add

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.motologr.R
import com.motologr.databinding.FragmentAddBinding
import com.motologr.data.DataManager

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val addViewModel =
            ViewModelProvider(this).get(AddViewModel::class.java)

        _binding = FragmentAddBinding.inflate(inflater, container, false)

        val root: View = binding.root

        DataManager.updateTitle(activity, "Record/Update Details")

        val buttonFuel: View = binding.buttonFuel
        buttonFuel.setOnClickListener() {
            findNavController().navigate(R.id.action_nav_add_to_nav_fuel)
        }

        val buttonInsurance: View = binding.buttonInsurance
        buttonInsurance.setOnClickListener() {
            findNavController().navigate(R.id.action_nav_add_to_nav_insurance)
        }

        val buttonRepair: View = binding.buttonRepair
        buttonRepair.setOnClickListener() {
            findNavController().navigate(R.id.action_nav_add_to_nav_repair)
        }

        val buttonService: View = binding.buttonService
        buttonService.setOnClickListener() {
            findNavController().navigate(R.id.action_nav_add_to_nav_service)
        }

        val buttonWof: View = binding.buttonWOF
        buttonWof.setOnClickListener() {
            findNavController().navigate(R.id.action_nav_add_to_nav_wof)
        }

        val buttonReg: View = binding.buttonReg
        buttonReg.setOnClickListener() {
            findNavController().navigate(R.id.action_nav_add_to_nav_reg)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}