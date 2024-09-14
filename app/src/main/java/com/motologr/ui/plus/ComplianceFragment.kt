package com.motologr.ui.plus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.motologr.R
import com.motologr.data.DataManager
import com.motologr.data.getDate
import com.motologr.databinding.FragmentComplianceBinding

class ComplianceFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private var _binding: FragmentComplianceBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentComplianceBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.textWofLastPrompt.text = "When does your current WOF expire?"
        binding.textRegExpirePrompt.text = "When does your current registration expire?"

        binding.buttonConfirm.setOnClickListener {
            val wofExpiry = binding.editTextWofLastInput.getDate()
            val regExpiry = binding.editTextRegExpireInput.getDate()

            DataManager.returnActiveVehicle()?.submitCompliance(wofExpiry, regExpiry)
            findNavController().navigate(R.id.nav_vehicle_1, null, NavOptions.Builder()
                .setPopUpTo(R.id.nav_vehicle_1, true).build())
        }

        return root
    }
}