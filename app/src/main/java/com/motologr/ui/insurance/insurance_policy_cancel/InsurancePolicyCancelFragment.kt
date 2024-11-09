package com.motologr.ui.insurance.insurance_policy_cancel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.motologr.R
import com.motologr.data.DataManager
import com.motologr.data.objects.insurance.Insurance
import com.motologr.databinding.FragmentInsurancePolicyCancelBinding
import com.motologr.ui.compose.CurrencyInput
import com.motologr.ui.compose.DatePickerModal
import com.motologr.ui.insurance.insurance_bill_manage.InsuranceBillManageViewModel
import com.motologr.ui.theme.AppTheme

class InsurancePolicyCancelFragment : Fragment() {
    private var _binding: FragmentInsurancePolicyCancelBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun getInsurance() : Insurance? {
        val argumentInsuranceId = arguments?.getInt("insuranceId") ?: -1

        return DataManager.returnActiveVehicle()?.insuranceLog?.returnInsuranceById(argumentInsuranceId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val insurancePolicyCancelViewModel = ViewModelProvider(this)[InsurancePolicyCancelViewModel::class.java]
        insurancePolicyCancelViewModel.displayToastMessage = { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
                .show()
        }

        _binding = FragmentInsurancePolicyCancelBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val activeVehicle = DataManager.returnActiveVehicle() ?:
            return root
        val insurance = getInsurance() ?:
            return root

        insurancePolicyCancelViewModel.navigateBack = {
            val bundle = Bundle()
            bundle.putInt("insuranceId", insurance.id)

            findNavController().navigate(R.id.nav_insurance_policy, bundle, NavOptions.Builder()
                .setPopUpTo(R.id.nav_insurance_policy, true).build())
        }

        insurancePolicyCancelViewModel.initViewModel(activeVehicle, insurance)

        val composeView = root.findViewById<ComposeView>(R.id.compose_view_insurance_policy_cancel)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    LazyColumn {
                        item {
                            InsurancePolicyCancelInterface(insurancePolicyCancelViewModel)
                        }
                    }
                }
            }
        }

        return root
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsurancePolicyCancelInterface(viewModel: InsurancePolicyCancelViewModel) {
    OutlinedCard(modifier = Modifier
        .padding(16.dp)
        .border(1.dp, MaterialTheme.colorScheme.secondary, shape)
        .height(IntrinsicSize.Min)) {
        Column(modifier = Modifier
            .padding(16.dp, 8.dp, 16.dp, 8.dp)
            .height(IntrinsicSize.Min)){
            Text(viewModel.insurancePolicyCancelCardTitle, fontSize = 24.sp,
                modifier = Modifier
                    .padding(PaddingValues(0.dp, 0.dp))
                    .fillMaxWidth(),
                lineHeight = 1.em,
                textAlign = TextAlign.Center)
            DatePickerModal(viewModel.cancellationDate, "Cancellation Date", true, modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))
            HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(16.dp, 8.dp))
            Text(viewModel.insurancePolicyCancelCardText, fontSize = 14.sp, modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp))

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier
                .padding(0.dp, 32.dp, 0.dp, 0.dp)
                .fillMaxWidth()) {
                Button(onClick = viewModel.onCancelPolicyClick, contentPadding = PaddingValues(8.dp)) {
                    Text("Cancel Policy", fontSize = 20.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}