@file:OptIn(ExperimentalMaterial3Api::class)

package com.motologr.ui.add.historical.reg

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
import androidx.navigation.fragment.findNavController
import com.motologr.R
import com.motologr.data.DataManager
import com.motologr.data.objects.reg.Reg
import com.motologr.data.objects.vehicle.Vehicle
import com.motologr.databinding.FragmentHistoricalRegBinding
import com.motologr.ui.compose.DatePickerModal
import com.motologr.ui.compose.CurrencyInput
import com.motologr.ui.compose.EditDeleteFABs
import com.motologr.ui.compose.SaveFAB
import com.motologr.ui.compose.SliderWithUnits
import com.motologr.ui.compose.SliderWithUnitsForRegistration
import com.motologr.ui.compose.WarningDialog
import com.motologr.ui.theme.AppTheme

class HistoricalRegFragment : Fragment() {
    private var _binding: FragmentHistoricalRegBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun initViewModel(historicalRegViewModel: HistoricalRegViewModel,
                              activeVehicle : Vehicle) {
        val loggableId: Int = arguments?.getInt("loggableId", -1) ?: -1
        if (loggableId != -1) {
            DataManager.updateTitle(activity, "View Reg")
            val reg: Reg = DataManager.returnActiveVehicle()?.returnLoggableById(loggableId)!! as Reg
            historicalRegViewModel.setViewModelToReadOnly(reg)
        } else {
            DataManager.updateTitle(activity, "Update Reg")
            val isHistorical = arguments?.getBoolean("isHistorical") ?: false
            historicalRegViewModel.initViewModel(activeVehicle, isHistorical)
        }

        historicalRegViewModel.displayToastMessage = { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
                .show()
        }

        historicalRegViewModel.navigateToVehicle = {
            findNavController().navigate(R.id.nav_vehicle_1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val activeVehicle = DataManager.returnActiveVehicle()!!

        val historicalRegViewModel =
            ViewModelProvider(this)[HistoricalRegViewModel::class.java]
        initViewModel(historicalRegViewModel, activeVehicle)

        _binding = FragmentHistoricalRegBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val composeView = root.findViewById<ComposeView>(R.id.compose_view_historical_reg)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    LazyColumn {
                        item {
                            HistoricalRegCard(historicalRegViewModel)
                            if (historicalRegViewModel.isExistingData && historicalRegViewModel.isReadOnly.value)
                                EditDeleteFABs(historicalRegViewModel.onDeleteClick, historicalRegViewModel.onEditClick)
                            else if (historicalRegViewModel.isExistingData && !historicalRegViewModel.isReadOnly.value)
                                SaveFAB(historicalRegViewModel.onSaveClick)
                            if (historicalRegViewModel.isDisplayDeleteDialog.value) {
                                WarningDialog(historicalRegViewModel.onDismissClick, historicalRegViewModel.onConfirmClick, "Delete Record",
                                    "Are you sure you want to delete this record? The deletion is irreversible.")
                            }
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
fun HistoricalRegCard(viewModel : HistoricalRegViewModel) {
    OutlinedCard(modifier = Modifier
        .padding(16.dp)
        .border(1.dp, MaterialTheme.colorScheme.secondary, shape)) {
        Column(modifier = Modifier
            .padding(16.dp, 8.dp, 16.dp, 8.dp)
            .height(IntrinsicSize.Min)){
            Text(viewModel.regTitle, fontSize = 24.sp,
                modifier = Modifier
                    .padding(PaddingValues(0.dp, 0.dp))
                    .fillMaxWidth(),
                lineHeight = 1.em,
                textAlign = TextAlign.Center)
            if (viewModel.isHistorical.value) {
                DatePickerModal(viewModel.historicalRegDate, "Purchase Date",
                    hasDefaultValue = viewModel.isExistingData,
                    isReadOnly = viewModel.isReadOnly.value,
                    modifier = Modifier.padding(top = 8.dp))
            } else {
                DatePickerModal(viewModel.oldRegExpiryDate, "Current Expiry Date",
                    hasDefaultValue = true,
                    isReadOnly = true,
                    modifier = Modifier.padding(top = 8.dp))
            }
            HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(PaddingValues(16.dp, 16.dp, 16.dp, 16.dp)))
            if (viewModel.isHistorical.value) {
                SliderWithUnits(viewModel.regPrice, viewModel.sliderPosition, 11, "month(s)",
                    Reg.calculateRegistrationLambda, viewModel.isReadOnly.value)
            } else {
                SliderWithUnitsForRegistration(viewModel.regPrice, viewModel.sliderPosition, 11, "month(s)",
                    Reg.calculateRegistrationLambda, viewModel.oldRegExpiryDate, viewModel.newRegExpiryDate,
                    Reg.calculateRegistrationDateLambda, viewModel.isReadOnly.value)
            }
            if (!viewModel.isHistorical.value) {
                DatePickerModal(viewModel.newRegExpiryDate, "New Expiry Date",
                    hasDefaultValue = true,
                    isReadOnly = viewModel.isReadOnly.value)
            }
            CurrencyInput(viewModel.regPrice, "Purchase Price", isReadOnly = viewModel.isReadOnly.value, modifier = Modifier.padding(top = 8.dp))

            if (!viewModel.isReadOnly.value && !viewModel.isExistingData) {
                var buttonText = HistoricalRegViewModel.UPDATE_REG_BUTTON
                if (viewModel.isHistorical.value)
                    buttonText = HistoricalRegViewModel.HISTORICAL_REG_BUTTON
                Row(
                    horizontalArrangement = Arrangement.End, modifier = Modifier
                        .padding(0.dp, 32.dp, 0.dp, 0.dp)
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = viewModel.onRecordClick,
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        Text(buttonText, fontSize = 3.em, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}