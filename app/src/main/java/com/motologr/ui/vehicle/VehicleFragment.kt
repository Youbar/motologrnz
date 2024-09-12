package com.motologr.ui.vehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.motologr.R
import com.motologr.databinding.FragmentVehicleBinding
import com.motologr.data.DataManager
import com.motologr.data.billing.BillingClientHelper
import com.motologr.data.objects.vehicle.Vehicle
import com.motologr.ui.theme.AppTheme
import java.math.RoundingMode
import java.text.DecimalFormat

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
        val vehicleViewModel =
            ViewModelProvider(this).get(VehicleViewModel::class.java)

        _binding = FragmentVehicleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (!DataManager.isVehicles())
            findNavController().navigate(R.id.nav_plus)

        val carName: TextView = binding.textCar
        val WOFDue: TextView = binding.textWOFDue
        val RegDue: TextView = binding.textRegDue
        val Odometer: TextView = binding.textOdometer
        val Insurer: TextView = binding.textInsurer
        val InsurerDate: TextView = binding.textInsurerDate
        val ApproxCostsTitle: TextView = binding.textApproxCostsTitle
        val ApproxCosts: TextView = binding.textApproxCosts

        val vehicle : Vehicle? = DataManager.returnActiveVehicle()

        if (vehicle != null) {
            DataManager.updateTitle(activity, vehicle.brandName + " " + vehicle.modelName)

            val vehicleText = vehicle.brandName + " " + vehicle.modelName + " | " + vehicle.year.toString()

            var expiryWOF: String = vehicle.returnWofExpiry()
            expiryWOF = "Next WOF: $expiryWOF"

            var regExpiry: String = vehicle.returnRegExpiry()
            regExpiry = "Next Reg: $regExpiry"

            val odometer: String = "Last Odometer Reading: " + vehicle.getLatestOdometerReading().toString() + " km"

            var insurer: String
            var insurerDate: String

            val df = DecimalFormat("0.00")
            df.roundingMode = RoundingMode.CEILING

            if (vehicle.hasCurrentInsurance()) {
                var insurance = vehicle.returnLatestInsurancePolicy()

                insurer = insurance.insurer
                insurer = "You are with $insurer insurance"

                insurerDate = "and your next bill of $${df.format(insurance.billing)} is due "
                insurerDate += insurance.getNextBillingDateString()
            } else {
                insurer = "You do not have a registered insurer"
                insurerDate = ""
            }

            var odometerVisible = false
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(this.requireContext())

            if (sharedPref != null) {
                odometerVisible = sharedPref.getBoolean(getString(R.string.fuel_consumption_key), false)
            }

                val approxCosts : String = "$" + df.format(vehicle.returnExpensesWithinFinancialYear())

            vehicleViewModel.textVehicle.observe(viewLifecycleOwner) {
                carName.text = vehicleText
            }
            vehicleViewModel.textWOFDue.observe(viewLifecycleOwner) {
                WOFDue.text = expiryWOF
            }
            vehicleViewModel.textRegDue.observe(viewLifecycleOwner) {
                RegDue.text = regExpiry
            }
            vehicleViewModel.textOdometer.observe(viewLifecycleOwner) {
                Odometer.text = odometer
                Odometer.isVisible = odometerVisible
            }
            vehicleViewModel.textInsurer.observe(viewLifecycleOwner) {
                Insurer.text = insurer
            }
            vehicleViewModel.textInsurerDate.observe(viewLifecycleOwner) {
                InsurerDate.text = insurerDate
            }
            vehicleViewModel.textApproxCostsTitle.observe(viewLifecycleOwner) {
                ApproxCostsTitle.text = it
            }
            vehicleViewModel.textApproxCosts.observe(viewLifecycleOwner) {
                ApproxCosts.text = approxCosts
            }
        }

        val composeView = root.findViewById<ComposeView>(R.id.composeView_vehicle)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    TopOutlinedCards()
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

@Preview
@Composable
fun TopOutlinedCards() {
    Column {
        Row(modifier = Modifier
            .padding(8.dp)
            .height(IntrinsicSize.Min)) {
            val cardModifier = Modifier
                .weight(0.5f)
                .padding(4.dp)
                .fillMaxHeight()

            ComplianceCard(cardModifier = cardModifier)
            InsuranceCard(cardModifier = cardModifier)
        }

        Row(modifier = Modifier
            .padding(8.dp, 0.dp, 8.dp, 8.dp)
            .height(IntrinsicSize.Min)) {
            val cardModifier = Modifier
                .weight(0.5f)
                .padding(4.dp, 0.dp, 4.dp, 4.dp)
                .fillMaxHeight()

            ExpensesCard(cardModifier = cardModifier)
        }
    }
}

@Composable
fun ComplianceCard(cardModifier: Modifier) {
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
                Text(
                    text = "ODO",
                    modifier = Modifier
                        .padding(16.dp, 4.dp, 0.dp, 8.dp),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
            }
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                Text(
                    text = "10/Sep/9999",
                    modifier = Modifier
                        .padding(0.dp, 8.dp, 16.dp, 0.dp),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Right,
                )
                Text(
                    text = "10/Sep/9999",
                    modifier = Modifier
                        .padding(0.dp, 4.dp, 16.dp, 0.dp),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Right,
                )
                Text(
                    text = "999999 km",
                    modifier = Modifier
                        .padding(0.dp, 4.dp, 16.dp, 8.dp),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Right,
                )
            }
        }
    }
}

@Composable
fun InsuranceCard(cardModifier: Modifier) {
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
                    text = "State",
                    modifier = Modifier
                        .padding(16.dp, 8.dp, 0.dp, 0.dp),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "$15.30",
                    modifier = Modifier
                        .padding(16.dp, 4.dp, 0.dp, 0.dp),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "Next Bill",
                    modifier = Modifier
                        .padding(16.dp, 4.dp, 0.dp, 8.dp),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
            }
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                Text(
                    text = "Comp.",
                    modifier = Modifier
                        .padding(0.dp, 8.dp, 16.dp, 0.dp),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Right,
                )
                Text(
                    text = "Fortnightly",
                    modifier = Modifier
                        .padding(0.dp, 4.dp, 16.dp, 0.dp),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Right,
                )
                Text(
                    text = "365 days",
                    modifier = Modifier
                        .padding(0.dp, 4.dp, 16.dp, 8.dp),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Right,
                )
            }
        }
    }
}

@Composable
fun ExpensesCard(cardModifier: Modifier) {
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
            text = "$150 current",
            modifier = Modifier
                .padding(16.dp, 8.dp, 16.dp, 0.dp),
            fontSize = 14.sp,
            textAlign = TextAlign.Right,
        )
        Text(
            text = "$250 projected",
            modifier = Modifier
                .padding(16.dp, 8.dp, 16.dp, 8.dp),
            fontSize = 14.sp,
            textAlign = TextAlign.Right,
        )
    }
}