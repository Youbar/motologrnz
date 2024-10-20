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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.motologr.BuildConfig
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

    private lateinit var adView : AdView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVehicleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val adView = AdView(requireContext())
        if (BuildConfig.DEBUG)
            adView.adUnitId = "ca-app-pub-3940256099942544/9214589741"
        else
            adView.adUnitId = "ca-app-pub-2605560937669954/2596902508"
        adView.setAdSize(AdSize.BANNER)
        this.adView = adView

        if (!BillingClientHelper.isArtPackEnabled) {
            binding.adCar.removeAllViews()
            binding.adCar.addView(adView)
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        } else {
            adView.isVisible = false
        }

        if (!DataManager.isVehicles())
            findNavController().navigate(R.id.nav_plus)

        var trackingFuelConsumption = false
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this.requireContext())

        if (sharedPref != null)
            trackingFuelConsumption = sharedPref.getBoolean(getString(R.string.fuel_consumption_key), false)

        val viewModel = ViewModelProvider(this)[VehicleViewModel::class.java]
        viewModel.updateActiveVehicle(DataManager.returnActiveVehicle())
        viewModel.updateOdometerView(trackingFuelConsumption)

        viewModel.textVehicle.observe(viewLifecycleOwner) {
            binding.textCar.text = it
        }

        val expensesNavigate = {
            findNavController().navigate(R.id.nav_expenses)
        }
        val composeView = root.findViewById<ComposeView>(R.id.compose_view_vehicle)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    OutlinedCards(viewModel, expensesNavigate)
                }
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activeVehicle: Vehicle = DataManager.returnActiveVehicle() ?: return

        val fab: FloatingActionButton = binding.fab

        if (activeVehicle.isMeetingCompliance())
            fab.setImageResource(android.R.drawable.ic_input_add)
        else
            fab.setImageResource(R.drawable.ic_input_question_mark)

        fab.setOnClickListener { _ ->
            if (activeVehicle.isMeetingCompliance())
                findNavController().navigate(R.id.action_nav_vehicle_1_to_nav_add)
            else
                findNavController().navigate(R.id.nav_compliance)
        }

        val car: ImageView = binding.imageCar

        setVehicleImage(activeVehicle.vehicleImage, car)
        DataManager.updateTitle(requireActivity(), activeVehicle.brandName + " " + activeVehicle.modelName)

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
fun OutlinedCards(viewModel : VehicleViewModel, expensesNavigate : () -> Unit) {
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
            val isRoadUserChargesDisplayed = viewModel.isRoadUserChargesDisplayed.observeAsState(false)
            val roadUserCharges = viewModel.textRoadUserCharges.observeAsState("")
            ComplianceCard(cardModifier, vehicleWofDt, vehicleRegDt, trackingFuelConsumption,
                vehicleOdo, isRoadUserChargesDisplayed, roadUserCharges)

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
            ExpensesCard(cardModifier, currentCosts, projectedCosts, expensesNavigate)
        }
    }
}

@Composable
fun ComplianceCard(
    cardModifier: Modifier,
    wofDt : State<String>,
    regDt :  State<String>,
    isOdoReadingVisible : State<Boolean>,
    odoReading : State<String>,
    isRoadUserChargesDisplayed: State<Boolean>,
    roadUserChargesHeld : State<String>
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
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
                TopTwoColumnText("WOF", TextAlign.Center)
                TopTwoColumnText("REG", TextAlign.Center)
                if (isOdoReadingVisible.value) {
                    TopTwoColumnText("ODO", TextAlign.Center)
                }
                if (isRoadUserChargesDisplayed.value) {
                    TopTwoColumnText("RUC", TextAlign.Center)
                }
            }
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                TopTwoColumnText(wofDt.value, TextAlign.Right)
                TopTwoColumnText(regDt.value, TextAlign.Right)
                if (isOdoReadingVisible.value) {
                    TopTwoColumnText(odoReading.value, TextAlign.Right)
                }
                if (isRoadUserChargesDisplayed.value) {
                    TopTwoColumnText(roadUserChargesHeld.value, TextAlign.Right)
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
                TopTwoColumnText(insurer.value, TextAlign.Center)
                if (hasActivePolicy.value) {
                    TopTwoColumnText(amount.value, TextAlign.Center)
                    TopTwoColumnText(nextChargeText.value, TextAlign.Center)
                }
            }
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                if (hasActivePolicy.value) {
                    TopTwoColumnText(coverage.value, TextAlign.Right)
                    TopTwoColumnText(cycle.value, TextAlign.Right)
                    TopTwoColumnText(nextCharge.value, TextAlign.Right)
                }
            }
        }
    }
}

@Composable
fun TopTwoColumnText(text : String, textAlign : TextAlign) {
    var modifier = Modifier
        .padding(16.dp, 0.dp, 0.dp, 0.dp)

    if (textAlign == TextAlign.Right)
        modifier = Modifier
            .padding(0.dp, 0.dp, 16.dp, 0.dp)

    Text(
        text = text,
        modifier = modifier,
        fontSize = 14.sp,
        textAlign = textAlign,
    )
}

@Composable
fun ExpensesCard(
    cardModifier: Modifier,
    currentAmount : State<String>,
    projectedAmount : State<String>,
    onClick : () -> Unit
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, Color.Black),
        modifier = cardModifier,
        onClick = onClick
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
                .padding(start = 16.dp),
            fontSize = 14.sp,
            textAlign = TextAlign.Right,
        )
        Text(
            text = projectedAmount.value,
            modifier = Modifier
                .padding(start = 16.dp),
            fontSize = 14.sp,
            textAlign = TextAlign.Right,
        )
    }
}