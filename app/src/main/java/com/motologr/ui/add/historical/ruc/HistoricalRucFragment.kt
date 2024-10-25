package com.motologr.ui.add.historical.ruc

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
import androidx.compose.runtime.MutableState
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
import com.motologr.data.objects.ruc.Ruc
import com.motologr.data.objects.vehicle.Vehicle
import com.motologr.databinding.FragmentHistoricalRucBinding
import com.motologr.ui.add.historical.reg.HistoricalRegViewModel
import com.motologr.ui.compose.CurrencyInput
import com.motologr.ui.compose.DatePickerModal
import com.motologr.ui.compose.EditDeleteFABs
import com.motologr.ui.compose.NumberInput
import com.motologr.ui.compose.SaveFAB
import com.motologr.ui.compose.SliderWithUnits
import com.motologr.ui.compose.SliderWithUnitsForRoadUserCharges
import com.motologr.ui.compose.WarningDialog
import com.motologr.ui.theme.AppTheme

class HistoricalRucFragment : Fragment() {
    private var _binding: FragmentHistoricalRucBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun initViewModel(historicalRucViewModel: HistoricalRucViewModel,
                              activeVehicle : Vehicle) {
        val loggableId: Int = arguments?.getInt("loggableId", -1) ?: -1
        if (loggableId != -1) {
            DataManager.updateTitle(activity, "View RUCs")
            val ruc: Ruc = DataManager.returnActiveVehicle()?.returnLoggableById(loggableId)!! as Ruc
            historicalRucViewModel.setViewModelToReadOnly(ruc)
        } else {
            DataManager.updateTitle(activity, "Update RUCs")
            val isHistorical = arguments?.getBoolean("isHistorical") ?: false
            historicalRucViewModel.initViewModel(activeVehicle, isHistorical)
        }

        historicalRucViewModel.displayToastMessage = { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
                .show()
        }

        historicalRucViewModel.navigateToVehicle = {
            findNavController().navigate(R.id.nav_vehicle_1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val activeVehicle = DataManager.returnActiveVehicle()!!

        val historicalRucViewModel =
            ViewModelProvider(this)[HistoricalRucViewModel::class.java]
        initViewModel(historicalRucViewModel, activeVehicle)

        _binding = FragmentHistoricalRucBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val composeView = root.findViewById<ComposeView>(R.id.compose_view_historical_ruc)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    LazyColumn {
                        item {
                            HistoricalRucCard(historicalRucViewModel)
                            if (historicalRucViewModel.isExistingData && historicalRucViewModel.isReadOnly.value)
                                EditDeleteFABs(historicalRucViewModel.onDeleteClick, historicalRucViewModel.onEditClick)
                            else if (historicalRucViewModel.isExistingData && !historicalRucViewModel.isReadOnly.value)
                                SaveFAB(historicalRucViewModel.onSaveClick)
                            if (historicalRucViewModel.isDisplayDeleteDialog.value) {
                                WarningDialog(historicalRucViewModel.onDismissClick, historicalRucViewModel.onConfirmClick, "Delete Record",
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
fun HistoricalRucCard(viewModel: HistoricalRucViewModel) {
    OutlinedCard(modifier = Modifier
        .padding(16.dp)
        .border(1.dp, MaterialTheme.colorScheme.secondary, shape)) {
        Column(modifier = Modifier
            .padding(16.dp, 8.dp, 16.dp, 8.dp)
            .height(IntrinsicSize.Min)){
            Text(viewModel.rucTitle, fontSize = 24.sp,
                modifier = Modifier
                    .padding(PaddingValues(0.dp, 0.dp))
                    .fillMaxWidth(),
                lineHeight = 1.em,
                textAlign = TextAlign.Center)
            DatePickerModal(viewModel.purchaseDate, "Purchase Date", true, isReadOnly = viewModel.isReadOnly.value)
            if (!viewModel.isHistorical.value)
                NumberInput(viewModel.oldUnitsHeld, "RUCs Held", modifier = Modifier.padding(top = 8.dp), isReadOnly = true)
            HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(PaddingValues(16.dp, 16.dp, 16.dp, 16.dp)))
            if (viewModel.isHistorical.value)
                SliderWithUnits(viewModel.rucPrice, viewModel.sliderPosition, 19, "unit(s)", Ruc.calculateRucPriceLambda, isReadOnly = viewModel.isReadOnly.value)
            else
                SliderWithUnitsForRoadUserCharges(viewModel.rucPrice, viewModel.sliderPosition, 19, "unit(s)", Ruc.calculateRucPriceLambda,
                    viewModel.oldUnitsHeld, viewModel.newUnitsHeld, Ruc.calculateRucUnitsLambda, isReadOnly = viewModel.isReadOnly.value)
            if (!viewModel.isHistorical.value)
                NumberInput(viewModel.newUnitsHeld, "New RUCs Held", modifier = Modifier.padding(top = 8.dp), isReadOnly = viewModel.isReadOnly.value)
            CurrencyInput(viewModel.rucPrice, "Purchase Price", modifier = Modifier.padding(top = 8.dp), isReadOnly = viewModel.isReadOnly.value)

            if (!viewModel.isReadOnly.value && !viewModel.isExistingData) {
                var buttonText = HistoricalRucViewModel.UPDATE_RUC_BUTTON
                if (viewModel.isHistorical.value)
                    buttonText = HistoricalRucViewModel.HISTORICAL_RUC_BUTTON
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier
                    .padding(0.dp, 32.dp, 0.dp, 0.dp)
                    .fillMaxWidth()) {
                    Button(onClick = viewModel.onRecordClick, contentPadding = PaddingValues(8.dp)) {
                        Text(buttonText, fontSize = 3.em, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}