package com.motologr.ui.add.insurance

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults.shape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.motologr.R
import com.motologr.databinding.FragmentInsuranceBinding
import com.motologr.data.DataManager
import com.motologr.data.EnumConstants
import com.motologr.data.objects.insurance.Insurance
import com.motologr.data.getDate
import com.motologr.data.toCalendar
import com.motologr.ui.compose.CurrencyInput
import com.motologr.ui.compose.DatePickerModal
import com.motologr.ui.compose.StringInput
import com.motologr.ui.theme.AppTheme
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Calendar
import java.util.Date

class InsuranceFragment : Fragment() {

    private var _binding: FragmentInsuranceBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val insuranceViewModel =
            ViewModelProvider(this)[InsuranceViewModel::class.java]

        _binding = FragmentInsuranceBinding.inflate(inflater, container, false)
        val root: View = binding.root

        DataManager.updateTitle(activity, "Add Insurance Policy")

        insuranceViewModel.initInsuranceViewModel()
        insuranceViewModel.displayToastMessage = { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
                .show()
        }
        insuranceViewModel.navigateToVehicle = {
            findNavController().navigate(R.id.action_nav_insurance_to_nav_vehicle_1, null, NavOptions.Builder()
                .setPopUpTo(R.id.nav_vehicle_1, true).build())
        }

        val composeView = root.findViewById<ComposeView>(R.id.compose_view_insurance)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    LazyColumn {
                        item {
                            InsuranceInterface(insuranceViewModel)
                        }
                    }
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsuranceInterface(viewModel: InsuranceViewModel) {
    OutlinedCard(modifier = Modifier
        .padding(16.dp)
        .border(1.dp, MaterialTheme.colorScheme.secondary, shape)
        .height(IntrinsicSize.Min)) {
        Column(modifier = Modifier
            .padding(16.dp, 8.dp, 16.dp, 8.dp)
            .height(IntrinsicSize.Min)){
            Text("Add Insurance Policy", fontSize = 24.sp,
                modifier = Modifier
                    .padding(PaddingValues(0.dp, 0.dp))
                    .fillMaxWidth(),
                lineHeight = 1.em,
                textAlign = TextAlign.Center)
            DatePickerModal(viewModel.startDate, "Start Date", true, modifier = Modifier.padding(top = 8.dp))
            DatePickerModal(viewModel.lastBillingDate, "Last Billing Date", true, modifier = Modifier.padding(top = 8.dp))
            StringInput(viewModel.insurerName, "Insurer Name", modifier = Modifier.padding(top = 8.dp))
            RowOfCoverageTypes(viewModel.isThirdChecked, viewModel.isThirdPlusChecked, viewModel.isComprehensiveChecked, viewModel.onCoverageChecked)
            CurrencyInput(viewModel.lastBillingAmount, "Last Billing Amount")
            RowOfBillingCycleTypes(viewModel.isFortnightlyChecked, viewModel.isMonthlyChecked, viewModel.isAnnuallyChecked, viewModel.onBillingCycleChecked)

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier
                .padding(0.dp, 32.dp, 0.dp, 0.dp)
                .fillMaxWidth()) {
                Button(onClick = viewModel.onRecordClick, contentPadding = PaddingValues(8.dp)) {
                    Text("Record", fontSize = 20.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun RowOfCoverageTypes(isThirdChecked : MutableState<Boolean>, isThirdPlusChecked : MutableState<Boolean>,
                       isComprehensiveChecked : MutableState<Boolean>,
                       onBoxChecked: (Int) -> Unit) {
    Row {
        Column {
            CoverageTypeCheckbox(isThirdChecked, "3rd", onBoxChecked, EnumConstants.InsuranceCoverage.Third.ordinal)
            CoverageTypeCheckbox(isThirdPlusChecked, "3rd+", onBoxChecked, EnumConstants.InsuranceCoverage.ThirdPlus.ordinal)
        }
        Column (horizontalAlignment = Alignment.End) {
            CoverageTypeCheckbox(isComprehensiveChecked, "Comprehensive", onBoxChecked, EnumConstants.InsuranceCoverage.Comprehensive.ordinal)
        }
    }
}

@Composable
fun CoverageTypeCheckbox(checkboxBoolean : MutableState<Boolean>, checkboxText : String, onBoxChecked : (Int) -> Unit, coverageType : Int) {
    var boxChecked by remember { checkboxBoolean }
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            checkboxText
        )
        Checkbox(
            checked = boxChecked,
            onCheckedChange = {
                boxChecked = it
                onBoxChecked(coverageType)
            }
        )
    }
}

@Composable
fun RowOfBillingCycleTypes(isFortnightlyChecked : MutableState<Boolean>, isMonthlyChecked : MutableState<Boolean>,
                           isAnnuallyChecked : MutableState<Boolean>,
                           onBoxChecked: (Int) -> Unit) {
    Row {
        Column {
            BillingCycleTypeCheckbox(isFortnightlyChecked, "Fortnightly", onBoxChecked, EnumConstants.InsuranceBillingCycle.Fortnightly.ordinal)
            BillingCycleTypeCheckbox(isMonthlyChecked, "Monthly", onBoxChecked, EnumConstants.InsuranceBillingCycle.Monthly.ordinal)
        }
        Column (horizontalAlignment = Alignment.End) {
            BillingCycleTypeCheckbox(isAnnuallyChecked, "Annually", onBoxChecked, EnumConstants.InsuranceBillingCycle.Annually.ordinal)
        }
    }
}

@Composable
fun BillingCycleTypeCheckbox(checkboxBoolean : MutableState<Boolean>, checkboxText : String, onBoxChecked : (Int) -> Unit, billingCycleType : Int) {
    var boxChecked by remember { checkboxBoolean }
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            checkboxText
        )
        Checkbox(
            checked = boxChecked,
            onCheckedChange = {
                boxChecked = it
                onBoxChecked(billingCycleType)
            }
        )
    }
}