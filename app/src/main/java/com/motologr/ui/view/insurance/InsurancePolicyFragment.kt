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
import com.motologr.data.DataManager
import com.motologr.data.objects.insurance.InsuranceBill
import com.motologr.databinding.FragmentInsurancePolicyBinding
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class InsurancePolicyFragment : Fragment() {

    private var _binding: FragmentInsurancePolicyBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val insurancePolicyViewModel =
            ViewModelProvider(this).get(InsurancePolicyViewModel::class.java)

        _binding = FragmentInsurancePolicyBinding.inflate(inflater, container, false)
        val root: View = binding.root

        DataManager.updateTitle(activity, "Insurance Policies")

        // getting the recyclerview by its id
        val recyclerview = binding.insuranceBillRecyclerView

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(root.context)

        // ArrayList of class ItemsViewModel
        val data = ArrayList<InsurancePolicyBillsViewModel>()

        val bundle = arguments
        if (bundle != null) {
            val policyIndex = bundle.getInt("position")

            val insurance = DataManager.ReturnActiveVehicle()?.insuranceLog?.returnInsurance(policyIndex)

            val format = SimpleDateFormat("dd/MM/yy")

            val df = DecimalFormat("0.00")
            df.roundingMode = RoundingMode.HALF_UP

            if (insurance != null) {
                binding.policyInsurer.text = "${insurance.insurer}"
                binding.policyStartDate.text = "Start Date - ${format.format(insurance.insurancePolicyStartDate)}"
                binding.policyEndDate.text = "End Date - ${format.format(insurance.endDt)}"
                binding.policyPricingAndCycle.text = "Pricing - $${df.format(insurance.billing)} ${insurance.returnCycleType()}"
                binding.policyCoverage.text = "Coverage - ${insurance.returnCoverageType()}"
            }

            val insuranceBillLog = insurance?.insuranceBillLog?.returnInsuranceBillLog()
            val insuranceBillLogSize = insuranceBillLog?.size?:0

            val localDate = LocalDate.now()

            if (insuranceBillLogSize > 0) {
                insuranceBillLog!!.sortByDescending { x -> x.billingDate.time }

                for (i in 0 until insuranceBillLogSize) {
                    val insuranceBill: InsuranceBill = insuranceBillLog!![i]
                    val billDtAsLocalDate = insuranceBill.billingDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

                    val billPaid = localDate.isAfter(billDtAsLocalDate)
                            || localDate.isEqual(billDtAsLocalDate)

                    val df = DecimalFormat("0.00")
                    df.roundingMode = RoundingMode.HALF_UP

                    data.add(
                        InsurancePolicyBillsViewModel(
                            R.drawable.ic_log_bill_16, format.format(insuranceBill.billingDate),
                            "$" + df.format(insurance.billing), billPaid)
                    )
                }
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