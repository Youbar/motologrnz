package com.motologr.ui.add.service

import androidx.lifecycle.ViewModelProvider
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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.preference.PreferenceManager
import com.motologr.R
import com.motologr.databinding.FragmentServiceBinding
import com.motologr.data.DataManager
import com.motologr.data.EnumConstants
import com.motologr.data.objects.maint.Service
import com.motologr.ui.compose.CurrencyInput
import com.motologr.ui.compose.DatePickerModal
import com.motologr.ui.compose.EditDeleteFABs
import com.motologr.ui.compose.MultiLineStringInput
import com.motologr.ui.compose.SaveFAB
import com.motologr.ui.compose.StringInput
import com.motologr.ui.compose.WarningDialog
import com.motologr.ui.theme.AppTheme

class ServiceFragment : Fragment() {

    private var _binding: FragmentServiceBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val serviceViewModel =
            ViewModelProvider(this)[ServiceViewModel::class.java]

        _binding = FragmentServiceBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val bundle: Bundle? = arguments
        val logPos: Int? = arguments?.getInt("position")

        var defaultProvider = ""
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
        if (sharedPref != null) {
            defaultProvider =
                sharedPref.getString(getString(R.string.default_mechanic_key), "").toString()
        }

        serviceViewModel.displayToastMessage = { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
                .show()
        }
        serviceViewModel.initServiceViewModel(defaultProvider)
        serviceViewModel.navigateToVehicle = {
            findNavController().navigate(R.id.action_nav_service_to_nav_vehicle_1, null, NavOptions.Builder()
                .setPopUpTo(R.id.nav_vehicle_1, true).build())
        }

        if (logPos != null) {
            DataManager.updateTitle(activity, "View Service")
            val service: Service = DataManager.returnActiveVehicle()?.returnLoggableByPosition(logPos)!! as Service
            serviceViewModel.setViewModelToReadOnly(service)
        } else {
            DataManager.updateTitle(activity, "Record Service")
        }

        val composeView = root.findViewById<ComposeView>(R.id.compose_view_service)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    Column {
                        LazyColumn {
                            item {
                                ServiceLoggingInterface(serviceViewModel)
                                if (serviceViewModel.isExistingData && serviceViewModel.isReadOnly.value)
                                    EditDeleteFABs(
                                        serviceViewModel.onDeleteClick,
                                        serviceViewModel.onEditClick
                                    )
                                else if (serviceViewModel.isExistingData && !serviceViewModel.isReadOnly.value)
                                    SaveFAB(serviceViewModel.onSaveClick)
                                if (serviceViewModel.isDisplayDeleteDialog.value) {
                                    WarningDialog(
                                        serviceViewModel.onDismissClick,
                                        serviceViewModel.onConfirmClick,
                                        "Delete Record",
                                        "Are you sure you want to delete this record? The deletion is irreversible."
                                    )
                                }
                            }
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
fun ServiceLoggingInterface(viewModel: ServiceViewModel) {
    OutlinedCard(modifier = Modifier
        .padding(16.dp, 8.dp, 16.dp, 8.dp)
        .border(1.dp, MaterialTheme.colorScheme.secondary, shape)
        .height(IntrinsicSize.Min)) {
        Column(modifier = Modifier
            .padding(16.dp, 8.dp, 16.dp, 8.dp)
            .height(IntrinsicSize.Min)){
            Text(viewModel.serviceCardTitle, fontSize = 24.sp,
                modifier = Modifier
                    .padding(PaddingValues(0.dp, 0.dp))
                    .fillMaxWidth(),
                lineHeight = 1.em,
                textAlign = TextAlign.Center)
            DatePickerModal(viewModel.serviceDate, "Service Date", true, viewModel.isReadOnly.value)
            CurrencyInput(viewModel.servicePrice, "Service Price", isReadOnly = viewModel.isReadOnly.value)
            RowOfServiceTypes(viewModel.isOilChangeChecked, viewModel.isGeneralChecked, viewModel.isFullChecked,
                viewModel.onBoxChecked, viewModel.isReadOnly.value)
            StringInput(viewModel.serviceProvider, "Service Provider", isReadOnly = viewModel.isReadOnly.value)
            MultiLineStringInput(viewModel.serviceComments, "Service Comments (Optional)",
                modifier = Modifier.defaultMinSize(minHeight = 120.dp), isReadOnly = viewModel.isReadOnly.value)

            if (!viewModel.isReadOnly.value && !viewModel.isExistingData) {
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
}

@Composable
fun RowOfServiceTypes(isMinorChecked : MutableState<Boolean>, isMajorChecked : MutableState<Boolean>,
                      isCriticalChecked : MutableState<Boolean>,
                      onBoxChecked: (Int) -> Unit, isReadOnly: Boolean) {
    Row {
        Column {
            ServiceTypeCheckbox(isMinorChecked, "Oil Change", onBoxChecked, EnumConstants.ServiceType.OilChange.ordinal, isReadOnly)
            ServiceTypeCheckbox(isMajorChecked, "General Service", onBoxChecked, EnumConstants.ServiceType.General.ordinal, isReadOnly)
            ServiceTypeCheckbox(isCriticalChecked, "Full Service", onBoxChecked, EnumConstants.ServiceType.Full.ordinal, isReadOnly)
        }
    }
}

@Composable
fun ServiceTypeCheckbox(checkboxBoolean : MutableState<Boolean>, checkboxText : String, onBoxChecked : (Int) -> Unit, serviceTypeId : Int, isReadOnly : Boolean) {
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
                onBoxChecked(serviceTypeId)
            },
            enabled = !isReadOnly
        )
    }
}