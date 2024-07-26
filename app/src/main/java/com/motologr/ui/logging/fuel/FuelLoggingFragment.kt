package com.motologr.ui.logging.fuel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.motologr.MainActivity
import com.motologr.R
import com.motologr.databinding.FragmentFuelLoggingBinding
import com.motologr.data.DataManager
import com.motologr.data.objects.fuel.Fuel
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat

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

        DataManager.updateTitle(activity, "Fuel Logs")

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val root: View = binding.root

        // getting the recyclerview by its id
        val recyclerview = binding.recyclerViewFuelLogging

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(root.context)

        // ArrayList of class ItemsViewModel
        val data = ArrayList<FuelLoggingItemsViewModel>()
        val fuelLog = DataManager.ReturnActiveVehicle()?.returnFuelLog()

        val fuelLogSize = (fuelLog?.size?:0)

        val format = SimpleDateFormat("dd/MM/yy")

        val df = DecimalFormat("0.00")
        df.roundingMode = RoundingMode.CEILING

        if (fuelLogSize > 0 && fuelLog?.size?:0 > 0) {
            for (i in 0 until fuelLog!!.size) {
                var fuel: Fuel = fuelLog[i]
                data.add(
                    FuelLoggingItemsViewModel(
                        R.drawable.ic_log_fuel_16, format.format(fuel.purchaseDate), "$" + df.format(fuel.price),
                        fuel.litres.toString() + " L")
                )
            }
        }

        // This will pass the ArrayList to our Adapter
        val adapter = FuelLoggingAdapter(data)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}