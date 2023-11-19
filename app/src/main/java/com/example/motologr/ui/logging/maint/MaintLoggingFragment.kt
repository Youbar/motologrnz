package com.example.motologr.ui.logging.maint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.motologr.R
import com.example.motologr.databinding.FragmentMaintLoggingBinding
import com.example.motologr.ui.data.DataManager
import com.example.motologr.ui.data.Repair
import com.example.motologr.ui.data.Service
import java.text.SimpleDateFormat

class MaintLoggingFragment : Fragment() {

    private var _binding: FragmentMaintLoggingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val maintLoggingViewModel =
            ViewModelProvider(this).get(MaintLoggingViewModel::class.java)

        _binding = FragmentMaintLoggingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // getting the recyclerview by its id
        val recyclerview = binding.recyclerViewMaintLogging

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(root.context)

        // ArrayList of class ItemsViewModel
        val data = ArrayList<MaintLoggingItemsViewModel>()
        val repairLog = DataManager.ReturnVehicle(0)?.repairLog?.returnRepairLog()
        val serviceLog = DataManager.ReturnVehicle(0)?.serviceLog?.returnServiceLog()

        var maintLogSize = (repairLog?.size?:0) + (serviceLog?.size?:0)

        val format: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

        if (maintLogSize > 0 && repairLog?.size?:0 > 0) {
            for (i in 0 until repairLog!!.size) {
                var repair: Repair = repairLog[i]
                data.add(
                    MaintLoggingItemsViewModel(R.drawable.ic_menu_arrow_16, "Repair", format.format(repair.repairDate),
                    "$" + repair.price.toString())
                )
            }
        }

        if (maintLogSize > 0 && serviceLog?.size?:0 > 0) {
            for (i in 0 until serviceLog!!.size) {
                var service: Service = serviceLog[i]
                data.add(
                    MaintLoggingItemsViewModel(R.drawable.ic_menu_arrow_16, "Service", format.format(service.serviceDate),
                    "$" + service.price.toString())
                )
            }
        }

        // This will pass the ArrayList to our Adapter
        val adapter = MaintLoggingAdapter(data)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}