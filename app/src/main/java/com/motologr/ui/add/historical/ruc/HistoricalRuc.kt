package com.motologr.ui.add.historical.ruc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.ViewModelProvider
import com.motologr.R
import com.motologr.databinding.FragmentHistoricalRucBinding
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
        val historicalWofViewModel =
            ViewModelProvider(this)[HistoricalRucViewModel::class.java]

        _binding = FragmentHistoricalRucBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val composeView = root.findViewById<ComposeView>(R.id.compose_view_historical_ruc)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                }
            }
        }

        return root
    }
}