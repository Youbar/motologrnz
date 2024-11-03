package com.motologr.ui.insurance.insurance_bill_manage

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
import com.motologr.data.DataHelper
import com.motologr.data.DataManager
import com.motologr.data.objects.insurance.Insurance
import com.motologr.data.objects.insurance.InsuranceBill
import com.motologr.databinding.FragmentInsuranceBillManageBinding
import com.motologr.ui.compose.CurrencyInput
import com.motologr.ui.compose.DatePickerModal
import com.motologr.ui.compose.EditDeleteFABs
import com.motologr.ui.compose.SaveFAB
import com.motologr.ui.compose.WarningDialog
import com.motologr.ui.theme.AppTheme

class InsuranceBillManageFragment : Fragment() {
    private var _binding: FragmentInsuranceBillManageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun getInsurance() : Insurance? {
        val argumentInsuranceId = arguments?.getInt("insuranceId") ?: -1

        return DataManager.returnActiveVehicle()?.insuranceLog?.returnInsuranceById(argumentInsuranceId)
    }

    private fun getInsuranceBill(insurance: Insurance) : InsuranceBill? {
        val argumentInsuranceBillId = arguments?.getInt("insuranceBillId") ?: -1

        if (argumentInsuranceBillId == -1)
            return null

        val insuranceBill = insurance.insuranceBillLog?.returnInsuranceBillById(argumentInsuranceBillId)

        return insuranceBill
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val activeVehicle = DataManager.returnActiveVehicle()!!

        val insuranceBillManageViewModel = ViewModelProvider(this)[InsuranceBillManageViewModel::class.java]

        _binding = FragmentInsuranceBillManageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val insurance = getInsurance() ?:
            return root

        insuranceBillManageViewModel.displayToastMessage = { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
                .show()
        }
        insuranceBillManageViewModel.initViewModel(insurance.id)
        insuranceBillManageViewModel.navigateToVehicle = {
            findNavController().popBackStack()
        }

        val insuranceBill = getInsuranceBill(insurance)
        if (insuranceBill != null) {
            insuranceBillManageViewModel.setViewModelToReadOnly(insuranceBill)
        } else {
            insuranceBillManageViewModel.insuranceBillDate.value = DataHelper.getCurrentDateString()
        }

        val composeView = root.findViewById<ComposeView>(R.id.compose_view_insurance_bill_manage)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    InsuranceBillInterface(insuranceBillManageViewModel)
                    if (insuranceBillManageViewModel.isExistingData && insuranceBillManageViewModel.isReadOnly.value)
                        EditDeleteFABs(insuranceBillManageViewModel.onDeleteClick, insuranceBillManageViewModel.onEditClick)
                    else if (insuranceBillManageViewModel.isExistingData && !insuranceBillManageViewModel.isReadOnly.value)
                        SaveFAB(insuranceBillManageViewModel.onSaveClick)
                    if (insuranceBillManageViewModel.isDisplayDeleteDialog.value) {
                        WarningDialog(insuranceBillManageViewModel.onDismissClick, insuranceBillManageViewModel.onConfirmClick, "Delete Record", "Are you sure you want to delete this record? The deletion is irreversible.")
                    }
                }
            }
        }

        return root
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsuranceBillInterface(viewModel: InsuranceBillManageViewModel) {
    LazyColumn {
        item {
            OutlinedCard(modifier = Modifier
                .padding(16.dp)
                .border(1.dp, MaterialTheme.colorScheme.secondary, shape)
                .height(IntrinsicSize.Min)) {
                Column(modifier = Modifier
                    .padding(16.dp, 8.dp, 16.dp, 8.dp)
                    .height(IntrinsicSize.Min)){
                    Text(viewModel.insuranceBillCardTitle, fontSize = 24.sp,
                        modifier = Modifier
                            .padding(PaddingValues(0.dp, 0.dp))
                            .fillMaxWidth(),
                        lineHeight = 1.em,
                        textAlign = TextAlign.Center)
                    DatePickerModal(viewModel.insuranceBillDate, "Payment Date", true, viewModel.isReadOnly.value, modifier = Modifier.padding(top = 8.dp))
                    CurrencyInput(viewModel.insuranceBillPrice, "Payment Amount", isReadOnly = viewModel.isReadOnly.value, modifier = Modifier.padding(top = 8.dp))

                    if (!viewModel.isReadOnly.value && !viewModel.isExistingData) {
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier
                            .padding(0.dp, 32.dp, 0.dp, 0.dp)
                            .fillMaxWidth()) {
                            Button(onClick = viewModel.onRecordClick, contentPadding = PaddingValues(8.dp)) {
                                Text("Add", fontSize = 20.sp, textAlign = TextAlign.Center)
                            }
                        }
                    }

                }
            }
        }
    }
}