package com.motologr.ui.insurance.insurance_bill_manage

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
import com.motologr.databinding.FragmentInsuranceBillManageBinding
import com.motologr.ui.theme.AppTheme

class InsuranceBillManageFragment : Fragment() {
    private var _binding: FragmentInsuranceBillManageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val activeVehicle = DataManager.returnActiveVehicle()!!

        val viewModel = ViewModelProvider(this)[InsuranceBillManageViewModel::class.java]

        _binding = FragmentInsuranceBillManageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val composeView = root.findViewById<ComposeView>(R.id.compose_view_insurance_bill_manage)
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