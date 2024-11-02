package com.motologr.ui.insurance.insurance_bills

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.ViewModelProvider
import com.motologr.R
import com.motologr.data.DataManager
import com.motologr.databinding.FragmentInsurancePolicyBillsBinding
import com.motologr.ui.theme.AppTheme

class InsurancePolicyBillsFragment : Fragment() {
    private var _binding: FragmentInsurancePolicyBillsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val activeVehicle = DataManager.returnActiveVehicle()!!

        val viewModel = ViewModelProvider(this)[InsurancePolicyBillsViewModel::class.java]

        _binding = FragmentInsurancePolicyBillsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val composeView = root.findViewById<ComposeView>(R.id.compose_view_insurance_policy_bills)
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