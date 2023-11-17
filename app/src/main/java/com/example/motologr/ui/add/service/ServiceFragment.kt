package com.example.motologr.ui.add.service

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.motologr.R
import com.example.motologr.databinding.FragmentServiceBinding
import com.example.motologr.databinding.FragmentWofRegBinding
import com.example.motologr.ui.add.wofreg.WofRegViewModel

class ServiceFragment : Fragment() {

    private var _binding: FragmentServiceBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val serviceViewModel =
            ViewModelProvider(this).get(ServiceViewModel::class.java)

        _binding = FragmentServiceBinding.inflate(inflater, container, false)

        val root: View = binding.root

        initialiseSaveButton()

        return root
    }

    private fun initialiseSaveButton() {
        // TODO: GET WHICH VEHICLE WE ARE EDITING

        binding.buttonServiceAdd.setOnClickListener {
            addService()
            findNavController().navigate(R.id.action_nav_service_to_nav_vehicle_1)
        }
    }

    private fun addService() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}