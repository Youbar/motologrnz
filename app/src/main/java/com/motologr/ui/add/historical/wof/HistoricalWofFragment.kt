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
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults.shape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
import com.motologr.databinding.FragmentHistoricalWofBinding
import com.motologr.ui.compose.CurrencyInput
import com.motologr.ui.compose.DatePickerModal
import com.motologr.ui.compose.StringInput
import com.motologr.ui.theme.AppTheme

class HistoricalWofFragment : Fragment() {
    private var _binding: FragmentHistoricalWofBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val historicalWofViewModel =
            ViewModelProvider(this)[HistoricalWofViewModel::class.java]

        historicalWofViewModel.displayToastMessage = { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
                .show()
        }

        historicalWofViewModel.navigateToVehicle = { findNavController().navigate(R.id.nav_vehicle_1) }

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
        if (sharedPref != null) {
            val defaultMechanic = sharedPref.getString(getString(R.string.default_mechanic_key), "")
            historicalWofViewModel.wofProvider.value = defaultMechanic ?: ""
        }

        _binding = FragmentHistoricalWofBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val composeView = root.findViewById<ComposeView>(R.id.compose_view_historical_wof)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    HistoricalWofCard(historicalWofViewModel.wofPrice, historicalWofViewModel.wofProvider,
                        historicalWofViewModel.wofDate, historicalWofViewModel.onClick)
                }
            }
        }

        return root
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricalWofCard(wofPrice: MutableState<String>, wofProvider: MutableState<String>,
                      wofDate: MutableState<String>, onSaveClick: () -> Unit = {}) {
    OutlinedCard(modifier = Modifier
        .padding(16.dp, 8.dp, 16.dp, 8.dp)
        .border(1.dp, MaterialTheme.colorScheme.secondary, shape)) {
        Column(modifier = Modifier
            .padding(16.dp, 8.dp, 16.dp, 8.dp)
            .height(IntrinsicSize.Min)){
            Text("Historical WOF", fontSize = 24.sp,
                modifier = Modifier
                    .padding(PaddingValues(0.dp, 0.dp))
                    .fillMaxWidth(),
                lineHeight = 1.em,
                textAlign = TextAlign.Center)
            DatePickerModal(wofDate, "WOF Date")
            HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(PaddingValues(16.dp, 16.dp, 16.dp, 16.dp)))
            CurrencyInput(wofPrice, "WOF Price")
            StringInput(wofProvider, "WOF Provider", modifier = Modifier.padding(top = 8.dp))
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