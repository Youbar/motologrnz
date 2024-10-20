package com.motologr.ui.ellipsis.addons

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.ViewModelProvider
import com.motologr.R
import com.motologr.ui.theme.AppTheme

class AddonsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_addons, container, false)
        val addonsViewModel = ViewModelProvider(this)[AddonsViewModel::class.java]

        val composeView = view.findViewById<ComposeView>(R.id.compose_view)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PurchasesLazyColumn(addonsViewModel)
            }
        }

        return view
    }
}

@Composable
fun PurchasesLazyColumn(viewModel: AddonsViewModel) {
    AppTheme {
        LazyColumn {
            item {
                ArtPackCard(viewModel.isArtPackPurchaseEnabled,
                    viewModel.artPackButtonText
                ) { viewModel.purchaseArtPack() }
            }
        }
    }
}

@Preview
@Composable
fun PreviewPurchasesLazyColumn(
    isArtPackPurchaseEnabled: Boolean = true,
    artPackButtonText: String = "$2.99"
) {
    AppTheme {
        LazyColumn {
            item {
                ArtPackCard(isArtPackPurchaseEnabled,
                    artPackButtonText
                ) { }
            }
        }
    }
}

@Composable
fun ArtPackCard(
    isArtPackPurchaseEnabled : Boolean,
    artPackButtonText : String,
    onClick: () -> Unit = { }
) {
    Card(modifier = Modifier
        .padding(8.dp)
        .border(1.dp, MaterialTheme.colorScheme.secondary, shape)
        .fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Art Pack", fontSize = 5.em, modifier = Modifier.padding(PaddingValues(0.dp, 4.dp)))
            HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(PaddingValues(16.dp, 0.dp)))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                ArtPackSquare()
                Text("Show your support for MotoLogr NZ by purchasing this art pack for your garage. You will also benefit from an ad-free experience.",
                    modifier = Modifier.weight(1.0f)
                        .padding(PaddingValues(6.dp, 0.dp)),
                    fontSize = 2.5.em)
                Button(onClick = onClick,
                    enabled = isArtPackPurchaseEnabled, contentPadding = PaddingValues(8.dp)) {
                    Text(artPackButtonText, fontSize = 3.em, textAlign = TextAlign.Center)
                }
            }
        }
    }
}


@Composable
fun ArtPackSquare() {
    val imageModifier = Modifier
        .size(40.dp)
        .padding(2.dp)

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(R.drawable.car_prem_convertible), null, modifier = imageModifier)
            Image(painter = painterResource(R.drawable.car_prem_compact), null, modifier = imageModifier)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(R.drawable.car_prem_hatchback), null, modifier = imageModifier)
            Image(painter = painterResource(R.drawable.car_prem_van), null, modifier = imageModifier)
        }
    }
}