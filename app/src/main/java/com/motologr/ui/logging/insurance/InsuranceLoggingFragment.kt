package com.motologr.ui.logging.insurance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.motologr.R
import com.motologr.databinding.FragmentInsuranceLoggingBinding
import com.motologr.ui.data.DataManager
import com.motologr.ui.data.objects.insurance.Insurance
import java.text.SimpleDateFormat

class InsuranceLoggingFragment : Fragment() {

    private var _binding: FragmentInsuranceLoggingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fuelLoggingViewModel =
            ViewModelProvider(this).get(InsuranceLoggingViewModel::class.java)

        _binding = FragmentInsuranceLoggingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        DataManager.updateTitle(activity, "Insurance Policies")

        // getting the recyclerview by its id
        val recyclerview = binding.recyclerViewInsuranceLogging

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(root.context)

        // ArrayList of class ItemsViewModel
        val data = ArrayList<InsuranceLoggingItemsViewModel>()
        val insuranceLog = DataManager.ReturnActiveVehicle()?.insuranceLog?.returnInsuranceLog()

        var insuranceLogSize = (insuranceLog?.size?:0)

        val format: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

        if (insuranceLogSize > 0 && insuranceLog?.size?:0 > 0) {
            for (i in 0 until insuranceLog!!.size) {
                var insurance: Insurance = insuranceLog[i]
                data.add(
                    InsuranceLoggingItemsViewModel(
                        R.drawable.ic_log_insurance_16, format.format(insurance.insurancePolicyStartDate), "$" + insurance.billing.toString(),
                        insurance.returnCycleType())
                )
            }
        }

        // This will pass the ArrayList to our Adapter
        val adapter = InsuranceLoggingAdapter(data)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}