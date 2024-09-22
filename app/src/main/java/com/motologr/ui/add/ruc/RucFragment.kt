package com.motologr.ui.add.ruc

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.motologr.R
import com.motologr.data.DataManager
import com.motologr.data.getDate
import com.motologr.data.objects.ruc.Ruc
import com.motologr.data.objects.vehicle.Vehicle
import com.motologr.databinding.FragmentComplianceBinding
import com.motologr.databinding.FragmentRucBinding
import java.math.BigDecimal

class RucFragment : Fragment() {

    private var _binding: FragmentRucBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRucBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val activeVehicle = DataManager.returnActiveVehicle()!!
        val latestRucUnits = activeVehicle.returnLatestRucUnits()

        binding.editTextRucCurrMiles.setText(latestRucUnits)

        binding.editTextRucUnits.doAfterTextChanged {
            val numberOfUnitsInput = binding.editTextRucUnits.text.toString()

            if (numberOfUnitsInput.isNotEmpty() && !numberOfUnitsInput.contains('.') && !numberOfUnitsInput.contains(',')) {
                binding.editTextRucPrice.setText(Ruc.calculateRucPrice(binding.editTextRucUnits.text.toString().toInt()).toString())
                binding.editTextRucNewMiles.setText(calculateNewRucMiles(latestRucUnits))
            }
        }

        binding.buttonRucSave.setOnClickListener {
            convertFragmentToRucObject(activeVehicle)
        }

        return root
    }

    private fun convertFragmentToRucObject(activeVehicle : Vehicle) {
        if (!isValidRucInputs()) {
            return
        }

        val transactionDate = binding.editTextRucTransDate.getDate()
        val unitsPurchased = binding.editTextRucUnits.text.toString().toInt()
        val unitsHeldAfterTransaction = binding.editTextRucNewMiles.text.toString().toInt()
        val price = binding.editTextRucPrice.text.toString().toBigDecimal()

        val ruc = Ruc(transactionDate, unitsPurchased, unitsHeldAfterTransaction, price, activeVehicle.id)
        activeVehicle.logRuc(ruc)
        findNavController().navigate(R.id.nav_vehicle_1, null, NavOptions.Builder()
            .setPopUpTo(R.id.nav_vehicle_1, true).build())
    }

    private fun calculateNewRucMiles(latestRucUnits : String) : String {
        val numberOfUnitsInput = binding.editTextRucUnits.text.toString()

        return if (numberOfUnitsInput.isEmpty() || numberOfUnitsInput.contains('.') || numberOfUnitsInput.contains(','))
            latestRucUnits
        else {
            (latestRucUnits.toInt() + numberOfUnitsInput.toInt() * 1000)
                .toString()
        }
    }

    private fun isValidRucInputs() : Boolean {
        val unitsPurchased = binding.editTextRucUnits.text.toString()

        if (unitsPurchased.isEmpty()) {
            displayValidationError("Please input number of units purchased")
            return false
        }

        if (unitsPurchased.contains('.') || unitsPurchased.contains(',')) {
            displayValidationError("Units purchased must be whole numbers")
            return false
        }

        if (unitsPurchased.toBigDecimal().toInt() <= 0) {
            displayValidationError("You cannot purchase 0 or less units")
            return false
        }

        if (unitsPurchased.toBigDecimal().toInt() > 999) {
            displayValidationError("It is highly unlikely you have purchased more than 1000 units")
            return false
        }

        if (unitsPurchased.contains('.') || unitsPurchased.contains(',')) {
            displayValidationError("You cannot purchase partial units")
            return false
        }

        val priceOfUnits = binding.editTextRucPrice.text.toString()

        if (priceOfUnits.isEmpty()) {
            displayValidationError("Please input price of units purchased")
            return false
        }

        if (priceOfUnits.toBigDecimal().toInt() < 0) {
            displayValidationError("You cannot have a negative value for price")
            return false
        }

        return true
    }

    private fun displayValidationError(toastText : String) {
        Toast.makeText(activity, toastText, Toast.LENGTH_LONG).show()
    }
}