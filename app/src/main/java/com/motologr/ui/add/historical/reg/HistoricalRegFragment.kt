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
import com.motologr.data.objects.reg.Reg
import com.motologr.data.objects.vehicle.Vehicle
import com.motologr.databinding.FragmentHistoricalRegBinding
import com.motologr.ui.compose.DatePickerModal
import com.motologr.ui.compose.CurrencyInput
import com.motologr.ui.compose.SliderWithUnits
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
/*
            val reg: Reg = DataManager.returnActiveVehicle()?.returnLoggableByPosition(logPos)!! as Reg
            historicalRegViewModel.setViewModelToReadOnly(reg)
*/
        } else {
            DataManager.updateTitle(activity, "Update Reg")
            val isHistorical = arguments?.getBoolean("isHistorical") ?: false
/*
            historicalRegViewModel.initViewModel(activeVehicle, isHistorical)
*/
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
                    HistoricalRegCard(historicalRegViewModel.registrationPrice, historicalRegViewModel.sliderPosition,
                        historicalRegViewModel.selectedDate, historicalRegViewModel.onClick)
                }
            }
        }

        return root
    }
}

@Composable
fun HistoricalRegCard(registrationPrice: MutableState<String>, sliderPosition: MutableState<Float>,
                      selectedDate: MutableState<String>, onSaveClick: () -> Unit = {}) {
    OutlinedCard(modifier = Modifier
        .padding(16.dp, 8.dp, 16.dp, 8.dp)
        .border(1.dp, MaterialTheme.colorScheme.secondary, shape)) {
        Column(modifier = Modifier
            .padding(16.dp, 8.dp, 16.dp, 8.dp)
            .height(IntrinsicSize.Min)){
            Text("Historical Registration", fontSize = 24.sp,
                modifier = Modifier
                    .padding(PaddingValues(0.dp, 0.dp))
                    .fillMaxWidth(),
                lineHeight = 1.em,
                textAlign = TextAlign.Center)
            DatePickerModal(selectedDate, "Purchase Date")
            HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(PaddingValues(16.dp, 16.dp, 16.dp, 16.dp)))
            SliderWithUnits(registrationPrice, sliderPosition, 11, "month(s)", Reg.calculateRegistrationLambda)
            CurrencyInput(registrationPrice, "Purchase Price")
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier
                .padding(0.dp, 32.dp, 0.dp, 0.dp)
                .fillMaxWidth()) {
                Button(onClick = onSaveClick, contentPadding = PaddingValues(8.dp)) {
                    Text("Save", fontSize = 3.em, textAlign = TextAlign.Center)
                }
            }
        }
    }
}