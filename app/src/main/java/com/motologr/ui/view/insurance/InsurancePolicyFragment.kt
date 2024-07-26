package com.motologr.ui.view.insurance

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.motologr.R
import com.motologr.databinding.FragmentInsuranceLoggingBinding
import com.motologr.data.DataManager
import com.motologr.data.objects.insurance.Insurance
import java.text.SimpleDateFormat

class InsurancePolicyFragment : Fragment() {

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
            ViewModelProvider(this).get(InsurancePolicyViewModel::class.java)

        _binding = FragmentInsuranceLoggingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        DataManager.updateTitle(activity, "Insurance Policies")

        // getting the recyclerview by its id
        val recyclerview = binding.recyclerViewInsuranceLogging

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(root.context)

        // ArrayList of class ItemsViewModel
        val data = ArrayList<InsurancePolicyBillsViewModel>()
        val insuranceLog = DataManager.ReturnActiveVehicle()?.insuranceLog?.returnInsuranceLog()

        val insuranceLogSize = (insuranceLog?.size?:0)

        var calendar = Calendar.getInstance()

        val format = SimpleDateFormat("dd/MM/yy")

        if (insuranceLogSize > 0) {
            for (i in 0 until insuranceLog!!.size) {
                var insurance: Insurance = insuranceLog[i]

                val policyStartDt = insurance.insurancePolicyStartDate
                calendar.set(policyStartDt.year + 1900 + 1, policyStartDt.month, policyStartDt.date, 0, 0, 0)
                val policyEndDt = calendar.time

                data.add(
                    InsurancePolicyBillsViewModel(
                        R.drawable.ic_log_insurance_16, format.format(insurance.insurancePolicyStartDate), "to " + format.format(policyEndDt),
                        "$" + insurance.billing.toString(), insurance.returnCycleType())
                )
            }
        }

        // This will pass the ArrayList to our Adapter
        val adapter = InsurancePolicyBillsAdapter(data)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}