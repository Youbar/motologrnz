package com.motologr.ui.view.insurance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CardDefaults.shape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.motologr.R
import com.motologr.data.DataManager
import com.motologr.data.objects.insurance.InsuranceBill
import com.motologr.databinding.FragmentInsurancePolicyBinding
import com.motologr.ui.add.AddFragmentCard
import com.motologr.ui.compose.WarningDialog
import com.motologr.ui.theme.AppTheme
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
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
        val insurancePolicyViewModel = ViewModelProvider(this)[InsurancePolicyViewModel::class.java]

        _binding = FragmentInsurancePolicyBinding.inflate(inflater, container, false)
        val root: View = binding.root

        DataManager.updateTitle(activity, "Insurance Policies")

        insurancePolicyViewModel.initInsurancePolicyViewModel(arguments)
        insurancePolicyViewModel.displayToastMessage = { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
                .show()
        }
        insurancePolicyViewModel.navigateToVehicle = {
            findNavController().navigate(R.id.nav_vehicle_1, null, NavOptions.Builder()
                .setPopUpTo(R.id.nav_vehicle_1, true).build())
        }

        val composeView = root.findViewById<ComposeView>(R.id.compose_view_insurance_policy)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    LazyColumn {
                        item {
                            InsurancePolicyInterface(insurancePolicyViewModel)
                        }
                        item {
                            AddFragmentCard("Manage Bills", "View, update, delete, or add to your existing bills for this insurance policy")
                        }
                        item {
                            AddFragmentCard("Cancel Policy", "Cancel this insurance policy. All bills after the cancellation date will be deleted and a new bill will be generated for you to specify arrears or refunds.")
                        }
                        item {
                            AddFragmentCard("Delete Policy", "Delete this insurance policy. This will remove all records of this policy from your vehicle. It is the recommended option if your insurance policy generated incorrectly.", insurancePolicyViewModel.onDeleteClick)
                        }
                    }
                    if (insurancePolicyViewModel.isDisplayDeleteDialog.value) {
                        WarningDialog(insurancePolicyViewModel.onDismissClick, insurancePolicyViewModel.onConfirmClick, "Delete Record", "Are you sure you want to delete this record? The deletion is irreversible.")
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

@Composable
fun InsurancePolicyInterface(viewModel : InsurancePolicyViewModel) {
    OutlinedCard(modifier = Modifier
        .padding(16.dp, 16.dp, 16.dp, 8.dp)
        .height(IntrinsicSize.Min)) {
            Text(viewModel.insurerName.value,
                fontSize = 24.sp,
                modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(16.dp, 8.dp))
            Text(viewModel.startDate.value, fontSize = 20.sp, modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp))
            Text(viewModel.endDate.value, fontSize = 20.sp, modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp))
            Text(viewModel.pricing.value, fontSize = 20.sp, modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp))
            Text(viewModel.coverage.value, fontSize = 20.sp, modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp))
    }
}