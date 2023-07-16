package com.example.motologr.ui.vehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.motologr.databinding.FragmentVehicleBinding
import com.example.motologr.ui.data.DataManager
import com.example.motologr.ui.data.Vehicle
import java.text.DateFormat
import java.text.DateFormat.getDateInstance
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar

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
        val vehicleViewModel =
            ViewModelProvider(this).get(VehicleViewModel::class.java)

        _binding = FragmentVehicleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val carName: TextView = binding.textCar
        val WOFDue: TextView = binding.textWOFDue
        val RegDue: TextView = binding.textRegDue
        val Odometer: TextView = binding.textOdometer
        val Insurer: TextView = binding.textInsurer
        val InsurerDate: TextView = binding.textInsurerDate
        val ApproxCostsTitle: TextView = binding.textApproxCostsTitle
        val ApproxCosts: TextView = binding.textApproxCosts

        val vehicle : Vehicle? = DataManager.ReturnVehicle(0)

        if (vehicle != null) {
            val format: SimpleDateFormat = SimpleDateFormat("dd/MMM/yyyy")

            var vehicleText = vehicle.modelName + " | " + vehicle.year.toString()

            var expiryWOF: String = format.format(vehicle.expiryWOF)
            expiryWOF = "Next WOF: $expiryWOF"

            var regExpiry: String = format.format(vehicle.regExpiry)
            regExpiry = "Next Reg: $regExpiry"

            var odometer: String = "Last Odometer Reading: " + vehicle.odometer.toString() + " km"

            vehicleViewModel.textVehicle.observe(viewLifecycleOwner) {
                carName.text = vehicleText
            }
            vehicleViewModel.textWOFDue.observe(viewLifecycleOwner) {
                WOFDue.text = expiryWOF
            }
            vehicleViewModel.textRegDue.observe(viewLifecycleOwner) {
                RegDue.text = regExpiry
            }
            vehicleViewModel.textOdometer.observe(viewLifecycleOwner) {
                Odometer.text = odometer
            }
            vehicleViewModel.textInsurer.observe(viewLifecycleOwner) {
                Insurer.text = it
            }
            vehicleViewModel.textInsurerDate.observe(viewLifecycleOwner) {
                InsurerDate.text = it
            }
            vehicleViewModel.textApproxCostsTitle.observe(viewLifecycleOwner) {
                ApproxCostsTitle.text = it
            }
            vehicleViewModel.textApproxCosts.observe(viewLifecycleOwner) {
                ApproxCosts.text = it
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}