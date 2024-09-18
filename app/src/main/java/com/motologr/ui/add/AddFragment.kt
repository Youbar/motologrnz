package com.motologr.ui.add

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.shape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.motologr.R
import com.motologr.databinding.FragmentAddBinding
import com.motologr.data.DataManager
import com.motologr.ui.ellipsis.addons.ArtPackSquare
import com.motologr.ui.ellipsis.addons.PurchasesLazyColumn
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
            ViewModelProvider(this).get(AddViewModel::class.java)

        _binding = FragmentAddBinding.inflate(inflater, container, false)

        val root: View = binding.root

        DataManager.updateTitle(activity, "Record/Update Details")

        val buttonFuel: View = binding.buttonFuel
        buttonFuel.setOnClickListener() {
            findNavController().navigate(R.id.action_nav_add_to_nav_fuel)
        }

        val buttonInsurance: View = binding.buttonInsurance
        buttonInsurance.setOnClickListener() {
            findNavController().navigate(R.id.action_nav_add_to_nav_insurance)
        }

        val buttonRepair: View = binding.buttonRepair
        buttonRepair.setOnClickListener() {
            findNavController().navigate(R.id.action_nav_add_to_nav_repair)
        }

        val buttonService: View = binding.buttonService
        buttonService.setOnClickListener() {
            findNavController().navigate(R.id.action_nav_add_to_nav_service)
        }

        val buttonWof: View = binding.buttonWOF
        buttonWof.setOnClickListener() {
            findNavController().navigate(R.id.action_nav_add_to_nav_wof)
        }

        val buttonReg: View = binding.buttonReg
        buttonReg.setOnClickListener() {
            findNavController().navigate(R.id.action_nav_add_to_nav_reg)
        }

        val buttonRUC : View = binding.buttonRuc
        val isDisplayRUC = DataManager.returnActiveVehicle()?.isUseRoadUserCharges
        if (isDisplayRUC == null || !isDisplayRUC) {
            buttonRUC.isVisible = false
            binding.textWofReg.text = "Update registration:"
        } else {
            buttonRUC.setOnClickListener {
                findNavController().navigate(R.id.nav_rucs)
            }
        }

        val composeView = root.findViewById<ComposeView>(R.id.compose_view_add)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AddOrUpdateList()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

object AddFragmentComposableConsts {
    // Fuel
    val fuelCardTitle = "Fuel"
    val fuelCardText = "Record the purchase of fuel."
    // Compliance
    val complianceCardTitle = "Compliance"
    val complianceCardText = "Update your vehicle's warrant of fitness, registration, or road user charges."
    // Mechanical
    val mechanicalCardTitle = "Mechanical"
    val mechanicalCardText = "Record a service or repair on your vehicle."
    // Insurance
    val insuranceCardTitle = "Insurance"
    val insuranceCardText = "Add an insurance policy for your vehicle."

}

@Preview
@Composable
fun AddOrUpdateList() {
    LazyColumn {
        item {
            AddFragmentCard(AddFragmentComposableConsts.fuelCardTitle,
                AddFragmentComposableConsts.fuelCardText)
        }
        item {
            AddFragmentCard(AddFragmentComposableConsts.complianceCardTitle,
                AddFragmentComposableConsts.complianceCardText)
        }
        item {
            AddFragmentCard(AddFragmentComposableConsts.mechanicalCardTitle,
                AddFragmentComposableConsts.mechanicalCardText)
        }
        item {
            AddFragmentCard(AddFragmentComposableConsts.insuranceCardTitle,
                AddFragmentComposableConsts.insuranceCardText)
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