package com.example.motologr.ui.vehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.motologr.databinding.FragmentVehicleBinding

class VehicleFragment : Fragment() {

    private var _binding: FragmentVehicleBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val carViewModel =
            ViewModelProvider(this).get(VehicleViewModel::class.java)

        _binding = FragmentVehicleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val carName: TextView = binding.textCar
        val WOFDone: TextView = binding.textWOFDone
        val WOFDue: TextView = binding.textWOFDue
        val RegDone: TextView = binding.textRegDone
        val RegDue: TextView = binding.textRegDue
        val Odometer: TextView = binding.textOdometer
        val Insurer: TextView = binding.textInsurer
        val InsurerDate: TextView = binding.textInsurerDate
        val ApproxCostsTitle: TextView = binding.textApproxCostsTitle
        val ApproxCosts: TextView = binding.textApproxCosts
        val CarImage: ImageView = binding.imageCar

        carViewModel.textCar.observe(viewLifecycleOwner) {
            carName.text = it
        }

        carViewModel.textWOFDone.observe(viewLifecycleOwner) {
            WOFDone.text = it
        }

        carViewModel.textWOFDue.observe(viewLifecycleOwner) {
            WOFDue.text = it
        }

        carViewModel.textRegDue.observe(viewLifecycleOwner) {
            RegDue.text = it
        }

        carViewModel.textOdometer.observe(viewLifecycleOwner) {
            Odometer.text = it
        }

        carViewModel.textInsurer.observe(viewLifecycleOwner) {
            Insurer.text = it
        }

        carViewModel.textInsurerDate.observe(viewLifecycleOwner) {
            InsurerDate.text = it
        }

        carViewModel.textApproxCostsTitle.observe(viewLifecycleOwner) {
            ApproxCostsTitle.text = it
        }

        carViewModel.textApproxCosts.observe(viewLifecycleOwner) {
            ApproxCosts.text = it
        }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}