package com.example.motologr.ui.logging.fuel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.motologr.databinding.FragmentFuelLoggingBinding

class FuelLoggingFragment : Fragment() {

    private var _binding: FragmentFuelLoggingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fuelLoggingViewModel =
            ViewModelProvider(this).get(FuelLoggingViewModel::class.java)

        _binding = FragmentFuelLoggingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}