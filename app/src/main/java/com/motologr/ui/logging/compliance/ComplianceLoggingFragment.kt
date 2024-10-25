package com.motologr.ui.logging.compliance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.motologr.R
import com.motologr.data.DataHelper
import com.motologr.data.DataManager
import com.motologr.data.EnumConstants
import com.motologr.databinding.FragmentComplianceLoggingBinding
import com.motologr.ui.theme.AppTheme
import java.math.BigDecimal
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.fragment.findNavController

class ComplianceLoggingFragment : Fragment() {
    private var _binding: FragmentComplianceLoggingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val navigateToCompliance = { loggableId : Int, classId : Int ->
        val bundle = Bundle()
        bundle.putInt("loggableId", loggableId)

        when (classId) {
            EnumConstants.LoggableType.WOF.id -> findNavController().navigate(R.id.nav_historical_wof, bundle)
            EnumConstants.LoggableType.Reg.id -> findNavController().navigate(R.id.nav_historical_reg, bundle)
            EnumConstants.LoggableType.Ruc.id -> findNavController().navigate(R.id.nav_historical_rucs, bundle)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val activeVehicle = DataManager.returnActiveVehicle()!!
        val viewModel = ViewModelProvider(this)[ComplianceLoggingViewModel::class.java]

        _binding = FragmentComplianceLoggingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val complianceLogs = activeVehicle.returnComplianceLogs()

        val composeView = root.findViewById<ComposeView>(R.id.compose_view_compliance_logging)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text("Compliance Logs",
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp,
                            fontWeight =  FontWeight.Bold)
                        LazyColumn {
                            items(complianceLogs) { loggable ->
                                val sortableDate = DataHelper.formatNumericalDateFormat(loggable.sortableDate)
                                ComplianceCard(loggable.id, loggable.classId, sortableDate, loggable.unitPrice,
                                    navigateToCompliance)
                            }
                        }
                    }
                }
            }
        }

        return root
    }
}

@Preview
@Composable
fun ComplianceCard(loggableId : Int = 0,
        complianceClassId : Int = 0,
        complianceDate : String = "14/05/2024",
        price : BigDecimal = 68f.toBigDecimal(),
        onClick : (Int, Int) -> Unit = { _, _ -> }) {
    OutlinedCard(
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .height(IntrinsicSize.Min),
        onClick = { onClick(loggableId, complianceClassId) }
    ) {
        var complianceIconResourceId = R.drawable.ic_log_car_wof_16
        var complianceText = "WOF"

        if (complianceClassId == EnumConstants.LoggableType.Reg.id) {
            complianceIconResourceId = R.drawable.ic_log_car_reg_16
            complianceText = "REG"
        } else if (complianceClassId == EnumConstants.LoggableType.Ruc.id) {
            complianceIconResourceId = R.drawable.ic_log_car_ruc_16
            complianceText = "RUC"
        }

        val cardFontSize = 20.sp
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly) {
            Icon(painter = painterResource(id = complianceIconResourceId), null,
                modifier = Modifier.size(32.dp))
            Text(complianceText, fontSize = cardFontSize)
            Text(complianceDate, fontSize = cardFontSize)
            Text("$${DataHelper.roundToTwoDecimalPlaces(price)}", fontSize = cardFontSize)
        }
    }
}