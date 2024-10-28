package com.motologr.ui.add.historical.wof

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
import androidx.preference.PreferenceManager
import com.motologr.R
import com.motologr.data.DataManager
import com.motologr.data.objects.maint.Wof
import com.motologr.data.objects.vehicle.Vehicle
import com.motologr.databinding.FragmentHistoricalWofBinding
import com.motologr.ui.compose.CurrencyInput
import com.motologr.ui.compose.DatePickerModal
import com.motologr.ui.compose.EditDeleteFABs
import com.motologr.ui.compose.SaveFAB
import com.motologr.ui.compose.StringInput
import com.motologr.ui.compose.WarningDialog
import com.motologr.ui.theme.AppTheme

class HistoricalWofFragment : Fragment() {
    private var _binding: FragmentHistoricalWofBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun initViewModel(historicalWofViewModel: HistoricalWofViewModel,
                              activeVehicle : Vehicle) {
        val loggableId: Int = arguments?.getInt("loggableId", -1) ?: -1
        if (loggableId != -1) {
            DataManager.updateTitle(activity, "View WOF")
            val wof: Wof = DataManager.returnActiveVehicle()?.returnLoggableById(loggableId)!! as Wof
            historicalWofViewModel.setViewModelToReadOnly(wof)
        } else {
            DataManager.updateTitle(activity, "Update WOF")
            val isHistorical = arguments?.getBoolean("isHistorical") ?: false
            historicalWofViewModel.initViewModel(activeVehicle, isHistorical)
        }

        historicalWofViewModel.displayToastMessage = { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
                .show()
        }

        historicalWofViewModel.navigateToVehicle = {
            findNavController().navigate(R.id.nav_vehicle_1)
        }

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
        if (sharedPref != null && loggableId == -1) {
            val defaultMechanic = sharedPref.getString(getString(R.string.default_mechanic_key), "")
            historicalWofViewModel.wofProvider.value = defaultMechanic ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val activeVehicle = DataManager.returnActiveVehicle()!!

        val historicalWofViewModel =
            ViewModelProvider(this)[HistoricalWofViewModel::class.java]
        initViewModel(historicalWofViewModel, activeVehicle)

        _binding = FragmentHistoricalWofBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val composeView = root.findViewById<ComposeView>(R.id.compose_view_historical_wof)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    LazyColumn {
                        item {
                            HistoricalWofCard(historicalWofViewModel)
                            if (historicalWofViewModel.isExistingData && historicalWofViewModel.isReadOnly.value)
                                EditDeleteFABs(historicalWofViewModel.onDeleteClick, historicalWofViewModel.onEditClick)
                            else if (historicalWofViewModel.isExistingData && !historicalWofViewModel.isReadOnly.value)
                                SaveFAB(historicalWofViewModel.onSaveClick)
                            if (historicalWofViewModel.isDisplayDeleteDialog.value) {
                                WarningDialog(historicalWofViewModel.onDismissClick, historicalWofViewModel.onConfirmClick, "Delete Record", "Are you sure you want to delete this record? The deletion is irreversible.")
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
fun HistoricalWofCard(viewModel : HistoricalWofViewModel) {
    OutlinedCard(modifier = Modifier
        .padding(16.dp)
        .border(1.dp, MaterialTheme.colorScheme.secondary, shape)) {
        Column(modifier = Modifier
            .padding(16.dp, 8.dp, 16.dp, 8.dp)
            .height(IntrinsicSize.Min)){
            Text(viewModel.wofTitle, fontSize = 24.sp,
                modifier = Modifier
                    .padding(PaddingValues(0.dp, 0.dp))
                    .fillMaxWidth(),
                lineHeight = 1.em,
                textAlign = TextAlign.Center)
            if (viewModel.isHistorical.value) {
                DatePickerModal(viewModel.historicalWofDate, "WOF Date",
                    hasDefaultValue = viewModel.isExistingData,
                    isReadOnly = viewModel.isReadOnly.value,
                    modifier = Modifier.padding(top = 8.dp))
            } else {
                DatePickerModal(viewModel.oldWofExpiryDate, "Current WOF Expiry",
                    hasDefaultValue = true,
                    isReadOnly = true,
                    modifier = Modifier.padding(top = 8.dp)
                )
                DatePickerModal(viewModel.newWofExpiryDate, "New WOF Expiry",
                    hasDefaultValue = true,
                    isReadOnly = viewModel.isReadOnly.value,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(PaddingValues(16.dp, 16.dp, 16.dp, 16.dp)))
            CurrencyInput(viewModel.wofPrice, "WOF Price", isReadOnly = viewModel.isReadOnly.value)
            StringInput(viewModel.wofProvider, "WOF Provider", modifier = Modifier.padding(top = 8.dp),
                isReadOnly = viewModel.isReadOnly.value)
            if (!viewModel.isReadOnly.value && !viewModel.isExistingData) {
                var buttonText = HistoricalWofViewModel.UPDATE_WOF_BUTTON
                if (viewModel.isHistorical.value)
                    buttonText = HistoricalWofViewModel.HISTORICAL_WOF_BUTTON
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier
                        .padding(0.dp, 32.dp, 0.dp, 0.dp)
                        .fillMaxWidth()) {
                    Button(onClick = viewModel.onRecordClick,
                        contentPadding = PaddingValues(8.dp)) {
                        Text(buttonText, fontSize = 20.sp, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}