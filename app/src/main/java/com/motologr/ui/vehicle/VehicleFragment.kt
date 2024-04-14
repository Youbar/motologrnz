package com.motologr.ui.vehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.motologr.R
import com.motologr.databinding.FragmentVehicleBinding
import com.motologr.ui.data.DataManager
import com.motologr.ui.data.Vehicle
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat

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

        val vehicle : Vehicle? = DataManager.ReturnActiveVehicle()

        if (vehicle != null) {
            val format: SimpleDateFormat = SimpleDateFormat("dd/MMM/yyyy")

            var vehicleText = vehicle.brandName + " " + vehicle.modelName + " | " + vehicle.year.toString()

            var expiryWOF: String = format.format(vehicle.expiryWOF)
            expiryWOF = "Next WOF: $expiryWOF"

            var regExpiry: String = format.format(vehicle.regExpiry)
            regExpiry = "Next Reg: $regExpiry"

            var odometer: String = "Last Odometer Reading: " + vehicle.getLatestOdometerReading().toString() + " km"

            var hasInsurance: Boolean = false

            if (vehicle.isInsuranceInitialised()) {
                hasInsurance = vehicle.insurance.returnIsActive()
            }

            var insurer: String
            var insurerDate: String

            if (hasInsurance) {
                insurer = vehicle.insurance.insurer
                insurer = "You are with $insurer insurance"

                val df = DecimalFormat("0.00")
                df.roundingMode = RoundingMode.CEILING

                insurerDate = "and your next bill of $${df.format(vehicle.insurance.billing)} is due "
                insurerDate += vehicle.insurance.getNextBillingDateString()
            } else {
                insurer = "You do not have a registered insurer"
                insurerDate = ""
            }



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
                Insurer.text = insurer
            }
            vehicleViewModel.textInsurerDate.observe(viewLifecycleOwner) {
                InsurerDate.text = insurerDate
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fab: View = binding.fab

        fab.setOnClickListener { _ ->
            findNavController().navigate(R.id.action_nav_vehicle_1_to_nav_add)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}