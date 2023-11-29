package com.motologr.ui.logging.maint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.motologr.R
import com.motologr.databinding.FragmentMaintLoggingBinding
import com.motologr.ui.data.DataManager
import com.motologr.ui.data.Loggable
import com.motologr.ui.data.Repair
import com.motologr.ui.data.Service
import com.motologr.ui.data.Wof
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
        val format: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

        var maintLog = DataManager.ReturnVehicle(0)?.returnLogs()

        if (maintLog != null && maintLog.size > 0) {
            for (i in 0 until maintLog!!.size) {
                var loggable: Loggable = maintLog[i]

                when (loggable.classId) {
                    0 -> { // Repair
                        var repair: Repair = loggable as Repair
                        data.add(
                            MaintLoggingItemsViewModel(
                                R.drawable.ic_menu_arrow_16, "Repair", format.format(repair.repairDate),
                                "$" + repair.price.toString()
                            )
                        )
                    }
                    1 -> { // Service
                        var service: Service = loggable as Service
                        data.add(
                            MaintLoggingItemsViewModel(
                                R.drawable.ic_menu_arrow_16,
                                "Service",
                                format.format(service.serviceDate),
                                "$" + service.price.toString()
                            )
                        )
                    }
                    2 -> { // WOF
                        var wof: Wof = loggable as Wof
                        data.add(
                            MaintLoggingItemsViewModel(
                                R.drawable.ic_menu_arrow_16, "WOF", format.format(wof.wofCompletedDate),
                                "$" + wof.price.toString()
                            )
                        )
                    }
                }
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