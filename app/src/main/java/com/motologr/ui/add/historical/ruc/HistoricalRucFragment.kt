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
import com.motologr.data.objects.ruc.Ruc
import com.motologr.databinding.FragmentHistoricalRucBinding
import com.motologr.ui.compose.CurrencyInput
import com.motologr.ui.compose.DatePickerModal
import com.motologr.ui.compose.SliderWithUnits
import com.motologr.ui.theme.AppTheme

class HistoricalRucFragment : Fragment() {
    private var _binding: FragmentHistoricalRucBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val historicalRucViewModel =
            ViewModelProvider(this)[HistoricalRucViewModel::class.java]

        historicalRucViewModel.displayToastMessage = { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
                .show()
        }

        historicalRucViewModel.navigateToVehicle = { findNavController().navigate(R.id.nav_vehicle_1) }

        _binding = FragmentHistoricalRucBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val composeView = root.findViewById<ComposeView>(R.id.compose_view_historical_ruc)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    HistoricalRucCard(historicalRucViewModel.registrationPrice, historicalRucViewModel.sliderPosition,
                        historicalRucViewModel.selectedDate, historicalRucViewModel.onClick)
                }
            }
        }

        return root
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricalRucCard(rucPrice: MutableState<String>, sliderPosition: MutableState<Float>,
                      selectedDate: MutableState<String>, onSaveClick: () -> Unit = {}) {
    OutlinedCard(modifier = Modifier
        .padding(16.dp, 8.dp, 16.dp, 8.dp)
        .border(1.dp, MaterialTheme.colorScheme.secondary, shape)) {
        Column(modifier = Modifier
            .padding(16.dp, 8.dp, 16.dp, 8.dp)
            .height(IntrinsicSize.Min)){
            Text("Historical Road User Charges", fontSize = 24.sp,
                modifier = Modifier
                    .padding(PaddingValues(0.dp, 0.dp))
                    .fillMaxWidth(),
                lineHeight = 1.em,
                textAlign = TextAlign.Center)
            DatePickerModal(selectedDate, "Purchase Date")
            HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(PaddingValues(16.dp, 16.dp, 16.dp, 16.dp)))
            SliderWithUnits(rucPrice, sliderPosition, 19, "unit(s)", Ruc.calculateRucPriceLambda)
            CurrencyInput(rucPrice, "Purchase Price")
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