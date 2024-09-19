package com.motologr.ui.add

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.shape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.fragment.findNavController
import com.motologr.R
import com.motologr.databinding.FragmentAddBinding
import com.motologr.data.DataManager
import com.motologr.ui.theme.AppTheme

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val addViewModel =
            ViewModelProvider(this)[AddViewModel::class.java]

        _binding = FragmentAddBinding.inflate(inflater, container, false)

        val root: View = binding.root

        DataManager.updateTitle(activity, "Record/Update Details")

        val fuelNav = { findNavController().navigate(R.id.action_nav_add_to_nav_fuel) }
        val wofNav = { findNavController().navigate(R.id.action_nav_add_to_nav_wof) }
        val regNav = { findNavController().navigate(R.id.action_nav_add_to_nav_reg) }
        val rucNav = { findNavController().navigate(R.id.nav_rucs) }
        val repairNav = { findNavController().navigate(R.id.action_nav_add_to_nav_repair) }
        val serviceNav = { findNavController().navigate(R.id.action_nav_add_to_nav_service) }
        val insuranceNav = { findNavController().navigate(R.id.action_nav_add_to_nav_insurance) }

        val composeView = root.findViewById<ComposeView>(R.id.compose_view_add)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    if (addViewModel.isDefaultListVisible)
                        AddOrUpdateList(fuelNav, addViewModel.onComplianceCardClicked,
                            addViewModel.onMechanicalCardClicked, insuranceNav)
                    if (addViewModel.isComplianceListVisible)
                        AddOrUpdateCompliance(addViewModel.onBackCardClicked, wofNav, regNav, rucNav, addViewModel.isRucsVisible)
                    if (addViewModel.isMechanicalListVisible)
                        AddOrUpdateMechanical(addViewModel.onBackCardClicked, repairNav, serviceNav)
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

object AddFragmentComposableConstants {
    // Fuel
    const val fuelCardTitle = "Fuel"
    const val fuelCardText = "Record the purchase of fuel."

    // Compliance
    const val complianceCardTitle = "Compliance"
    const val complianceCardText = "Update your vehicle's warrant of fitness, registration, or road user charges."

    const val wofCardTitle = "Warrant of Fitness"
    const val wofCardText = "Update your vehicle's warrant of fitness."

    const val regCardTitle = "Registration"
    const val regCardText = "Update your vehicle's registration."

    const val rucCardTitle = "Road User Charges"
    const val rucCardText = "Update your vehicle's road user charges."

    // Mechanical
    const val mechanicalCardTitle = "Mechanical"
    const val mechanicalCardText = "Record a service or repair on your vehicle."

    const val repairCardTitle = "Repair"
    const val repairCardText = "Record a repair on your vehicle."

    const val serviceCardTitle = "Service"
    const val serviceCardText = "Record a service on your vehicle."

    // Insurance
    const val insuranceCardTitle = "Insurance"
    const val insuranceCardText = "Add an insurance policy for your vehicle."
}

@Preview
@Composable
fun AddOrUpdateList(fuelNav : () -> Unit = {},
                    onComplianceCardClicked : () -> Unit = {},
                    onMechanicalCardClicked : () -> Unit = {},
                    insuranceNav : () -> Unit = {}) {
    LazyColumn {
        item {
            AddFragmentCard(AddFragmentComposableConstants.fuelCardTitle,
                AddFragmentComposableConstants.fuelCardText,
                fuelNav)
        }
        item {
            AddFragmentCard(AddFragmentComposableConstants.complianceCardTitle,
                AddFragmentComposableConstants.complianceCardText,
                onComplianceCardClicked)
        }
        item {
            AddFragmentCard(AddFragmentComposableConstants.mechanicalCardTitle,
                AddFragmentComposableConstants.mechanicalCardText,
                onMechanicalCardClicked)
        }
        item {
            AddFragmentCard(AddFragmentComposableConstants.insuranceCardTitle,
                AddFragmentComposableConstants.insuranceCardText,
                insuranceNav)
        }
    }
}

@Preview
@Composable
fun AddOrUpdateCompliance(backButtonOnClick : () -> Unit = {},
                          wofNav : () -> Unit = {},
                          regNav : () -> Unit = {},
                          rucNav : () -> Unit = {},
                          isRucsVisible : Boolean = true) {
    LazyColumn {
        item {
            BackButtonCard(backButtonOnClick)
        }
        item {
            AddFragmentCard(AddFragmentComposableConstants.wofCardTitle,
                AddFragmentComposableConstants.wofCardText,
                wofNav)
        }
        item {
            AddFragmentCard(AddFragmentComposableConstants.regCardTitle,
                AddFragmentComposableConstants.regCardText,
                regNav)
        }
        if (isRucsVisible) {
            item {
                AddFragmentCard(AddFragmentComposableConstants.rucCardTitle,
                    AddFragmentComposableConstants.rucCardText,
                    rucNav)
            }
        }
    }
}

@Preview
@Composable
fun AddOrUpdateMechanical(backButtonOnClick : () -> Unit = {},
                          repairNav : () -> Unit = {},
                          serviceNav : () -> Unit = {}) {
    LazyColumn {
        item {
            BackButtonCard(backButtonOnClick)
        }
        item {
            AddFragmentCard(AddFragmentComposableConstants.repairCardTitle,
                AddFragmentComposableConstants.repairCardText,
                repairNav)
        }
        item {
            AddFragmentCard(AddFragmentComposableConstants.serviceCardTitle,
                AddFragmentComposableConstants.serviceCardText,
                serviceNav)
        }
    }
}

@Composable
fun AddFragmentCard(titleText : String, contentText : String, onClick: () -> Unit = {}) {
    Card(modifier = Modifier
        .padding(16.dp, 8.dp, 16.dp, 8.dp)
        .border(1.dp, MaterialTheme.colorScheme.secondary, shape),
        onClick = onClick) {
        Text(titleText, fontSize = 5.em,
            modifier = Modifier
                .padding(PaddingValues(0.dp, 0.dp))
                .fillMaxWidth(),
            lineHeight = 1.em,
            textAlign = TextAlign.Center)
        Text(contentText, fontSize = 2.5.em,
            modifier = Modifier
                .padding(PaddingValues(8.dp, 4.dp))
                .fillMaxWidth(),
            lineHeight = 1.em,
            textAlign = TextAlign.Center)
    }
}

@Composable
fun BackButtonCard(onClick: () -> Unit = {}) {
    Card(modifier = Modifier
        .padding(16.dp, 8.dp, 16.dp, 8.dp)
        .border(1.dp, MaterialTheme.colorScheme.secondary, shape),
        onClick = onClick) {
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp, 16.dp, 8.dp),
            horizontalArrangement = Arrangement.Center) {
            Image(painter = painterResource(R.drawable.ic_add_back_32), null,
                modifier = Modifier
                    .scale(scaleX = -1f, scaleY = 1f))
            Text("Back", fontSize = 5.em,
                modifier = Modifier
                    .height(32.dp)
                    .wrapContentHeight(),
                lineHeight = 1.em,
                textAlign = TextAlign.Center)
        }
    }
}