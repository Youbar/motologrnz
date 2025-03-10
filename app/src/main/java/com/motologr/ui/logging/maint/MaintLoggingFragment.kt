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
import com.motologr.data.DataManager
import com.motologr.data.logging.Loggable
import com.motologr.data.objects.maint.Repair
import com.motologr.data.objects.maint.Service
import com.motologr.data.objects.maint.Wof
import java.math.RoundingMode
import java.text.DecimalFormat
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

        DataManager.updateTitle(activity, "Mechanical Logs")

        // getting the recyclerview by its id
        val recyclerview = binding.recyclerViewMaintLogging

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(root.context)

        // ArrayList of class ItemsViewModel
        val data = ArrayList<MaintLoggingItemsViewModel>()
        val format = SimpleDateFormat("dd/MM/yy")

        val maintLog = DataManager.returnActiveVehicle()?.returnMechanicalLogs()

        val df = DecimalFormat("0.00")
        df.roundingMode = RoundingMode.CEILING

        if (maintLog != null && maintLog.size > 0) {
            maintLog.sortByDescending { x -> x.sortableDate.time }

            for (i in 0 until maintLog!!.size) {
                var loggable: Loggable = maintLog[i]

                when (loggable.classId) {
                    0 -> { // Repair
                        var repair: Repair = loggable as Repair
                        data.add(
                            MaintLoggingItemsViewModel(
                                R.drawable.ic_log_car_repair_16, "Repair", format.format(repair.repairDate),
                                "$" + df.format(repair.price)
                            )
                        )
                    }
                    1 -> { // Service
                        var service: Service = loggable as Service
                        data.add(
                            MaintLoggingItemsViewModel(
                                R.drawable.ic_log_car_service_16,
                                "Service",
                                format.format(service.serviceDate),
                                "$" + df.format(service.price)
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