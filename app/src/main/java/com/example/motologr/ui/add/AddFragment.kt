package com.example.motologr.ui.add

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.motologr.R
import com.example.motologr.databinding.FragmentAddBinding
import com.example.motologr.ui.data.DataManager
import com.example.motologr.ui.data.Vehicle
import java.text.SimpleDateFormat
import java.util.Date

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

        val buttonFuel: View = binding.buttonFuel
        buttonFuel.setOnClickListener() {
            findNavController().navigate(R.id.action_nav_add_to_nav_fuel)
        }

        val buttonInsurance: View = binding.buttonInsurance
        buttonInsurance.setOnClickListener() {
            findNavController().navigate(R.id.action_nav_add_to_nav_insurance)
        }

        val buttonWof: View = binding.buttonWOF
        buttonWof.setOnClickListener() {
            val bundle: Bundle = Bundle();
            bundle.putString("intent", "wof");
            findNavController().navigate(R.id.action_nav_add_to_nav_wofreg, bundle)
        }

        val buttonReg: View = binding.buttonReg
        buttonReg.setOnClickListener() {
            val bundle: Bundle = Bundle();
            bundle.putString("intent", "reg");
            findNavController().navigate(R.id.action_nav_add_to_nav_wofreg, bundle)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/*
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

        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}*/
