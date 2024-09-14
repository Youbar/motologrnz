package com.motologr.ui.vehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.motologr.R
import com.motologr.databinding.FragmentVehicleBinding
import com.motologr.data.DataManager
import com.motologr.data.billing.BillingClientHelper
import com.motologr.data.objects.vehicle.Vehicle
import com.motologr.ui.theme.AppTheme

class VehicleFragment : Fragment() {

    private var _binding: FragmentVehicleBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVehicleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (!DataManager.isVehicles())
            findNavController().navigate(R.id.nav_plus)

        val viewModel = ViewModelProvider(this)[VehicleViewModel::class.java]

        viewModel.textVehicle.observe(viewLifecycleOwner) {
            binding.textCar.text = it
        }

        val composeView = root.findViewById<ComposeView>(R.id.composeView_vehicle)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    OutlinedCards(viewModel)
                }
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fab: View = binding.fab

        fab.setOnClickListener { _ ->
            findNavController().navigate(R.id.action_nav_vehicle_1_to_nav_add)
        }

        val car: ImageView = binding.imageCar

        val activeVehicle: Vehicle? = DataManager.returnActiveVehicle()

        if (activeVehicle != null)
            setVehicleImage(activeVehicle.vehicleImage, car)

        car.setOnClickListener { _ ->
            val newVehicleImageId = DataManager.changeActiveVehicleImageId(BillingClientHelper.isArtPackEnabled)

            setVehicleImage(newVehicleImageId, car)
        }
    }

    private fun setVehicleImage(vehicleImageId : Int, car : ImageView) {
        if (vehicleImageId == 0)
            car.setImageResource(R.drawable.car_sedan)
        if (vehicleImageId == 1)
            car.setImageResource(R.drawable.car_suv)
        if (vehicleImageId == 2)
            car.setImageResource(R.drawable.car_truck)
        if (vehicleImageId == 3)
            car.setImageResource(R.drawable.car_prem_hatchback)
        if (vehicleImageId == 4)
            car.setImageResource(R.drawable.car_prem_convertible)
        if (vehicleImageId == 5)
            car.setImageResource(R.drawable.car_prem_van)
        if (vehicleImageId == 6)
            car.setImageResource(R.drawable.car_prem_convertible_2)
        if (vehicleImageId == 7)
            car.setImageResource(R.drawable.car_prem_compact)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@Composable
fun OutlinedCards(viewModel : VehicleViewModel) {
    Column {
        Row(modifier = Modifier
            .padding(8.dp)
            .height(IntrinsicSize.Min)) {
            val cardModifier = Modifier
                .weight(0.5f)
                .padding(4.dp)
                .fillMaxHeight()

            val vehicleWofDt = viewModel.textWOFDue.observeAsState("")
            val vehicleRegDt = viewModel.textRegDue.observeAsState("")
            val trackingFuelConsumption = viewModel.isOdometerDisplayed.observeAsState(false)
            val vehicleOdo = viewModel.textOdometer.observeAsState("")
            ComplianceCard(cardModifier, vehicleWofDt, vehicleRegDt, trackingFuelConsumption, vehicleOdo)

            val policyInsurer = viewModel.textInsurer.observeAsState("")
            val policyCoverage = viewModel.textInsurerCoverage.observeAsState("")
            val policyCost = viewModel.textInsurerCost.observeAsState("")
            val policyCycle = viewModel.textInsurerCycle.observeAsState("")
            val nextChargeText = viewModel.textNextCharge.observeAsState("")
            val daysToNextCharge = viewModel.textInsurerDaysToNextCharge.observeAsState("")
            val hasActivePolicy = viewModel.hasCurrentInsurance.observeAsState(false)
            InsuranceCard(cardModifier, policyInsurer, policyCoverage,
                policyCost, policyCycle, nextChargeText, daysToNextCharge, hasActivePolicy)
        }

        Row(modifier = Modifier
            .padding(8.dp, 0.dp, 8.dp, 8.dp)
            .height(IntrinsicSize.Min)) {
            val cardModifier = Modifier
                .weight(0.5f)
                .padding(4.dp, 0.dp, 4.dp, 4.dp)
                .fillMaxHeight()

            val currentCosts = viewModel.textCurrentCosts.observeAsState("")
            val projectedCosts = viewModel.textProjectedCosts.observeAsState("")
            ExpensesCard(cardModifier, currentCosts, projectedCosts)
        }
    }
}

@Composable
fun ComplianceCard(
    cardModifier: Modifier,
    wofDt : State<String>,
    regDt :  State<String>,
    isOdoReadingVisible : State<Boolean>,
    odoReading : State<String>
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, Color.Black),
        modifier = cardModifier
    ) {
        Text(
            text = "Compliance",
            modifier = Modifier
                .padding(16.dp, 8.dp, 0.dp, 0.dp),
            fontSize = 20.sp
        )
        Row {
            Column {
                Text(
                    text = "WOF",
                    modifier = Modifier
                        .padding(16.dp, 8.dp, 0.dp, 0.dp),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "REG",
                    modifier = Modifier
                        .padding(16.dp, 4.dp, 0.dp, 0.dp),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
                if (isOdoReadingVisible.value) {
                    Text(
                        text = "ODO",
                        modifier = Modifier
                            .padding(16.dp, 4.dp, 0.dp, 8.dp),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                Text(
                    text = wofDt.value,
                    modifier = Modifier
                        .padding(0.dp, 8.dp, 16.dp, 0.dp),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Right,
                )
                Text(
                    text = regDt.value,
                    modifier = Modifier
                        .padding(0.dp, 4.dp, 16.dp, 0.dp),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Right,
                )
                if (isOdoReadingVisible.value) {
                    Text(
                        text = odoReading.value,
                        modifier = Modifier
                            .padding(0.dp, 4.dp, 16.dp, 8.dp),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Right,
                    )
                }
            }
        }
    }
}

@Composable
fun InsuranceCard(
    cardModifier: Modifier,
    insurer : State<String>,
    coverage : State<String>,
    amount : State<String>,
    cycle : State<String>,
    nextChargeText : State<String>,
    nextCharge : State<String>,
    hasActivePolicy : State<Boolean>
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, Color.Black),
        modifier = cardModifier
    ) {
        Text(
            text = "Insurance",
            modifier = Modifier
                .padding(16.dp, 8.dp, 0.dp, 0.dp),
            fontSize = 20.sp
        )
        Row {
            Column {
                Text(
                    text = insurer.value,
                    modifier = Modifier
                        .padding(16.dp, 8.dp, 0.dp, 0.dp),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
                if (hasActivePolicy.value) {
                    Text(
                        text = amount.value,
                        modifier = Modifier
                            .padding(16.dp, 4.dp, 0.dp, 0.dp),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = nextChargeText.value,
                        modifier = Modifier
                            .padding(16.dp, 4.dp, 0.dp, 8.dp),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                if (hasActivePolicy.value) {
                    Text(
                        text = coverage.value,
                        modifier = Modifier
                            .padding(0.dp, 8.dp, 16.dp, 0.dp),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Right,
                    )
                    Text(
                        text = cycle.value,
                        modifier = Modifier
                            .padding(0.dp, 4.dp, 16.dp, 0.dp),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Right,
                    )
                    Text(
                        text = nextCharge.value,
                        modifier = Modifier
                            .padding(0.dp, 4.dp, 16.dp, 8.dp),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Right,
                    )
                }
            }
        }
    }
}

@Composable
fun ExpensesCard(
    cardModifier: Modifier,
    currentAmount : State<String>,
    projectedAmount : State<String>
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, Color.Black),
        modifier = cardModifier
    ) {
        Text(
            text = "Expenses",
            modifier = Modifier
                .padding(16.dp, 8.dp, 0.dp, 0.dp),
            fontSize = 20.sp
        )
        Text(
            text = currentAmount.value,
            modifier = Modifier
                .padding(16.dp, 8.dp, 16.dp, 0.dp),
            fontSize = 14.sp,
            textAlign = TextAlign.Right,
        )
        Text(
            text = projectedAmount.value,
            modifier = Modifier
                .padding(16.dp, 8.dp, 16.dp, 8.dp),
            fontSize = 14.sp,
            textAlign = TextAlign.Right,
        )
    }
}