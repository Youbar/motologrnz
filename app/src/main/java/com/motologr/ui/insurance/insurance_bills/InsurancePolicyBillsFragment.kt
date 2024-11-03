package com.motologr.ui.insurance.insurance_bills

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.motologr.R
import com.motologr.data.DataHelper
import com.motologr.data.DataManager
import com.motologr.data.objects.insurance.Insurance
import com.motologr.databinding.FragmentInsurancePolicyBillsBinding
import com.motologr.ui.theme.AppTheme
import java.math.BigDecimal

class InsurancePolicyBillsFragment : Fragment() {
    private var _binding: FragmentInsurancePolicyBillsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val navigateToInsuranceBill = { insuranceBillId : Int, insuranceId : Int ->
        val bundle = Bundle()
        bundle.putInt("insuranceBillId", insuranceBillId)
        bundle.putInt("insuranceId", insuranceId)

        findNavController().navigate(R.id.nav_insurance_bill_manage, bundle)
    }

    private fun getInsurancePolicy() : Insurance? {
        val argumentInsuranceId = arguments?.getInt("insuranceId") ?: -1

        if (argumentInsuranceId == -1)
            return null

        val insurance = DataManager.returnActiveVehicle()?.insuranceLog?.returnInsuranceById(argumentInsuranceId)

        return insurance
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val activeVehicle = DataManager.returnActiveVehicle()!!

        val viewModel = ViewModelProvider(this)[InsurancePolicyBillsViewModel::class.java]

        _binding = FragmentInsurancePolicyBillsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val insuranceBillLogs = getInsurancePolicy()?.insuranceBillLog?.returnInsuranceBillLog()
            ?: return root
        insuranceBillLogs.sortByDescending { x -> x.billingDate.time }

        val composeView = root.findViewById<ComposeView>(R.id.compose_view_insurance_policy_bills)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text("Insurance Policy Payments",
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp,
                            fontWeight =  FontWeight.Bold)
                        LazyColumn {
                            items(insuranceBillLogs) { insuranceBill ->
                                val sortableDate = DataHelper.formatNumericalDateFormat(insuranceBill.sortableDate)
                                InsuranceBillLoggingCard(insuranceBill.id, insuranceBill.insuranceId, sortableDate, insuranceBill.unitPrice,
                                    navigateToInsuranceBill)
                            }
                        }
                    }
                }
            }
        }

        return root
    }
}

@Preview
@Composable
fun InsuranceBillLoggingCard(insuranceBillId : Int = 0,
                             insuranceId : Int = 0,
                             complianceDate : String = "14/05/2024",
                             price : BigDecimal = 68f.toBigDecimal(),
                             onClick : (Int, Int) -> Unit = { _, _ -> }) {
    OutlinedCard(
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .height(IntrinsicSize.Min),
        onClick = { onClick(insuranceBillId, insuranceId) }
    ) {
        val cardFontSize = 20.sp
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly) {
            Text(complianceDate, fontSize = cardFontSize)
            Text("$${DataHelper.roundToTwoDecimalPlaces(price)}", fontSize = cardFontSize)
        }
    }
}