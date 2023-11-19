package com.example.motologr.ui.logging.fuel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.motologr.R
import com.example.motologr.databinding.FragmentFuelLoggingBinding
import com.example.motologr.ui.data.DataManager
import com.example.motologr.ui.data.Fuel
import com.example.motologr.ui.data.Repair
import com.example.motologr.ui.data.Service
import com.example.motologr.ui.logging.maint.MaintLoggingAdapter
import com.example.motologr.ui.logging.maint.MaintLoggingItemsViewModel
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

        // getting the recyclerview by its id
        val recyclerview = binding.recyclerViewFuelLogging

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(root.context)

        // ArrayList of class ItemsViewModel
        val data = ArrayList<FuelLoggingItemsViewModel>()
        val fuelLog = DataManager.ReturnVehicle(0)?.fuelLog?.returnFuelLog()

        var fuelLogSize = (fuelLog?.size?:0)

        val format: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

        if (fuelLogSize > 0 && fuelLog?.size?:0 > 0) {
            for (i in 0 until fuelLog!!.size) {
                var fuel: Fuel = fuelLog[i]
                data.add(
                    FuelLoggingItemsViewModel(
                        R.drawable.ic_menu_arrow_16, format.format(fuel.purchaseDate), "$" + fuel.price.toString(),
                        fuel.litres.toString() + " L")
                )
            }
        }

        // This will pass the ArrayList to our Adapter
        val adapter = FuelLoggingAdapter(data)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}